package com.example.startup_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.viewmodels.HospitalCard
import com.example.startup_app.viewholders.HospitalCardViewHolder
import com.example.startup_app.R
import java.util.Locale

class HospitalCardAdapter(
    val context: Context,
    var data: MutableList<HospitalCard>,
    private val deletable : Boolean,
    val onFavEmpty : () -> Unit,
) : RecyclerView.Adapter<HospitalCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalCardViewHolder {
        return HospitalCardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.hospitals_card, parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HospitalCardViewHolder, position: Int) {
        val item = data[position]
        holder.image.setImageResource(item.image)
        holder.name.text = item.name
        holder.rating.text = item.rating.toString()
        holder.street.text = item.street
        val isFr = Locale.getDefault().language == "fr"
        holder.reviews.text = if (isFr) {
            "(${item.reviews} avis)"
        } else {
            "(${item.reviews} reviews)"
        }
        if(item.isFavorite) {
            holder.heart.setImageResource(R.drawable.heart_filled)
        } else {
            holder.heart.setImageResource(R.drawable.heart_outlined)
        }
        holder.favorite.setOnClickListener {
            if(item.isFavorite) {
                holder.heart.setImageResource(R.drawable.heart_outlined)
            } else {
                holder.heart.setImageResource(R.drawable.heart_filled)
            }
            item.isFavorite = !item.isFavorite
            if(deletable && !item.isFavorite) {
                data.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, data.size)
                if (data.isEmpty()) {
                    onFavEmpty()
                }
                if(data.none { it.isFavorite }) {
                    onFavEmpty()
                }
            }

        }



    }
}