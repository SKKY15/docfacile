package com.example.startup_app.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.startup_app.R
import com.example.startup_app.databinding.ActivityProfileBinding
import com.example.startup_app.viewmodels.User
import com.google.gson.Gson

class ProfileActivity : BaseActivity() {
    lateinit var binding: ActivityProfileBinding
    private lateinit var userData : User
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val json = sharedPrefs.getString("userData", null)
        if (json != null) {
            userData = Gson().fromJson(json, User::class.java)
            binding.name.text = "${userData.fname} ${userData.lname}"
            if (userData.image.isNotEmpty()) {
                val imageUri = Uri.parse(userData.image)
                binding.photoinfo.setImageURI(imageUri)
            }
        }
        binding.hm.setOnClickListener {
            startActivity(
                Intent(this@ProfileActivity, HomeActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.openedit.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, FillProfileActivity::class.java).apply { putExtra("userInfo", json) }, ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
        binding.gofavi.setOnClickListener {
            startActivity(
                Intent(this@ProfileActivity, FavoritsActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.map.setOnClickListener {
            startActivity(
                Intent(this@ProfileActivity, MapsActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.clndr.setOnClickListener {
            startActivity(
                Intent(this@ProfileActivity, BookingActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.notifs.setOnClickListener {
            startActivity(
                Intent(this@ProfileActivity, NotificationActivity::class.java).apply {
                    putExtra("where","profile")
                },
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.btnlog.setOnClickListener {
            showLogoutDialog()
        }
        binding.cancellogout.setOnClickListener {
            hideLogoutDialog()
        }
        binding.terms.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, TermsAndConditionsActivity::class.java), ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle())
        }
        binding.helpandsupp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:yanishales19@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Help and support")
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        binding.yeslogout.setOnClickListener {
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.putBoolean("fscreens", true)
            editor.apply()
            startActivity(Intent(this@ProfileActivity, SignInActivity::class.java), ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle())
        }
    }
    private fun hideLogoutDialog() {
        val logoutView = findViewById<LinearLayout>(R.id.checklogout)

        val shadow = findViewById<ConstraintLayout>(R.id.shadow)
        shadow.visibility = View.GONE
        logoutView.animate()
            .translationY(logoutView.height.toFloat() + 500f)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                logoutView.visibility = View.GONE
            }
            .start()
    }

    private fun showLogoutDialog() {
        val logoutLayout = findViewById<LinearLayout>(R.id.checklogout)
        logoutLayout.bringToFront()
        logoutLayout.invalidate()
        logoutLayout.visibility = View.VISIBLE
        logoutLayout.translationY = logoutLayout.height.toFloat() + 500f
        logoutLayout.alpha = 0f
        val shadow = findViewById<ConstraintLayout>(R.id.shadow)
        shadow.visibility = View.VISIBLE
        logoutLayout.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }


}