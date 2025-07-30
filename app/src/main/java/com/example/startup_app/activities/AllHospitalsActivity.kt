package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.adapters.HospitalCardAdapter
import com.example.startup_app.databinding.ActivityAllHospitalsBinding
import com.example.startup_app.objects.DataHolder

class AllHospitalsActivity : BaseActivity() {
    lateinit var binding: ActivityAllHospitalsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllHospitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recycler = binding.recyclerrr
        val adapter = HospitalCardAdapter(this, DataHolder.HospitalList,false) {}
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.returnarra.setOnClickListener {
            startActivity(
                Intent(this@AllHospitalsActivity
                    ,HomeActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
    }
}