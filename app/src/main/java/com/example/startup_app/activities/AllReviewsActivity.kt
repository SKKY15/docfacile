package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.adapters.ReviewAdapter
import com.example.startup_app.databinding.ActivityAllReviewsBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.Reviews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AllReviewsActivity : BaseActivity() {
    lateinit var binding : ActivityAllReviewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recycler = binding.reviewsrecycler
        val type = object : TypeToken<List<Reviews>>() {}.type
        val data: List<Reviews> = Gson().fromJson(intent.getStringExtra("reviewsJson"), type)
        val adapter = ReviewAdapter(this, data)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.imageView6.setOnClickListener {
            val gson = Gson()
            val mydoc = DataHolder.DoctorList.find { it.id == intent.getStringExtra("docId")?.toInt() }
            val i = Intent(this@AllReviewsActivity, DoctorDetailsActivity::class.java)
            if(mydoc == null) return@setOnClickListener
            i.putExtra("name", mydoc.name)
            i.putExtra("id", "${mydoc.id}")
            i.putExtra("rating", mydoc.rating)
            i.putExtra("reviews", mydoc.reviews)
            i.putExtra("speciality", mydoc.speciality)
            i.putExtra("street", mydoc.street)
            i.putExtra("image", mydoc.image)
            i.putExtra("exp", mydoc.experienceYear)
            val workingTimeJson = gson.toJson(mydoc.workingTime)
            i.putExtra("workingTime", workingTimeJson)
            i.putExtra("patients", mydoc.numPatients)
            i.putExtra("bio", mydoc.bio)

            startActivity(i, ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
    }
}