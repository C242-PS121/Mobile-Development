package com.dicoding.thriftify.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.pref.UserModel
import com.dicoding.thriftify.databinding.ActivityLoginBinding
import com.dicoding.thriftify.ui.main.MainActivity
import com.dicoding.thriftify.utils.ViewModelFactory
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.data.remote.request.LoginRequest
import com.dicoding.thriftify.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (!validateEmail(email) || !validatePassword(password)) {
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            viewModel.login(loginRequest)
            viewModel.loginResponse.observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val user = UserModel(
                            email,
                            result.data.data.userId,
                            result.data.data.accessToken,
                            result.data.data.refreshToken,
                            true
                        )
                        viewModel.saveSession(user)
                        viewModel.saveSession(user)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.success_title))
                            setMessage(getString(R.string.login_success_message))
                            setPositiveButton(getString(R.string.continue_button)) { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result)
                    }
                }
            }
        }
    }

    private fun showError(error: Result.Error) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error_title))
            setMessage(error.error)
            setPositiveButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            binding.edLoginEmail.error = getString(R.string.email_empty)
            return false
        }
        if (!isValidEmail(email)) {
            binding.edLoginEmail.error = getString(R.string.email_invalid)
            return false
        }
        binding.edLoginEmail.error = null
        return true
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) {
            binding.edLoginPassword.error = getString(R.string.password_empty)
            return false
        }
        if (password.length < 8) {
            binding.edLoginPassword.error = getString(R.string.password_less_than_8)
            return false
        }
        binding.edLoginPassword.error = null
        return true
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val loginText = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                loginText
            )
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}