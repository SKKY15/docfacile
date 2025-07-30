package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.startup_app.databinding.ActivityForgotPasswordBinding
import java.util.Locale

class ForgotPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener {
            startActivity(
                Intent(this@ForgotPasswordActivity
                    ,SignInActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        val lang = Locale.getDefault().language
        val isFr = lang == "fr"
        binding.btnsend.setOnClickListener {
            if (binding.emailreset.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(binding.emailreset.text.toString())
                    .matches()
            ) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    if(isFr) "S'il vous plaît, remplissez le champ email correctement." else "Please fill in the email field correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val code = (10000..99999).random().toString()
                val i =
                    Intent(this@ForgotPasswordActivity, EmailCodeVerifyActivity::class.java).apply {
                        putExtra("verification_code", code)
                    }
                startActivity(
                    i,
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
            }
        }
    }
}