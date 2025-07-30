package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.startup_app.databinding.ActivityNewPasswordBinding
import java.util.Locale

class NewPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityNewPasswordBinding
    val lang : String = Locale.getDefault().language
    val isFr = lang == "fr"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chng.setOnClickListener {
            if (binding.pass.text.isEmpty() || binding.passsignin.text.isEmpty()) {
                Toast.makeText(
                    this@NewPasswordActivity,
                    if (isFr) "Veuillez remplir les champs" else "Please fill in the fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.pass.text.toString() != binding.passsignin.text.toString()) {
                Toast.makeText(
                    this@NewPasswordActivity,
                    if (isFr) "Mot de passe différents" else "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(
                    Intent(this@NewPasswordActivity, HomeActivity::class.java),
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
                Toast.makeText(this@NewPasswordActivity, if (isFr) "Mot de passe changé" else "Password changed"
                    , Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.back.setOnClickListener {
            startActivity(
                Intent(this@NewPasswordActivity
                    ,HomeActivity::class.java).apply { putExtra("verification_code", intent.getStringExtra("code")) },
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
    }
}