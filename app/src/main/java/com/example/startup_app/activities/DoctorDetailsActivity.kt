package com.example.startup_app.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.adapters.ReviewAdapter
import com.example.startup_app.databinding.ActivityDoctorDetailsBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.WorkingTimeModel
import com.google.gson.Gson

class DoctorDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityDoctorDetailsBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.setOnClickListener {
            val i = Intent(this@DoctorDetailsActivity, AllDoctorsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i,options)
        }
        binding.namedoc1.text = intent.getStringExtra("name").toString()
        binding.specialitydoc1.text = intent.getStringExtra("speciality").toString()
        binding.adressedoc1.text = intent.getStringExtra("street").toString()
        binding.ratingdoc1.text = intent.getDoubleExtra("rating", 0.0).toString()
        binding.reviewsdoc1.text = "${intent.getIntExtra("reviews", 0)}"
        binding.statsRating.text = intent.getDoubleExtra("rating", 0.0).toString()
        binding.statsReviews.text = intent.getIntExtra("reviews", 0).toString()
        binding.imgdoc1.setImageResource(intent.getIntExtra("image", R.drawable.profile))
        binding.docexp.text = "${intent.getIntExtra("exp", 0)} +"
        binding.bio.text = intent.getStringExtra("bio")
        binding.docpatients.text = intent.getIntExtra("patients", 0).toString()
        if(DataHolder.DoctorList.find { it.id == intent.getStringExtra("id")?.toInt() }?.isFavorite == true) {
            binding.imageView2.setImageResource(R.drawable.heart_filled)
        }
        val workingTimeJson = intent.getStringExtra("workingTime")
        val gson = Gson()
        val workingTime = gson.fromJson(workingTimeJson, WorkingTimeModel::class.java)
        binding.worktime.text = "$workingTime"
        val recycler = binding.carouselrecycler
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.right = 32
            }
        })
        val filteredData = DataHolder.ReviewList
            .filter { it.doctorId == intent.getStringExtra("id") }
            .sortedByDescending { it.date.toInt() }
            .take(3)
        if(filteredData.isEmpty()) {
            binding.noreviewid.visibility = View.VISIBLE
        } else {
            val adapter = ReviewAdapter(this, DataHolder.ReviewList.filter { it.doctorId == intent.getStringExtra("id") }.sortedByDescending { it.date.toInt() }.take(3))
            binding.seeallreviews.visibility = View.VISIBLE
            recycler.adapter = adapter
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recycler)
        }
        binding.btnappointement.setOnClickListener {
            val i = Intent(this@DoctorDetailsActivity, AppointmentActivity::class.java)
            i.putExtra("name", intent.getStringExtra("name").toString())
            i.putExtra("id", intent.getStringExtra("id").toString())
            startActivity(i)
        }
        binding.seeallreviews.setOnClickListener {
            val i = Intent(this@DoctorDetailsActivity, AllReviewsActivity::class.java)
            val reviewsJson = Gson().toJson(DataHolder.ReviewList
                .filter { it.doctorId == intent.getStringExtra("id") }
                .sortedByDescending { it.date.toInt() })
            i.putExtra("reviewsJson", reviewsJson)
            i.putExtra("docId", "${intent.getStringExtra("id")}")
            startActivity(i)
        }
    }








}