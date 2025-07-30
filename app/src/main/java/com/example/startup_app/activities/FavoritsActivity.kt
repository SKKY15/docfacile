package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.adapters.DoctorAdapter
import com.example.startup_app.adapters.HospitalCardAdapter
import com.example.startup_app.databinding.ActivityFavoritsBinding
import com.example.startup_app.objects.DataHolder
import java.util.Locale

class FavoritsActivity : BaseActivity() {
    lateinit var binding: ActivityFavoritsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fullListDoctors = DataHolder.DoctorList
        val fullListHospitals = DataHolder.HospitalList
        binding.docselection.isSelected = true
        val favoriteDoctors = fullListDoctors.filter { it.isFavorite }.toMutableList()
        val lang = Locale.getDefault().language
        val isFr = lang == "fr"
        val firstAdapter = DoctorAdapter(this, favoriteDoctors, true) {
            showEmptyMessage(if(isFr) "Aucun de vos médecins favoris" else "No Favorite Doctors")
        }
        binding.imageView3.setOnClickListener {
                startActivity(
                    Intent(this@FavoritsActivity
                        ,ProfileActivity::class.java),
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
        }
        if (favoriteDoctors.isEmpty()) {
            showEmptyMessage(if(isFr) "Aucun de vos médecins favoris" else "No Favorite Doctors")
        } else {
            binding.recycle.adapter = firstAdapter
            binding.recycle.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        binding.doctor.setOnClickListener {
            binding.emptyMessage.visibility = View.GONE
            animateSelection(binding.docselection, binding.hospitalselection)
            binding.docselection.isSelected = true
            binding.hospitalselection.isSelected = false

            val filteredDoctors = fullListDoctors.filter { it.isFavorite }.toMutableList()
            val adapter = DoctorAdapter(this, filteredDoctors, true) {
                showEmptyMessage(if(isFr) "Aucun de vos médecins favoris" else "No Favorite Doctors")
            }

            if (filteredDoctors.isEmpty()) {
                showEmptyMessage(if(isFr) "Aucun de vos médecins favoris" else "No Favorite Doctors")
            } else {
                binding.recycle.adapter = adapter
                binding.recycle.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }

        binding.hospital.setOnClickListener {
            animateSelection(binding.hospitalselection, binding.docselection)

            binding.emptyMessage.visibility = View.GONE
            binding.hospitalselection.isSelected = true
            binding.docselection.isSelected = false

            val filteredHospitals = fullListHospitals.filter { it.isFavorite }.toMutableList()
            val adapter = HospitalCardAdapter(this, filteredHospitals, true) {
                showEmptyMessage(if(isFr) "Aucun de vos hôpitaux favoris" else "No Favorite Hospitals")
            }

            if (filteredHospitals.isEmpty()) {
                showEmptyMessage(if(isFr) "Aucun de vos hôpitaux favoris" else "No Favorite Hospitals")
            } else {
                binding.recycle.adapter = adapter
                binding.recycle.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }


    }

    private fun showEmptyMessage(message: String) {
        val emptyText = binding.emptyMessage
        emptyText.text = message
        emptyText.visibility = View.VISIBLE
        emptyText.alpha = 0f
        emptyText.animate().alpha(1f).setDuration(300).start()
    }

    private fun animateSelection(selectedView: View, unselectedView: View) {
        selectedView.visibility = View.VISIBLE
        selectedView.alpha = 0f
        selectedView.translationX = -30f
        selectedView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(300)
            .start()
        unselectedView.animate()
            .alpha(0f)
            .translationX(30f)
            .setDuration(300)
            .withEndAction {
                unselectedView.visibility = View.INVISIBLE
                unselectedView.translationX = 0f
            }.start()
    }

}