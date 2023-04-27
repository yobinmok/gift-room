package com.example.capstone_giftcon.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.BoardDetailActivity
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.databinding.DialogCommentDeleteBinding
import com.example.capstone_giftcon.databinding.DialogMycommentBinding
import com.example.capstone_giftcon.ui.viewModel.CommentViewModel

class CommentDeleteDialog(private val commentDialog: MyCommentDialog): DialogFragment() {

    private var _binding: DialogCommentDeleteBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: CommentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogCommentDeleteBinding.inflate(inflater, container, false)

        binding.yesButton.setOnClickListener {
            BoardDetailActivity().deleteComment(commentDialog.commentId, commentDialog.position)
        }
        binding.noButton.setOnClickListener {
            dismiss()
            commentDialog.dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
            commentDialog.dismiss()
        }
        return binding.root
    }

}