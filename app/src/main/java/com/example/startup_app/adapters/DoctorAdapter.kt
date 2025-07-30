package com.example.startup_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.viewmodels.Doctor
import com.example.startup_app.viewholders.DoctorViewHolder
import com.example.startup_app.R
import java.util.Locale

class DoctorAdapter(
    val context : Context,
    var data : MutableList<Doctor>,
    private var deletable : Boolean,
    val onFavEmpty: () -> Unit
) : RecyclerView.Adapter<DoctorViewHolder>() {
    var onItemClick : ((Doctor) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        return DoctorViewHolder(
            LayoutInflater.from(context).inflate(R.layout.doctor_card, parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
            val item = data[position]
            holder.namedoc.text = "Dr. ${item.name}"
            holder.ratingdoc.text = if (item.rating % 1.0 == 0.0) {
                item.rating.toInt().toString()
            } else {
                item.rating.toString()
            }
            val lang = Locale.getDefault().language
            val isFr = lang == "fr"
            holder.reviewsdoc.text = if (isFr) {
                "${item.reviews} avis"
            } else {
                "${item.reviews} reviews"
            }
            holder.speciality.text = item.speciality
            holder.streetdoc.text = item.street
            val im = if (item.isFavorite) R.drawable.heart_filled else R.drawable.heart_outlined_black
            holder.favdoc.setImageResource(im)
            holder.photodoc.setImageResource(item.image)
            holder.favdoc.setOnClickListener {
                    item.isFavorite = !item.isFavorite
                    val favIcon = if (item.isFavorite) R.drawable.heart_filled else R.drawable.heart_outlined_black
                    holder.favdoc.setImageResource(favIcon)
                if (deletable && !item.isFavorite) {
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
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }


    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: MutableList<Doctor>) {
        data = newList
        notifyDataSetChanged()
    }
}