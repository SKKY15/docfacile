package com.example.startup_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.viewholders.ReviewViewHolder
import com.example.startup_app.viewmodels.Reviews

class ReviewAdapter(
    val context : Context,
    val data : List<Reviews>
) : RecyclerView.Adapter<ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {

        return ReviewViewHolder(
            LayoutInflater.from(context).inflate(R.layout.review_card, parent , false)
        )
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.patientName
        holder.image.setImageResource(item.patientPhoto)
        holder.rating.text = item.rating.toString()
        holder.review.text = item.comment
        val stars = listOf(holder.star1, holder.star2, holder.star3, holder.star4, holder.star5)
        setStarRating(holder.rating.text.toString().toDoubleOrNull() ?: 0.0, stars)

    }
    private fun setStarRating(rating: Double, stars: List<ImageView>) {
        val fullStars = rating.toInt()
        val hasHalfStar = rating - fullStars in 0.25..0.74
        val shouldRoundUp = rating - fullStars >= 0.75

        for (i in stars.indices) {
            when {
                i < fullStars -> {
                    stars[i].setImageResource(R.drawable.star_filled)
                }
                i == fullStars && hasHalfStar -> {
                    stars[i].setImageResource(R.drawable.star_fiiled_half)
                }
                i == fullStars && shouldRoundUp -> {
                    stars[i].setImageResource(R.drawable.star_filled)
                }
                else -> {
                    stars[i].setImageResource(R.drawable.star_filled_gray)
                }
            }
        }
    }

}