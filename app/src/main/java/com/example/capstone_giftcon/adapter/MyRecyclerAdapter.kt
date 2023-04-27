package com.example.capstone_giftcon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.R
import com.example.capstone_giftcon.item.CommentItem
import java.lang.Boolean
import kotlin.Int

class MyRecyclerAdapter : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {
    private var CommentList: ArrayList<CommentItem>? = null
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null
    private var context: Context? = null

    // 댓글 클릭: https://parkho79.tistory.com/152
    // https://recipes4dev.tistory.com/168 -> 해결
    // 리사이클러뷰 설명 : https://itknowledgeshare.tistory.com/entry/%EB%A6%AC%EC%82%AC%EC%9D%B4%ED%81%B4%EB%9F%AC%EB%B7%B0-RecyclerView
    //                  https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko
    // View holder를 생성하는 부분으로, inflate된 view에 listener를 설정할 수 있다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        context = parent.context
        return ViewHolder(view, itemClickListener!!, itemLongClickListener)
    }

    //Adapter's onBindViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(CommentList!![position])
    }

    fun setList(list: ArrayList<CommentItem>?) {
        CommentList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return CommentList!!.size
    }

    fun clear() {
        val size = CommentList!!.size
        CommentList!!.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // 목록의 각 개별 요소는 뷰 홀더 객체로 정의된다. 따라서 아이템에 접근하기 위해서는 뷰 홀더 객체에 접근해야 함
    inner class ViewHolder(
        itemView: View,
        itemClickListener: OnItemClickListener,
        itemLongClickListener: OnItemLongClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        var profile: ImageView
        var content: TextView
        var writer: TextView
        var time: TextView
        var more: ImageView

        // ViewHolder 생성자 -> 인자로 전달된 item에 listener를 설정할 수 있다.
        init {
            profile = itemView.findViewById<View>(R.id.profile) as ImageView
            content = itemView.findViewById<View>(R.id.content) as TextView
            writer = itemView.findViewById<View>(R.id.writer) as TextView
            time = itemView.findViewById<View>(R.id.time) as TextView
            more = itemView.findViewById(R.id.commentEtc)
            more.setOnClickListener(View.OnClickListener { view ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(view, position)
                }
            })
            itemView.setOnLongClickListener(OnLongClickListener { view ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemLongClickListener?.onItemLongClick(view, position)
                }
                true
            })
        }

        fun onBind(item: CommentItem) {
            val author_img = item.author_img
            Glide.with(context!!).load(author_img).into(profile)
            content.text = item.b_contents
            writer.text = item.b_writer
            time.text = item.b_time
            if (item.b_mine == Boolean.TRUE) {
                more.visibility = View.VISIBLE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View?, position: Int)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        itemLongClickListener = listener
    }
}