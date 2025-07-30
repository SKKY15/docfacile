package com.example.startup_app.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R

class DoctorViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val namedoc : TextView = itemView.findViewById(R.id.namedoc)
        val photodoc : ImageView = itemView.findViewById(R.id.imgdoc)
        val favdoc : ImageView = itemView.findViewById(R.id.favdoc)
        val speciality : TextView = itemView.findViewById(R.id.specialitydoc)
        val streetdoc : TextView = itemView.findViewById(R.id.adressedoc)
        val ratingdoc : TextView = itemView.findViewById(R.id.ratingdoc)
        val reviewsdoc : TextView = itemView.findViewById(R.id.reviewsdoc)
}