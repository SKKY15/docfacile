package com.example.startup_app.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R

class ReviewViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.patientName)
        val image: ImageView = itemView.findViewById(R.id.patientImage)
        val rating : TextView = itemView.findViewById(R.id.patientRating)
        val review : TextView = itemView.findViewById(R.id.patientReview)
        val star1 : ImageView = itemView.findViewById(R.id.star1)
        val star2 : ImageView = itemView.findViewById(R.id.star2)
        val star3 : ImageView = itemView.findViewById(R.id.star3)
        val star4 : ImageView = itemView.findViewById(R.id.star4)
        val star5 : ImageView = itemView.findViewById(R.id.star5)
}