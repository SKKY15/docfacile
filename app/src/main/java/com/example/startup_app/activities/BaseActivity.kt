package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    private var toastSent = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserLocation()
        onBackPressedDispatcher.addCallback(this) {

        }
    }
    private fun checkUserLocation() {
        Thread {
            try {
                val url = URL("https://ipinfo.io/json")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val countryCode = json.optString("country", "UNKNOWN")
                    val countryName = getCountryNameFromCode(countryCode)

                    Log.d("DEBUG_CHECK", "Got country: $countryName ($countryCode)")

                    val lang = Locale.getDefault().language
                    val isFr = lang == "fr"
                    runOnUiThread {
                        Log.d("DEBUG_CHECK", "runOnUiThread started")
                        if (!isFinishing && !isDestroyed && countryCode != "DZ") {
                            if(!toastSent) {
                                Toast.makeText(
                                    this,
                                    if(isFr) "⚠️ Cette application est réservée à l’Algérie (pays détecté : $countryName)" else "⚠\uFE0F This application is restricted to Algeria (detected country: $countryName)",
                                    Toast.LENGTH_LONG
                                ).show()
                                toastSent = true
                            }
                            startActivity(Intent(this@BaseActivity, BlockedCountryActivity::class.java), ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                        }
                    }
                } else {
                    Log.d("DEBUG_CHECK", "Response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_CHECK", "Exception: ${e.message}")
            }
        }.start()
    }

    private fun getCountryNameFromCode(code: String): String {
        val locale = Locale("", code)
        return locale.displayCountry
    }

}
