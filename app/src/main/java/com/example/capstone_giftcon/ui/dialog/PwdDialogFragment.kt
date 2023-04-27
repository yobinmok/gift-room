package com.example.capstone_giftcon.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.databinding.DialogPwdBinding
import com.example.capstone_giftcon.ui.viewModel.UserViewModel
import com.google.gson.JsonObject

class PwdDialogFragment: DialogFragment() {

    private var _binding: DialogPwdBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: UserViewModel by activityViewModels()

    private var pwdCheckBool = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPwdBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.pwdCheck.setOnClickListener {
            checkPwd(binding.oldPwd.text.toString())
        }

        binding.saveButton.setOnClickListener {
            if(binding.newPwd.text == binding.checkPwd.text){
                if(!pwdCheckBool)
                    Toast.makeText(
                        requireContext(),
                        "기존 비밀번호 입력 후 확인 버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
                else{
                    postPwd(binding.newPwd.text.toString())
                    Toast.makeText(requireContext(), "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }else
                Toast.makeText(requireContext(), "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        binding.closeButton.setOnClickListener { dismiss() }

        return binding.root
    }

    private fun checkPwd(pwd: String){
        val data = JsonObject()
        data.addProperty("password", pwd)
        sharedViewModel.checkPwd(data)
        sharedViewModel.checkPwdResult.observe(this){
            when(it){
                NetworkResult.SUCCESS ->{
                    Toast.makeText(requireContext(),
                        "올바른 비밀번호 입니다.",
                        Toast.LENGTH_SHORT).show()
                    pwdCheckBool = true
                }
                NetworkResult.FAIL -> {
                    Toast.makeText(requireContext(),
                        "실패하였습니다. 다시 시도해주세요.",
                        Toast.LENGTH_SHORT).show()
                }
                else ->{
                    Toast.makeText(requireContext(),
                        "비밀번호가 틀렸습니다. \n다시 한 번 입력해주세요.",
                        Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun postPwd(pwd: String?) {
        val data = JsonObject()
        data.addProperty("password", pwd)
        sharedViewModel.postProfile(data)
        sharedViewModel.postProfileResult.observe(this) {
            when (it) {
                NetworkResult.SUCCESS -> {
                    Toast.makeText(requireContext(),
                        "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(),
                        "비밀번호를 변경하지 못하였습니다. \n 다시 시도해주세요.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
