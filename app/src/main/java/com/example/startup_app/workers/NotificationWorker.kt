package com.example.startup_app.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.startup_app.R
import com.example.startup_app.viewmodels.Booking
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.graphics.BitmapFactory
import android.util.Log
import com.example.startup_app.objects.DataHolder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Random


class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    @SuppressLint("DefaultLocale")
    override fun doWork(): Result {
        Log.d("NotificationWorker", "Worker started. Reading SharedPreferences...")
        val sharedPref = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Booking>>() {}.type
        val bookings: MutableList<Booking> = gson.fromJson(sharedPref.getString("bookings", null), type)
            ?: mutableListOf()

        Log.d("NotificationWorker", "Bookings loaded: ${bookings.size}")

        var updated = false
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

        for (booking in bookings) {
            if (booking.status != "upcoming" || booking.isNotified) {
                Log.d("NotificationWorker", "⏩ Déjà notifié ou non pertinent: ${booking.date}")
                continue
            }

            Log.d("NotificationWorker", "🔍 Vérification du rendez-vous: ${booking.date}, isNotified: ${booking.isNotified}")

            try {
                val timeParts = booking.time.split(":")
                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: continue
                val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                val normalizedTime = String.format("%02d:%02d", hour, minute)
                val dateTimeString = "${booking.date} $normalizedTime"

                val dateParsed = parser.parse(dateTimeString) ?: continue
                val bookingCalendar = Calendar.getInstance().apply { time = dateParsed }

                val nowMillis = System.currentTimeMillis()
                val diffMillis = bookingCalendar.timeInMillis - nowMillis
                val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis)

                Log.d("NotificationWorker", "✅ Date complète: $dateTimeString | DiffHeures: $diffHours")

                if (diffHours in 1..24) {
                    Log.d("NotificationWorker", "🔔 Envoi de la notification pour le rendez-vous de demain.")
                    sendSystemNotification(applicationContext, booking)
                    booking.isNotified = true
                    updated = true
                }

            } catch (e: Exception) {
                Log.e("NotificationWorker", "❌ Erreur lors du traitement de: ${booking.date} ${booking.time}", e)
            }
        }

        if (updated) {
            val editor = sharedPref.edit()
            editor.putString("bookings", gson.toJson(bookings))
            editor.apply()
            Log.d("NotificationWorker", "Updated bookings saved in SharedPreferences.")
        }

        return Result.success()
    }



    private fun sendSystemNotification(context: Context, booking: Booking) {
        val channelId = "appointments_channel"
        createNotificationChannel(context)

        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.mylogo)

        val title = context.getString(R.string.appointment_title)
        val doctor = DataHolder.DoctorList.find { it.id == booking.doctorId.toInt() }?.name
        val doctorName = doctor ?: if(Locale.getDefault().language == "fr") "votre médecin" else "your doctor"
        val displayName = if (doctor != null) "Dr. $doctor" else doctorName
        val shortMessage = context.getString(
            R.string.appointment_tomorrow_fr,
            "${formatDateWithSystemLocale(booking.date)} ${normalizeTime(booking.time)}",
            displayName
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_basic)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setLargeIcon(largeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(shortMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random().nextInt(), builder.build())
    }


    private fun createNotificationChannel(context: Context) {
        val name = "Appointments"
        val descriptionText = "Appointement notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("appointments_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    @SuppressLint("DefaultLocale")
    fun normalizeTime(timeStr: String): String {
        val parts = timeStr.split(":")
        val hour = parts[0].toInt()
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return String.format("%02d:%02d", hour, minute)
    }
    private fun formatDateWithSystemLocale(dateStr: String): String {
        val locale = Locale.getDefault()
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateStr, inputFormatter)

        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        val day = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.FULL, locale)

        return "${dayOfWeek.replaceFirstChar { it.uppercase(locale) }} $day ${month.replaceFirstChar { it.uppercase(locale) }}"
    }

}
