package com.example.startup_app.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R

class CarouselmagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.img_head)
    val titleHead: TextView = itemView.findViewById(R.id.title_head)
    val titleSecond: TextView = itemView.findViewById(R.id.title_second)
}
