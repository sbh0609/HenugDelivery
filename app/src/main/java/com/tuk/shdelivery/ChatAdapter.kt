package com.tuk.shdelivery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.databinding.ChatItemBinding

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    inner class ChatViewHolder(val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.tvContent.text = message.content
    }

    override fun getItemCount() = messages.size
}
