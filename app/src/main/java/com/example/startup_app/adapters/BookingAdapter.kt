package com.example.startup_app.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.activities.AppointmentActivity
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.utils.showLocalNotification
import com.example.startup_app.viewholders.BookingCardViewHolder
import com.example.startup_app.viewmodels.Booking
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class BookingAdapter(
    val context : Context,
    var data : MutableList<Booking>,
    private var selected : Int = 1,
) : RecyclerView.Adapter<BookingCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingCardViewHolder {
        return BookingCardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.booking_card, parent, false)
        )
    }
    override fun getItemCount(): Int = data.size
    val sharedPref: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val type: Type = object : TypeToken<MutableList<Booking>>() {}.type
    private val currentBookings: MutableList<Booking> =
        gson.fromJson(sharedPref.getString("bookings", null), type) ?: mutableListOf()
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingCardViewHolder, position: Int) {
        val item = data[position]
        val doctor = DataHolder.DoctorList.firstOrNull { it.id == item.doctorId.toInt() }
        if (doctor != null) {
            holder.date.text = "${formatDateWithSystemLocale(item.date)} ${normalizeTime(item.time)}"
            holder.name.text = doctor.name
            holder.street.text = doctor.street
            holder.specialty.text = doctor.speciality
            holder.image.setImageResource(doctor.image)
            when (selected) {
                1 -> {
                    holder.rebook.text = context.getString(R.string.cancel)
                    holder.addrev.text = context.getString(R.string.reschedule)
                    holder.rebook.visibility = View.VISIBLE
                    holder.addrev.visibility = View.VISIBLE
                }
                2 -> {
                    holder.rebook.text = context.getString(R.string.rebook1)
                    holder.addrev.text = context.getString(R.string.review)
                    holder.rebook.visibility = View.VISIBLE
                    holder.addrev.visibility = View.VISIBLE
                }
                3 -> {
                    holder.rebook.visibility = View.GONE
                    holder.addrev.visibility = View.GONE
                }
            }
            if(item.rating != null) holder.addrev.visibility = View.GONE
            holder.rebook.setOnClickListener {
                if(selected == 1) {
                    val index = currentBookings.indexOfFirst { it.id == item.id }
                    if (index != -1) {
                        currentBookings[index].status = context.getString(R.string.canceled)
                        sharedPref.edit().putString("bookings", gson.toJson(currentBookings)).apply()
                    }
                    val intent = (context as Activity).intent
                    context.finish()
                    context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, 0, 0).toBundle())
                    showLocalNotification(context, currentBookings[position], "canceled")
                } else {
                    val intent = Intent(context, AppointmentActivity::class.java)
                    val doctorJson = gson.toJson(DataHolder.DoctorList.find { it.id == currentBookings[position].doctorId.toInt() })
                    intent.putExtra("doctor", doctorJson)
                    intent.putExtra("reshedule", true)
                    context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, 0, 0).toBundle())
                    (context as Activity).finish()
                }
            }
            holder.addrev.setOnClickListener {
                if(selected == 1) {
                    val intent = Intent(context, AppointmentActivity::class.java)
                    val gson = Gson()
                    val doctorToJson = gson.toJson(doctor)
                    intent.putExtra("doctor", doctorToJson)
                    context.startActivity(intent)
                } else {
                    holder.itemView.findViewById<Button>(R.id.cancelrebook).visibility = View.GONE
                    holder.itemView.findViewById<Button>(R.id.addreviewreshedule).visibility = View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.starrs).visibility = View.VISIBLE
                    val star = listOf(
                        holder.itemView.findViewById<ImageView>(R.id.starrr1),
                        holder.itemView.findViewById(R.id.starrr2),
                        holder.itemView.findViewById(R.id.starrr3),
                        holder.itemView.findViewById(R.id.starrr4),
                        holder.itemView.findViewById(R.id.starrr5),
                    )
                    star.forEach { starView ->
                        starView.setOnClickListener {
                            val index = currentBookings.indexOfFirst { it.id == item.id }

                            if (index != -1) {
                                val ratingValue = starView.resources.getResourceEntryName(starView.id).split("rrr")[1].toInt()
                                val updatedBooking = currentBookings[index].copy(rating = ratingValue)
                                currentBookings[index] = updatedBooking
                                sharedPref.edit().putString("bookings", gson.toJson(currentBookings)).apply()
                                holder.itemView.findViewById<Button>(R.id.cancelrebook).visibility = View.VISIBLE
                                holder.itemView.findViewById<LinearLayout>(R.id.starrs).visibility = View.GONE
                            }
                        }
                    }
                }
            }


        }
    }
    @SuppressLint("DefaultLocale")
    fun normalizeTime(timeStr: String): String {
        val parts = timeStr.split(":")
        val hour = parts[0].toInt()
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return String.format("%02d:%02d", hour, minute)
    }
    private fun formatDateWithSystemLocale(dateStr: String): String {
        val locale = Locale.getDefault()
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateStr, inputFormatter)

        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        val day = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.FULL, locale)

        return "${dayOfWeek.replaceFirstChar { it.uppercase(locale) }} $day ${month.replaceFirstChar { it.uppercase(locale) }}"
    }

}