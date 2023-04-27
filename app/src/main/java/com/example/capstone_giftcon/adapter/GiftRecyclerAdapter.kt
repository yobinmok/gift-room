package com.example.capstone_giftcon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.databinding.GiftListItemBinding
import com.example.capstone_giftcon.item.GiftItem

class GiftRecyclerAdapter(private val onItemClicked: (GiftItem) -> Unit):ListAdapter<GiftItem, GiftRecyclerAdapter.GiftViewHolder>(DiffUtil) {

    class GiftViewHolder(private val itemBinding: GiftListItemBinding, private val context: Context):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: GiftItem){
            Glide.with(context).load(item.imgId).into(itemBinding.imageGift)
            itemBinding.textBrand.text = item.brand
            itemBinding.textGiftName.text = item.giftName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val item = GiftListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftViewHolder(item, parent.context)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            // DetailActivity로 아이템 정보 보내기!
            onItemClicked(item)
        }
        holder.bind(item)
    }

    companion object{
        private val DiffUtil = object: DiffUtil.ItemCallback<GiftItem>(){
            override fun areItemsTheSame(oldItem: GiftItem, newItem: GiftItem): Boolean {
                return oldItem.giftId == newItem.giftId
            }

            override fun areContentsTheSame(oldItem: GiftItem, newItem: GiftItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}