package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.R
import com.example.startup_app.adapters.BookingAdapter
import com.example.startup_app.databinding.ActivityBookingBinding
import com.example.startup_app.viewmodels.Booking
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookingActivity : BaseActivity() {
    lateinit var binding: ActivityBookingBinding
    lateinit var adapter : BookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val selection = listOf(binding.upcomselection, binding.canceledselection, binding.completedselection)
        binding.upcomselection.isSelected = true
        adapter = configureAdapter(1)
        binding.re.adapter = adapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        binding.re.layoutManager = layoutManager
        binding.completed.setOnClickListener {
            selection.forEach { it.isSelected = false }
            binding.completedselection.isSelected = true
            adapter = configureAdapter(2)
            binding.re.adapter = adapter
        }
        binding.canceledbooking.setOnClickListener {
            selection.forEach { it.isSelected = false }
            binding.canceledselection.isSelected = true
            adapter = configureAdapter(3)
            binding.re.adapter = adapter
        }
        binding.upcoming.setOnClickListener {
            selection.forEach { it.isSelected = false }
            binding.upcomselection.isSelected = true
            adapter = configureAdapter(1)
            binding.re.adapter = adapter
        }
        binding.prf.setOnClickListener {
            val i = Intent(this@BookingActivity, ProfileActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, android.R.anim.fade_in, android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.hm.setOnClickListener {
            val i = Intent(this@BookingActivity, HomeActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, android.R.anim.fade_in, android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.mp.setOnClickListener {
            val i = Intent(this@BookingActivity, MapsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, android.R.anim.fade_in, android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }

    }

    private fun configureAdapter(n: Int): BookingAdapter {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val bookingsJson = sharedPref.getString("bookings", null)
        val type = object: TypeToken<MutableList<Booking>>() {}.type
        val bookings: MutableList<Booking> = gson.fromJson(bookingsJson, type) ?: mutableListOf()
        val filteredBookings = when (n) {
            1 -> bookings.filter {
                val status = it.status.lowercase()
                status == getString(R.string.upcomings).lowercase() || status == "à venir" || status == "upcoming"
            }
            2 -> bookings.filter {
                val status = it.status.lowercase()
                status == getString(R.string.completed).lowercase() || status == "terminé" || status == "completed"
            }
            3 -> bookings.filter {
                val status = it.status.lowercase()
                status == getString(R.string.canceled).lowercase() || status == "annulés" || status == "canceled"
            }
            else -> bookings
        }
        adapter = BookingAdapter(this, filteredBookings.toMutableList(), n)
        if (adapter.itemCount == 0) {
            binding.aze.visibility = View.VISIBLE
            binding.re.visibility = View.GONE
            binding.aze.text = when (n) {
                1 -> getString(R.string.nobookingup)
                2 -> getString(R.string.nobookingcom)
                3 -> getString(R.string.nobookingcan)
                else -> getString(R.string.nobookingfound)
            }
        } else {
            binding.aze.visibility = View.GONE
            binding.re.visibility = View.VISIBLE
        }

        return adapter
    }

}