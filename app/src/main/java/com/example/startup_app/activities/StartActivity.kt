package com.example.startup_app.activities

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.startup_app.R
import com.example.startup_app.databinding.ActivityStartBinding

class StartActivity : BaseActivity() {
    lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val isshowen = sharedPref.getBoolean("fscreens", false)
        if (isshowen) {
            val i = Intent(this@StartActivity, SignUpActivity::class.java)
            startActivity(
                i,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        } else {

            setContentView(binding.root)
            var currentPosition = 0
            val nextButton = binding.nextButton
            nextButton.setOnClickListener {
                currentPosition = (currentPosition + 1) % 3
                when (currentPosition) {
                    1 -> setVisibility(binding.doc2, binding.txt2, binding.tx1)
                    2 -> setVisibility(binding.doc3, binding.txt3, binding.tx3)
                    0 -> {
                        val i = Intent(this@StartActivity, SignUpActivity::class.java)
                        startActivity(
                            i,
                            ActivityOptions.makeCustomAnimation(
                                this,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            ).toBundle()
                        )
                        finish()
                        editor.putBoolean("fscreens", true)
                        editor.apply()
                    }
                }
                if (currentPosition != 0) {
                    updateIndicator(currentPosition)
                }
            }


            binding.skip.setOnClickListener {
                val i = Intent(this@StartActivity, SignUpActivity::class.java)
                startActivity(
                    i,
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
                finish()
                editor.putBoolean("fscreens", true)
                editor.apply()
            }
        }
    }


    private fun updateIndicator(position: Int) {
        val indicators = listOf(
            findViewById(R.id.indicator1),
            findViewById(R.id.indicator2),
            findViewById<View>(
                R.id.indicator3
            )
        )
        for ((index, view) in indicators.withIndex()) {
            val targetWidthDp = if (index == position) 30 else 10
            val scale = this.resources.displayMetrics.density
            val targetWidthPx = (targetWidthDp * scale + 0.5f).toInt()
            val drawable = if (index == position)
                ContextCompat.getDrawable(this, R.drawable.bg_button_next)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_rounded_grey)

            val widthAnimator = ValueAnimator.ofInt(view.width, targetWidthPx)
            widthAnimator.addUpdateListener { animator ->
                val params = view.layoutParams
                params.width = animator.animatedValue as Int
                view.layoutParams = params
            }
            widthAnimator.duration = 300
            widthAnimator.start()
            view.background = drawable
        }
    }

    private fun setVisibility(vararg viewsToShow: View) {
        val allViews = listOf(
            binding.doc1,
            binding.doc2,
            binding.doc3,
            binding.txt1,
            binding.txt2,
            binding.txt3,
            binding.tx1,
            binding.tx3
        )
        allViews.forEach { it.visibility = View.GONE }
        viewsToShow.forEach { it.visibility = View.VISIBLE }
    }

}