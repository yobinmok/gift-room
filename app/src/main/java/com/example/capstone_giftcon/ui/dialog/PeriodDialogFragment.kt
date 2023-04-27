package com.example.capstone_giftcon.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.databinding.DialogPeriodBinding
import com.example.capstone_giftcon.ui.viewModel.UserViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class PeriodDialogFragment(private val pushOnOff: Boolean): DialogFragment() {

    private var _binding: DialogPeriodBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPeriodBinding.inflate(inflater, container, false)

        getPush()

        binding.saveButton.setOnClickListener {
            postPush()
            dismiss()
        }
        binding.closeButton.setOnClickListener { dismiss() }
        return binding.root
    }

    private fun getPush(){
        sharedViewModel.getPush()
        sharedViewModel.getPushResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    val timeSpan = sharedViewModel.timeSpan
                    binding.period1.setText(timeSpan.value!!.get(0).toString())
                    binding.period2.setText(timeSpan.value!!.get(1).toString())
                    binding.period3.setText(timeSpan.value!!.get(2).toString())
                }else ->{
                    Toast.makeText(requireContext(), "기간 정보를 불러오는 데 실패했습니다. \n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun postPush(){
        val periodArray = JsonArray()
        periodArray.add(binding.period1.text.toString())
        periodArray.add(binding.period1.text.toString())
        periodArray.add(binding.period1.text.toString())

        val data = JsonObject()
        data.addProperty("service", pushOnOff)
        data.add("timespan", periodArray)

        sharedViewModel.postPush(data)
        sharedViewModel.postPushResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {}
                else -> {
                    Toast.makeText(requireContext(), "기간 정보를 저장하는 데 실패했습니다. \n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}