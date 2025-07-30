package com.example.startup_app.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.viewmodels.NotificationDisplayItem

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(header: NotificationDisplayItem.Header) {
        itemView.findViewById<TextView>(R.id.headerTitle).text = header.title
    }
}