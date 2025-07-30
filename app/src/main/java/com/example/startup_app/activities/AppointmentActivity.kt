package com.example.startup_app.activities
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.startup_app.databinding.ActivityAppointmentBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.utils.showLocalNotification
import com.example.startup_app.viewmodels.Booking
import com.example.startup_app.viewmodels.Doctor
import com.example.startup_app.workers.NotificationWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.startup_app.utils.isBookingValidForDoctor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class AppointmentActivity : BaseActivity() {
    lateinit var binding: ActivityAppointmentBinding
    private var selectedDate: Long = 0L
    private var selectedHour = -1
    private var selectedMinute = 0
    private lateinit var bookingdate : Date
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        setContentView(binding.root)
            val hoursBtns = listOf(
                binding.nineam,
                binding.nineandhalfam,
                binding.tenam,
                binding.tenandhalfam,
                binding.elevenam,
                binding.elevenandhalfam,
                binding.thirdpm,
                binding.thirdandhalfpm,
                binding.fourthpm,
                binding.fourthandhalfpm,
                binding.fivthpm,
                binding.fifthandhalfpm
            )
        val hourMap = mapOf(
            binding.nineam to Pair(9, 0),
            binding.nineandhalfam to Pair(9, 30),
            binding.tenam to Pair(10, 0),
            binding.tenandhalfam to Pair(10, 30),
            binding.elevenam to Pair(11, 0),
            binding.elevenandhalfam to Pair(11, 30),
            binding.thirdpm to Pair(15, 0),
            binding.thirdandhalfpm to Pair(15, 30),
            binding.fourthpm to Pair(16, 0),
            binding.fourthandhalfpm to Pair(16, 30),
            binding.fivthpm to Pair(17, 0),
            binding.fifthandhalfpm to Pair(17, 30)
        )
        hoursBtns.forEach { button ->
            button.isSelected = false
            button.setTextColor(Color.parseColor("#6B7280"))

            button.setOnClickListener { bt ->
                hoursBtns.forEach {
                    it.isSelected = false
                    it.setTextColor(Color.parseColor("#6B7280"))
                }
                bt.isSelected = true
                (bt as Button).setTextColor(Color.WHITE)

                val time = hourMap[bt]
                if (time != null) {
                    selectedHour = time.first
                    selectedMinute = time.second
                }
            }
        }
            selectedDate = binding.calendar.date
            binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
            }

        val doctorJson = intent.getStringExtra("doctor")
        val doctor = gson.fromJson(doctorJson, Doctor::class.java)

        binding.imageView6.setOnClickListener {
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()

            if(doctor != null) {
                val i = Intent(this@AppointmentActivity, BookingActivity::class.java)


                i.putExtra("name", doctor.name)
                i.putExtra("id", "${doctor.id}")
                i.putExtra("rating", doctor.rating)
                i.putExtra("reviews", doctor.reviews)
                i.putExtra("speciality", doctor.speciality)
                i.putExtra("street", doctor.street)
                i.putExtra("image", doctor.image)
                i.putExtra("exp", doctor.experienceYear)
                val workingTimeJson = gson.toJson(doctor.workingTime)
                i.putExtra("workingTime", workingTimeJson)
                i.putExtra("patients", doctor.numPatients)
                i.putExtra("bio", doctor.bio)
                startActivity(i, options)
            } else {
                val mydoc = DataHolder.DoctorList.find { it.id == intent.getStringExtra("id")?.toInt() }
                val i = Intent(this@AppointmentActivity, DoctorDetailsActivity::class.java)
                if(mydoc == null) return@setOnClickListener
                i.putExtra("name", mydoc.name)
                i.putExtra("id", "${mydoc.id}")
                i.putExtra("rating", mydoc.rating)
                i.putExtra("reviews", mydoc.reviews)
                i.putExtra("speciality", mydoc.speciality)
                i.putExtra("street", mydoc.street)
                i.putExtra("image", mydoc.image)
                i.putExtra("exp", mydoc.experienceYear)
                val workingTimeJson = gson.toJson(mydoc.workingTime)
                i.putExtra("workingTime", workingTimeJson)
                i.putExtra("patients", mydoc.numPatients)
                i.putExtra("bio", mydoc.bio)

                startActivity(i, options)
            }
        }

        val lang = Locale.getDefault().language
        val isFr = lang == "fr"
        binding.btnconfappointment.setOnClickListener {
            if (selectedHour == -1) {
                Toast.makeText(this, if (isFr) "Veuillez choisir une heure." else "Please select a time.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val fullSelectedDate = calendar.timeInMillis

            if (fullSelectedDate  < System.currentTimeMillis()) {
                    Toast.makeText(this, if(isFr) "Vous ne pouvez pas réserver une date passée." else "You cannot book a past date.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            val date = Date(selectedDate)
            val locale = Locale.getDefault()
            val formatter = SimpleDateFormat("EEEE dd MMMM yyyy", locale)
            val formattedDate = formatter.format(date)
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val rawDate = isoFormatter.format(date)
            val hs = "$selectedHour:$selectedMinute"
            val bookingDateStr = "${formatDateWithSystemLocale(rawDate)} ${normalizeTime(hs)}"
            val doctor1 = doctor ?: DataHolder.DoctorList.find {
                it.id == intent.getStringExtra("id")?.toIntOrNull()
            }
            if (doctor1 != null) {
                Log.d("BookingDebug", "Booking date: $bookingDateStr")
                Log.d("BookingDebug", "Doctor available from ${doctor1.workingTime.startTime} to ${doctor1.workingTime.endTime}")
            } else {
                Log.e("BookingDebug", "Doctor not found.")
            }
            val fullDateStr = "$formattedDate ${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}"
            val sdf = SimpleDateFormat("EEEE dd MMMM yyyy HH:mm", Locale.getDefault())
            try {
                bookingdate = sdf.parse(fullDateStr)!!
                Log.d("SS", "Parsed bookingDate: $bookingdate")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Erreur lors de la lecture de la date", Toast.LENGTH_SHORT).show()
            }

            val worktime = doctor1?.workingTime
            val isValid = isBookingValidForDoctor(bookingdate, worktime!!)
            if(!isValid) {
                val message = if (Locale.getDefault().language == "fr") {
                    "Le médecin n'est pas disponible à cette date."
                } else {
                    "The doctor is not available at this date."
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val type = object : TypeToken<MutableList<Booking>>() {}.type
                val currentBookings: MutableList<Booking> = gson.fromJson(
                    sharedPref.getString("bookings", null), type
                ) ?: mutableListOf()
                    if(doctor != null) {

                        val index = currentBookings.indexOfFirst { it.doctorId == doctor.id.toString() }
                        if (index != -1) {
                            if(intent.getBooleanExtra("reshedule", false)) {
                                val updatedBooking = currentBookings[index].copy(
                                    status = "upcoming",
                                    date = rawDate,
                                    id = UUID.randomUUID().toString(),
                                    rating = null,
                                    time = "$selectedHour:$selectedMinute"
                                )
                                currentBookings.add(updatedBooking)
                                binding.successMsg.text = if(!isFr)
                                    "Your appointment with Dr. ${doctor.name} is re-scheduled for $formattedDate, at ${hoursBtns.first { it.isSelected }.text}."
                                    else
                                    "Votre rendez-vous avec le Dr ${doctor.name} a été replanifié pour le $formattedDate à ${hoursBtns.first { it.isSelected }.text}."
                            } else {
                                val updatedBooking = currentBookings[index].copy(
                                    date = rawDate,
                                    time = "$selectedHour:$selectedMinute"
                                )
                                currentBookings[index] = updatedBooking
                                binding.successMsg.text = if(!isFr)
                                    "Your appointment with Dr. ${doctor.name} is updated for $formattedDate, at ${hoursBtns.first { it.isSelected }.text}."
                                    else
                                    "Votre rendez-vous avec le Dr ${doctor.name} a été mis à jour pour le $formattedDate à ${hoursBtns.first { it.isSelected }.text}."

                            }
                        }
                        val updatedJson = gson.toJson(currentBookings)
                        sharedPref.edit().putString("bookings", updatedJson).apply()
                        showLocalNotification(this, currentBookings[index], "changed")
                    } else {
                        binding.successMsg.text = if(!isFr)
                            "Your appointment with Dr. ${intent.getStringExtra(("name").toString())} is confirmed for ${formattedDate}, at ${
                                hoursBtns.first { it.isSelected }.text
                            }."
                            else
                            "Votre rendez-vous avec le Dr ${intent.getStringExtra(("name").toString())} est confirmé pour le ${formattedDate}, à ${
                                hoursBtns.first { it.isSelected }.text
                            }."
                        val editor = sharedPref.edit()
                        val booking = Booking(
                            date = rawDate,
                            doctorId = "${intent.getStringExtra("id")}",
                            status = "upcoming",
                            time = "$selectedHour:$selectedMinute"
                        )

                        val alreadyBooked = currentBookings.any { it.doctorId == booking.doctorId && it.status == "upcoming" }
                        if (alreadyBooked) {
                            Toast.makeText(this, if (isFr) "Vous avez déjà une réservation avec ce docteur." else "You already have an appointment with this doctor.", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        currentBookings.add(booking)
                        val updatedJson = gson.toJson(currentBookings)
                        editor.putString("bookings", updatedJson)
                        editor.apply()
                        showLocalNotification(this, booking, "success")
                    }
                    binding.overlay.visibility = View.VISIBLE
                    binding.success.visibility = View.VISIBLE

                    startNotificationWorker(this)

        }
        binding.btndone.setOnClickListener {
            binding.overlay.visibility = View.GONE
            binding.success.visibility = View.GONE
        }
    }
    private fun startNotificationWorker(context: Context) {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(request)
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