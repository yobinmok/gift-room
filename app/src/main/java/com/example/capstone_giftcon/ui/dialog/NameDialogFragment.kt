package com.example.capstone_giftcon.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.databinding.DialogNameBinding
import com.example.capstone_giftcon.ui.viewModel.UserViewModel
import com.google.gson.JsonObject

class NameDialogFragment: DialogFragment(){

    private var _binding: DialogNameBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: UserViewModel by activityViewModels()

    private var dupCheck = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DialogNameBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        getName()

        binding.nameCheck.setOnClickListener {
            sharedViewModel.duplicateNameCheck(binding.newName.text.toString())
            sharedViewModel.duplicateNameResult.observe(this) {
                binding.nameWarn.visibility = View.VISIBLE
                dupCheck = when(it){
                    NetworkResult.SUCCESS -> {
                        binding.nameWarn.setTextColor(Color.parseColor("#5876ed"))
                        binding.nameWarn.text = "사용 가능한 이름 입니다."
                        true
                    }
                    NetworkResult.FAIL -> {
                        Log.d("duplicateNameCheck", "Failure")
                        false
                    }
                    else -> {
                        binding.nameWarn.setTextColor(Color.parseColor("#f25f30"))
                        binding.nameWarn.text = "중복된 이름 입니다."
                        false
                    }
                }
            }
        }

        binding.saveButton.setOnClickListener {
            if (dupCheck) {
                val value: String = binding.newName.text.toString()
                postName(value)
                binding.newName.setText("")
                binding.nameWarn.visibility = View.GONE
                dismiss()
            } else
                Toast.makeText(requireContext(), "이름 중복확인을 해주세요.", Toast.LENGTH_SHORT)
                    .show()
        }

        binding.closeButton.setOnClickListener(View.OnClickListener { dismiss() })
        return binding.root
    }

    private fun getName(){
        sharedViewModel.getProfile()
        sharedViewModel.getProfileResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    sharedViewModel.profile.observe(this){ hashMap ->
                        binding.oldName.setText(hashMap["nickname"])
                    }
                }
                else -> Log.d("getName", "Failure / Not successful")
            }
        }
    }

    private fun postName(name: String?) {
        val data = JsonObject()
        data.addProperty("nickname", name)
        sharedViewModel.postProfile(data)
        sharedViewModel.postProfileResult.observe(this) {
            when (it) {
                NetworkResult.SUCCESS -> {
                    Toast.makeText(requireContext(),
                        "이름을 성공적으로 변경하였습니다.", Toast.LENGTH_SHORT).show()
                }
                NetworkResult.ERROR -> {
                    Toast.makeText(requireContext(),
                        "중복된 이름입니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d("postProfile", "Failure or Not Successful")
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}