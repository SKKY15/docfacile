package com.example.startup_app.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.startup_app.R
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.Booking
import com.example.startup_app.viewmodels.NotificationDisplayItem
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale


fun showLocalNotification(context: Context, booking: Booking, type: String) {
    val doctorName = DataHolder.DoctorList.find { it.id == booking.doctorId.toInt() }?.name
        ?: context.getString(R.string.default_doctor_name)


    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dateParsed = parser.parse(booking.date)

    val currentLocale = Locale.getDefault()
    val outputFormatter = SimpleDateFormat("EEEE - d MMMM yyyy", currentLocale)
    val localizedDate = outputFormatter.format(dateParsed!!)
    val appointmentTime = booking.time

    val message = when (type) {
        "success" -> context.getString(R.string.appointment_success, doctorName, "$localizedDate ${if(Locale.getDefault().language == "fr") "à" else "at"} ${normalizeTime(appointmentTime)}")
        "changed" -> context.getString(R.string.appointment_changed, doctorName, "$localizedDate ${if(Locale.getDefault().language == "fr") "à" else "at"} ${normalizeTime(appointmentTime)}")
        "canceled" -> context.getString(R.string.appointment_canceled, doctorName)
        else -> context.getString(R.string.appointment_updated)
    }

    val notificationItem = NotificationDisplayItem.NotificationItem(
                type = type,
                title = if(Locale.getDefault().language == "fr") "Rendez-vous DocFacile" else "DocFacile Appointment",
                message = message,
                time = localizedDate
            )

            val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val notifJson = sharedPref.getString("notifications", null)
            val notifType = object : TypeToken<MutableList<NotificationDisplayItem.NotificationItem>>() {}.type
            val existing: MutableList<NotificationDisplayItem.NotificationItem> =
                gson.fromJson(notifJson, notifType) ?: mutableListOf()

            existing.add(0, notificationItem)
            sharedPref.edit().putString("notifications", gson.toJson(existing)).apply()
}
@SuppressLint("DefaultLocale")
fun normalizeTime(timeStr: String): String {
    val parts = timeStr.split(":")
    val hour = parts[0].toInt()
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return String.format("%02d:%02d", hour, minute)
}
