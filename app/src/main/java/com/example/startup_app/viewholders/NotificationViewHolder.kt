package com.example.startup_app.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.viewmodels.NotificationDisplayItem

class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(notification: NotificationDisplayItem.NotificationItem) {
        val icon = itemView.findViewById<ImageView>(R.id.calendartype)
        val title = itemView.findViewById<TextView>(R.id.headtt)
        val message = itemView.findViewById<TextView>(R.id.secondtt)
        val time = itemView.findViewById<TextView>(R.id.time)
        val lin = itemView.findViewById<LinearLayout>(R.id.lin)
        title.text = notification.title
        message.text = notification.message
        time.text = notification.time
        when (notification.type) {
            "success" ->  {
                lin.setBackgroundColor(Color.parseColor("#DEF7E5"))
                icon.setImageResource(R.drawable.calendar_success)
            }
            "canceled" -> {
                lin.setBackgroundColor(Color.parseColor("#FDE8E8"))
                icon.setImageResource(R.drawable.calendar_cancel)
            }
            "changed" -> {
                lin.setBackgroundColor(Color.parseColor("#F3F4F6"))
                icon.setImageResource(R.drawable.calendar_changed)
            }
        }
    }
}