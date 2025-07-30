package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.startup_app.databinding.ActivitySignInBinding
import java.util.Locale

class SignInActivity : BaseActivity() {
    lateinit var binding: ActivitySignInBinding
    val lang : String = Locale.getDefault().language
    val isFr = lang == "fr"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.singup.setOnClickListener {
            val i = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(
                i,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        }
        binding.passforgot.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
            startActivity(
                i,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        }
        binding.seconnecter.setOnClickListener {
            val email = binding.emailsignin.text.toString().trim()
            val password = binding.passsignin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this@SignInActivity,
                    if (isFr) "S'il vous plaît, remplissez toutes les informations correctement." else "Please fill in all the information correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val i = Intent(this@SignInActivity, HomeActivity::class.java)
                startActivity(
                    i,
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
                val editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
                editor.putBoolean("signed", true)
                editor.apply()
                finish()
            }
        }

        val language = Locale.getDefault().language
        val isFr = language == "fr"
        val soonbtn = listOf(binding.gc, binding.fc)
        soonbtn.forEach { btn ->
            btn.setOnClickListener {
                Toast.makeText(this, if(isFr) "Sera Bientôt disponible!" else "Will be available soon!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}