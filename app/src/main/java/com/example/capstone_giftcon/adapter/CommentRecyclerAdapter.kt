package com.example.capstone_giftcon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.databinding.CommentItemBinding
import com.example.capstone_giftcon.item.CommentItem
import com.example.capstone_giftcon.ui.dialog.MyCommentDialog

class CommentRecyclerAdapter(private val clickListener: (CommentItem, Int) -> Unit): ListAdapter<CommentItem, CommentRecyclerAdapter.ViewHolder>(DiffUtil) {

    class ViewHolder(private val itemBinding: CommentItemBinding,
                     private val context: Context,
                     private val clickListener: (CommentItem, Int) -> Unit): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: CommentItem, position: Int){
            Glide.with(context).load(item.author_img).into(itemBinding.profile)
            itemBinding.content.text = item.b_contents
            itemBinding.writer.text = item.b_writer
            itemBinding.time.text = item.b_time
            if(item.b_mine){
                itemBinding.commentEtc.visibility = View.VISIBLE
                itemBinding.commentEtc.setOnClickListener {
                    clickListener(item, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    companion object{
        val DiffUtil = object:DiffUtil.ItemCallback<CommentItem>(){
            override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
                return oldItem.b_id == newItem.b_id
            }

            override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}