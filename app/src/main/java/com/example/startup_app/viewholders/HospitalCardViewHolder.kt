package com.example.startup_app.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R

class HospitalCardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val name : TextView = itemView.findViewById(R.id.name)
    val image : ImageView = itemView.findViewById(R.id.imageh)
    val favorite : LinearLayout = itemView.findViewById(R.id.favorite)
    val street : TextView = itemView.findViewById(R.id.street)
    val rating : TextView = itemView.findViewById(R.id.rating)
    val reviews : TextView = itemView.findViewById(R.id.reviews)
    val heart : ImageView = itemView.findViewById(R.id.heart)
}