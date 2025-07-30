package com.example.startup_app.adapters

import com.example.startup_app.viewmodels.NotificationDisplayItem
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.viewholders.HeaderViewHolder
import com.example.startup_app.viewholders.NotificationViewHolder

class NotificationAdapter(
    val context : Context,
    private var items: List<NotificationDisplayItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTIFICATION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationDisplayItem.Header -> TYPE_HEADER
            is NotificationDisplayItem.NotificationItem -> TYPE_NOTIFICATION
            else -> throw IllegalArgumentException("Unknown view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.notifications_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.notifications, parent, false)
            NotificationViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> {
                val headerItem = item as NotificationDisplayItem.Header
                holder.bind(headerItem)
            }
            is NotificationViewHolder -> {
                val notifItem = item as NotificationDisplayItem.NotificationItem
                holder.bind(notifItem)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<NotificationDisplayItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun getItemCount() = items.size
}
