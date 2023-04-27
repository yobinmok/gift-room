package com.example.capstone_giftcon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.databinding.BoardlistItemBinding
import com.example.capstone_giftcon.item.BoardItem

class BoardRecyclerAdapter(private val clickListener: (BoardItem) -> Unit): ListAdapter<BoardItem, BoardRecyclerAdapter.BoardViewHolder>(DiffUtil) {

    class BoardViewHolder(private val itemBinding: BoardlistItemBinding, private val context: Context): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: BoardItem){
            Glide.with(context).load(item.imageUrl).into(itemBinding.imageGift)
            itemBinding.title.text = item.title
            itemBinding.contents.text = item.contents
            itemBinding.date.text = item.date
            itemBinding.writer.text = item.writer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = BoardlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    companion object{
        private val DiffUtil = object: DiffUtil.ItemCallback<BoardItem>(){
            override fun areItemsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}