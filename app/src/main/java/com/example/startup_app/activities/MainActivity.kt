package com.example.startup_app.activities

import android.Manifest
import android.app.ActivityOptions
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.startup_app.R
import com.example.startup_app.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val channelid = "default_channel"
    private val notificationid = 101
    private val permissionrequestcode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkNotificationPermission()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(this@MainActivity, StartActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
            finish()
        }, 300)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("Notification", "Permission already granted")
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    Log.d("Notification", "Showing rationale")
                    requestNotificationPermission()
                }
                else -> {
                    Log.d("Notification", "Requesting permission")
                    requestNotificationPermission()
                }
            }
        } else {
            Log.d("Notification", "No permission needed for this Android version")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            permissionrequestcode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionrequestcode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Notification", "Permission granted by user")
                    showNotification()
                } else {
                    Log.d("Notification", "Permission denied by user")
                }
            }
        }
    }

    private fun showNotification() {
        Log.d("Notification", "Attempting to show notification")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelid)
            .setSmallIcon(R.drawable.ic_notification_basic)
            .setContentTitle("Test Notification")
            .setContentText("Notification is working!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(this).apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(notificationid, builder.build())
                    Log.d("Notification", "Notification shown with NotificationManagerCompat")
                } else {
                    Log.d("Notification", "No permission to show notification (Compat)")
                }
            }

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                notify(notificationid, builder.build())
                Log.d("Notification", "Notification shown with NotificationManager")
            }
        } catch (e: Exception) {
            Log.e("Notification", "Failed to show notification", e)
        }
    }



}