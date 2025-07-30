package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.startup_app.databinding.ActivitySignUpBinding
import com.example.startup_app.viewmodels.User
import com.google.gson.Gson
import java.util.Locale

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var gson: Gson
    val lang : String = Locale.getDefault().language
    val isFr = lang == "fr"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gson = Gson()

        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if (sharedPref.getBoolean("signed", false)) {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(
                homeIntent,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
            return
        }
        if (sharedPref.getBoolean("profilenotcompleted", false)) {
            val fillIntent = Intent(this, FillProfileActivity::class.java)
            startActivity(
                fillIntent,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
            return
        }
        binding.singin.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(
                signInIntent,
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        }
        binding.createacc.setOnClickListener {
            val usernameText = binding.username.text.toString().trim()
            val passwordText = binding.password.text.toString().trim()
            val emailText    = binding.email.text.toString().trim()
            if (usernameText.isEmpty()
                || passwordText.isEmpty()
                || emailText.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
            ) {
                Toast.makeText(
                    this,
                    if (isFr) "Veuillez remplir tous les champs correctement." else "Please fill in all the fields correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                saveUserData(usernameText, emailText, passwordText)
                editor.putBoolean("profilenotcompleted", true)
                editor.apply()
                val userToJson = User(
                    lname    = "null",
                    fname    = "null",
                    username = usernameText,
                    email    = emailText,
                    birthday = "null",
                    gender   = "null",
                    image    = "null",
                    password = passwordText
                )
                val user = Gson().toJson(userToJson)
                val fillIntent = Intent(this, FillProfileActivity::class.java).apply { putExtra("userInfo", user)
                putExtra("sign", true)}
                startActivity(
                    fillIntent,
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
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

    private fun saveUserData(username: String, email: String, password: String) {
        val profilAlreadyMarked = sharedPref.getBoolean("profilenotcompleted", false)
        if (profilAlreadyMarked) return

        val userData = User(
            lname    = "null",
            fname    = "null",
            username = username,
            email    = email,
            birthday = "null",
            gender   = "null",
            image    = "null",
            password = password
        )
        val userJson = gson.toJson(userData)
        sharedPref.edit()
            .putString("user_data", userJson)
            .putBoolean("signed", true)
            .apply()
    }
}
