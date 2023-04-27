package com.example.capstone_giftcon.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.capstone_giftcon.BoardDetailActivity
import com.example.capstone_giftcon.databinding.ActivityBoardDetailBinding
import com.example.capstone_giftcon.databinding.DialogCommentDeleteBinding
import com.example.capstone_giftcon.databinding.DialogMycommentBinding
import com.example.capstone_giftcon.item.CommentItem
import com.example.capstone_giftcon.ui.viewModel.CommentViewModel

class MyCommentDialog(private val item: CommentItem,
                      private val fragmentManager: FragmentManager,
                      private val detailBinding: ActivityBoardDetailBinding,
                      private val commentPosition: Int): DialogFragment() {

    private var _binding: DialogMycommentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: CommentViewModel by activityViewModels()

    var position: Int = 0
    var commentId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogMycommentBinding.inflate(inflater, container, false)

        position = commentPosition
        commentId = item.b_id.toInt()

        binding.delete.setOnClickListener {
            CommentDeleteDialog(this).show(fragmentManager, "commentDeleteDialog")
        }
        binding.update.setOnClickListener {
            sharedViewModel.updateBoolean(true)
            detailBinding.comment.setText(item.b_contents)
            position = commentPosition
            dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}