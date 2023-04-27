package com.example.capstone_giftcon.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.LoginActivity
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.databinding.DialogDeleteBinding
import com.example.capstone_giftcon.ui.viewModel.UserViewModel

class DeleteDialogFragment: DialogFragment() {

    private var _binding: DialogDeleteBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogDeleteBinding.inflate(inflater, container, false)

        binding.yesButton.setOnClickListener {
            deleteAccount()
            dismiss()
        }
        binding.noButton.setOnClickListener { dismiss() }
        binding.closeButton.setOnClickListener { dismiss() }

        return binding.root
    }

    private fun deleteAccount(){
        sharedViewModel.deleteAccount()
        sharedViewModel.deleteAccountResult.observe(this){
            when(it) {
                NetworkResult.SUCCESS -> {
                    Toast.makeText(requireContext(), "계정이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                } else ->
                    Toast.makeText(requireContext(), "계정이 삭제되지 않았습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}