package com.example.startup_app.viewholders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R

class BookingCardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.date)
        val image = itemView.findViewById<ImageView>(R.id.imgdoc2)
        val name = itemView.findViewById<TextView>(R.id.namedoc1)
        val specialty = itemView.findViewById<TextView>(R.id.specialitydoc1)
        val street = itemView.findViewById<TextView>(R.id.adressedoc1)
        val rebook = itemView.findViewById<Button>(R.id.cancelrebook)
        val addrev = itemView.findViewById<TextView>(R.id.addreviewreshedule)
}