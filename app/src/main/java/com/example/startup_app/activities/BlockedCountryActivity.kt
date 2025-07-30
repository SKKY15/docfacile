package com.example.startup_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.startup_app.databinding.ActivityBlockedCountryBinding

class BlockedCountryActivity : AppCompatActivity() {
    lateinit var binding : ActivityBlockedCountryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}