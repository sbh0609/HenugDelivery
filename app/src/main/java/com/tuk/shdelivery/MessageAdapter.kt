package com.tuk.shdelivery

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuk.shdelivery.databinding.MessageItemBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: MutableList<Chat>, private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    inner class MessageViewHolder(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.tvUserId.text = "${message.userId} :"
        holder.binding.tvContent.text = message.content
        holder.binding.tvTimestamp.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
        // Add layout direction here
        if (message.userId == currentUserId) {
            holder.binding.messageContainer.gravity = Gravity.END
        } else {
            holder.binding.messageContainer.gravity = Gravity.START
        }
    }

    override fun getItemCount() = messages.size
}


