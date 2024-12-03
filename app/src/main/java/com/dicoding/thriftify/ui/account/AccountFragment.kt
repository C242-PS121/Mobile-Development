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

        binding.logoutButton.setOnClickListener {
            mainViewModel.logout().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("Logout", "Loading...")
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Log.d("Logout", "Logout successful: ${result.data.message}")
                        Toast.makeText(requireContext(), result.data.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), WelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Log.e("Logout", "Error: ${result.error}")
                        showError(result)
                    }
                }
            }
        }

//        binding.logoutButton.setOnClickListener {
//            mainViewModel.logout()
//            mainViewModel.logoutResponse.observe(viewLifecycleOwner) { result ->
//                when (result) {
//                    is Result.Loading -> {
//                        Log.d("Logout", "Loading...")
//                        showLoading(true)
//                    }
//
//                    is Result.Success -> {
//                        showLoading(false)
//                        Log.d("Logout", "Logout successful: ${result.data.message}")
//                        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(intent)
//                        requireActivity().finish()
//                    }
//
//                    is Result.Error -> {
//                        Log.e("Logout", "Error: ${result.error}")
//                        showLoading(false)
//                        showError(result)
//                    }
//                    else -> {
//                        Toast.makeText(requireContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }


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