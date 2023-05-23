package com.tuk.shdelivery

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.databinding.ChatroomItemBinding

class ChatroomAdapter(private val chatrooms: List<Chatroom>) :
    RecyclerView.Adapter<ChatroomAdapter.ChatroomViewHolder>() {

    inner class ChatroomViewHolder(val binding: ChatroomItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomViewHolder {
        return ChatroomViewHolder(ChatroomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatroomViewHolder, position: Int) {
        val chatroom = chatrooms[position]
        holder.binding.roomName.text = chatroom.storeName
        holder.binding.roomName.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("CHATROOM_ID", chatroom.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = chatrooms.size
}



data class Chatroom(var id: String="", var storeName: String="", var foodType: String="", var time: String="")
