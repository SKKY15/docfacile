package com.example.startup_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.viewmodels.Carouselmages
import com.example.startup_app.viewholders.CarouselmagesViewHolder
import com.example.startup_app.R

class CarouselmagesAdapter(
    val context : Context,
    private val items: List<Carouselmages>
) : RecyclerView.Adapter<CarouselmagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselmagesViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.carousel_images, parent, false)
        return CarouselmagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselmagesViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.image)
        holder.titleHead.text = item.title_head
        holder.titleSecond.text = item.title_second
    }

    override fun getItemCount(): Int = items.size
}
