package com.dicoding.thriftify.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dicoding.thriftify.R
import com.dicoding.thriftify.data.Result
import com.dicoding.thriftify.databinding.FragmentAccountBinding
import com.dicoding.thriftify.ui.main.MainViewModel
import com.dicoding.thriftify.ui.welcome.WelcomeActivity
import com.dicoding.thriftify.utils.ViewModelFactory

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel.isLogin) {
                val userId = userModel.userId
                mainViewModel.getUserById(userId).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            val user = result.data
                            Log.d("AccountFragment", "User data: $user")
                            binding.tvFullname.text = user.data.fullname
                            binding.tvPhone.text = user.data.phone
                            binding.tvEmail.text = user.data.email
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    mainViewModel.logout().observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(
                                    requireContext(),
                                    result.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }

                            is Result.Error -> {
                                showLoading(false)
                                showError(result)
                            }
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(error: Result.Error) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.error_title))
            setMessage(error.error)
            setPositiveButton(getString(R.string.ok)) { _, _ -> }
            create()
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}