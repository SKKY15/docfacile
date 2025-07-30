package com.example.startup_app.activities

import com.example.startup_app.viewmodels.NotificationDisplayItem
import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.startup_app.R
import com.example.startup_app.adapters.CarouselmagesAdapter
import com.example.startup_app.adapters.HospitalCardAdapter
import com.example.startup_app.databinding.ActivityHomeBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.Booking
import com.example.startup_app.viewmodels.Carouselmages
import com.example.startup_app.viewmodels.Doctor
import com.example.startup_app.viewmodels.HospitalCard
import com.example.startup_app.viewmodels.Reviews
import com.example.startup_app.viewmodels.WeekDay
import com.example.startup_app.viewmodels.WorkingTimeModel
import com.example.startup_app.workers.NotificationWorker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class HomeActivity : BaseActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var indicators: MutableList<View>
    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var delay: Long = 3000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: ActivityHomeBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var userLocation: LatLng? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var googleMap: GoogleMap

    @SuppressLint("SuspiciousIndentation", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLocationCallback()


        val reviewsList = mutableListOf(
            Reviews(
                "Yanis Hales",
                "Very disappointing experience. I do not recommend this doctor.",
                1.0,
                "2024",
                R.drawable.userreview1,
                "1"
            ),
            Reviews(
                "Sarah Brown",
                "Decent service, but there’s definitely room for improvement.",
                3.0,
                "2023",
                R.drawable.userreview2,
                "1"
            ),
            Reviews(
                "Imène Zerrouki",
                "The consultation was okay, but a few things could be better.",
                3.9,
                "2024",
                R.drawable.userreview3,
                "1"
            ),
            Reviews(
                "Karim Mansouri",
                "Excellent care! I highly recommend this doctor.",
                5.0,
                "2022",
                R.drawable.userreview4,
                "1"
            ),
            Reviews(
                "Mohamed Said",
                "I wasn’t satisfied. The explanation was unclear.",
                2.5,
                "2023",
                R.drawable.userreview5,
                "1"
            ),
            Reviews(
                "Lina Boucher",
                "Great experience! The clinic was clean and staff very professional.",
                4.2,
                "2021",
                R.drawable.userreview6,
                "1"
            ),
            Reviews(
                "Sara Boumerdassi",
                "Average service. Nothing special but not terrible either.",
                3.2,
                "2022",
                R.drawable.userreview7,
                "1"
            ),
            Reviews(
                "Fatima Zeroual",
                "Very helpful and caring doctor. I’m truly grateful.",
                4.7,
                "2023",
                R.drawable.userreview8,
                "1"
            ),
            Reviews(
                "Riyad Benameur",
                "Didn’t feel very personal. The care could be more attentive.",
                2.8,
                "2024",
                R.drawable.userreview9,
                "3"
            ),
            Reviews(
                "Noor Khelifa",
                "Modern clinic with friendly staff. A great experience overall.",
                4.6,
                "2022",
                R.drawable.userreview10,
                "2"
            ),
            Reviews(
                "Nesrine Haddad",
                "Quick, professional, and efficient! Highly appreciated.",
                4.3,
                "2021",
                R.drawable.userreview11,
                "2"
            )
        )

        val doclist = mutableListOf(
            Doctor(
                "Sabrina Aït Ahmed",
                R.drawable.doc1,
                1,
                false,
                getString(R.string.dentiste),
                "Boumerdes",
                reviewsList
                    .filter { it.doctorId.toInt() == 1 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                85,
                7,
                "Chirurgienne-dentiste expérimentée, spécialisée dans les soins dentaires esthétiques et les traitements bucco-dentaires.",
                reviewsList.filter { it.doctorId.toInt() == 1 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Friday, "08:00", "16:00"),
                36.7663,
                3.4772
            )
            ,
            Doctor(
                "Lina Bensaïd",
                R.drawable.doc2,
                2,
                false,
                getString(R.string.general),
                "Boudouaou, Boumerdes",
                reviewsList
                    .filter { it.doctorId.toInt() == 2 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                130,
                10,
                "Médecin généraliste avec une approche centrée sur le patient et des années de service public.",
                reviewsList.filter { it.doctorId.toInt() == 2 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Friday, "09:00", "17:00"),
                36.7333,
                3.4090
            ),
            Doctor(
                "Yacine Khelaf",
                R.drawable.doc3,
                3,
                false,
                getString(R.string.vaccination),
                "Boudouaou, Boumerdes",
                reviewsList
                    .filter { it.doctorId.toInt() == 3 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                140,
                8,
                "Médecin généraliste spécialisé en vaccination, assurant la prévention contre les maladies infectieuses.",
                reviewsList.filter { it.doctorId.toInt() == 3 }.size,
                WorkingTimeModel(WeekDay.Tuesday, WeekDay.Thursday, "08:30", "15:30"),
                36.7490,
                3.4063
            ),
            Doctor(
                "Yassmine Benyamina",
                R.drawable.doc4,
                4,
                false,
                getString(R.string.gastronomie),
                "Ouled Heddaj, Boumerdes",
                reviewsList
                    .filter { it.doctorId.toInt() == 4 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                100,
                12,
                "Gastro-entérologue spécialisée dans les troubles digestifs et les maladies de l'appareil digestif.",
                reviewsList.filter { it.doctorId.toInt() == 4 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Saturday, "10:00", "18:00"),
                36.7219,
                3.3422
            ),
            Doctor(
                "Mehdi Cherif",
                R.drawable.doc5,
                5,
                false,
                getString(R.string.cardiologue),
                "Rouiba, Alger",
                reviewsList
                    .filter { it.doctorId.toInt() == 5 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                90,
                6,
                "Cardiologue spécialisée dans la prévention des maladies cardiovasculaires.",
                reviewsList.filter { it.doctorId.toInt() == 5 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Friday, "07:30", "14:30"),
                36.7351,
                3.2800
            ),
            Doctor(
                "Nabil Aghiles",
                R.drawable.doc6,
                6,
                false,
                getString(R.string.neurologie),
                "Zemmouri, Boumerdes",
                reviewsList
                    .filter { it.doctorId.toInt() == 6 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                110,
                9,
                "Neurologue reconnu pour son expertise dans les troubles du système nerveux.",
                reviewsList.filter { it.doctorId.toInt() == 6 }.size,
                WorkingTimeModel(WeekDay.Tuesday, WeekDay.Saturday, "09:00", "16:00"),
                36.7811,
                3.5767
            ),
            Doctor(
                "Walid bouzid",
                R.drawable.doc7,
                7,
                false,
                getString(R.string.dentiste),
                "Tizi Ouzou",
                reviewsList
                    .filter { it.doctorId.toInt() == 7 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                180,
                11,
                "Dentiste passionnée par la santé bucco-dentaire et l’esthétique du sourire.",
                reviewsList.filter { it.doctorId.toInt() == 7 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Friday, "10:00", "17:00"),
                36.7069,
                4.0497
            ),
            Doctor(
                "Zine El Abidine Meziane",
                R.drawable.doc8,
                8,
                false,
                getString(R.string.pneumologie),
                "Bejaia",
                reviewsList
                    .filter { it.doctorId.toInt() == 8 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                70,
                5,
                "Spécialiste en maladies respiratoires, notamment l'asthme et les allergies.",
                reviewsList.filter { it.doctorId.toInt() == 8 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Saturday, "08:30", "15:30"),
                36.7509,
                5.0567
            ),
            Doctor(
                "Lina Kessaci",
                R.drawable.doc9,
                9,
                false,
                getString(R.string.gastronomie),
                "El Mouradia, Alger",
                reviewsList
                    .filter { it.doctorId.toInt() == 9 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                95,
                8,
                "Gastrologue expérimentée dans les troubles digestifs chroniques.",
                reviewsList.filter { it.doctorId.toInt() == 9 }.size,
                WorkingTimeModel(WeekDay.Tuesday, WeekDay.Friday, "09:00", "16:00"),
                36.7525,
                3.0419
            ),
            Doctor(
                "Nour El Houda Benali",
                R.drawable.doc10,
                10,
                false,
                getString(R.string.laboratory),
                "Bir Mourad Rais, Alger",
                reviewsList
                    .filter { it.doctorId.toInt() == 10 }
                    .mapNotNull {
                        it.rating.toString().replace(',', '.').toDoubleOrNull()
                    }
                    .average()
                    .takeIf { !it.isNaN() }
                    ?.let { String.format(Locale.US,"%.1f", it).toDouble() }
                    ?: 0.0,
                65,
                10,
                "Spécialiste en analyses médicales, offrant des services précis et rapides pour vos besoins de laboratoire.",
                reviewsList.filter { it.doctorId.toInt() == 10 }.size,
                WorkingTimeModel(WeekDay.Monday, WeekDay.Thursday, "07:00", "14:00"),
                36.7414,
                3.0504
            )
        )


        val hoslist = mutableListOf(
            HospitalCard(
                id = 1,
                name ="Hôpital Chahids Mahmoudi",
                image = R.drawable.hospital1,
                rating =5.0,
                reviews =30,
                time = "A",
                street ="Tizi ouzou centre",
                mapsUrl = "https://maps.google.com/?cid=10554303022432617085",
                isFavorite = false,
                latitude = 36.7113,
                longitude = 4.0483
            ),
            HospitalCard(
            id = 2,
            name = "Hôpital de Boumerdès",
            image = R.drawable.hospital1, // Remplace par ton image dans drawable
            rating = 4.1,
            reviews = 124,
            time = "B",
            street = "Rue de la Liberté, Centre-ville, Boumerdès 35000, Algérie",
            mapsUrl = "https://maps.google.com/?q=36.7574,3.4722",
            latitude = 36.7574,
            longitude = 3.4722,
            isFavorite = false,
            ),
            HospitalCard(
            id = 3,
            name = "Centre Hospitalier de Djelfa",
            image = R.drawable.hospital1,
            rating = 4.0,
            time = "C",
            reviews = 180,
            street = "com.example.startup_app.viewmodels.Route de Laghouat, Djelfa 17000, Algérie",
            mapsUrl = "https://maps.google.com/?q=34.6736,3.2584",
            latitude = 34.6736,
            longitude = 3.2584,
            isFavorite = false
        ),

            HospitalCard(
                id = 4,
                name = "CHU Mustapha‑Pacha",
                image = R.drawable.hospital1,
                rating = 4.2,
                time = "D",
                reviews = 320,
                street = "Place du 1er Mai 1945, Sidi M’Hamed, Alger Centre, 16000",
                mapsUrl = "https://maps.google.com/?q=36.7620,3.0534",
                latitude = 36.7620,
                longitude = 3.0534,
                isFavorite = false
            ),

            HospitalCard(
                id = 1,
                name = "Hôpital Frantz‑Fanon – Béjaïa",
                image = R.drawable.hospital1,
                rating = 4.1,
                reviews = 150,
                time = "",
                street = "Bordj Moussa, 06000 Béjaïa, Algérie",
                mapsUrl = "https://maps.google.com/?q=36.75623,5.08346",
                latitude = 36.75623,
                longitude = 5.08346,
                isFavorite = false
            )

        )
            if (!DataHolder.loaded) {
                DataHolder.HospitalList = hoslist
                DataHolder.ReviewList = reviewsList
                DataHolder.DoctorList = doclist
                DataHolder.loaded = true
            }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        checkAppointmentsNow(this)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.HOURS
        )
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "appointment_notification_check",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        val gson = Gson()
        viewPager = binding.viewPager
        indicators = mutableListOf()
        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val type = object : TypeToken<MutableList<NotificationDisplayItem.NotificationItem>>() {}.type
        val currentBo: MutableList<NotificationDisplayItem.NotificationItem> = gson.fromJson(
            sharedPref.getString("notifications", null), type
        ) ?: mutableListOf()
        val hasUnreadNotification = currentBo.any { !it.isRead }
        if(hasUnreadNotification) {
            binding.notreaden.visibility = View.VISIBLE
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewPager.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
        val bookingsJson = sharedPref.getString("bookings", null)
        val bookingsType = object : TypeToken<MutableList<Booking>>() {}.type
        val bookings: MutableList<Booking> = gson.fromJson(bookingsJson, bookingsType) ?: mutableListOf()
        val notifJson = sharedPref.getString("notifications", null)
        val notifType = object : TypeToken<MutableList<NotificationDisplayItem.NotificationItem>>() {}.type
        val existingNotifications: MutableList<NotificationDisplayItem.NotificationItem> =
            gson.fromJson(notifJson, notifType) ?: mutableListOf()
        bookings.forEach {
            val fullDate = it.date
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateParsed = parser.parse(fullDate)
            val outputFormatter = SimpleDateFormat("EEEE - d MMMM yyyy", Locale.getDefault())
            val todayCalendar = Calendar.getInstance()
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
            todayCalendar.set(Calendar.MINUTE, 0)
            todayCalendar.set(Calendar.SECOND, 0)
            todayCalendar.set(Calendar.MILLISECOND, 0)
            val localizedDate = outputFormatter.format(dateParsed!!)
            val appointmentCalendar = Calendar.getInstance()
            appointmentCalendar.time = dateParsed
            appointmentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            appointmentCalendar.set(Calendar.MINUTE, 0)
            appointmentCalendar.set(Calendar.SECOND, 0)
            appointmentCalendar.set(Calendar.MILLISECOND, 0)
            val currentLocale = Locale.getDefault()
            val language = currentLocale.language
            val message = if (language == "fr") {
                "Votre rendez-vous le $localizedDate est maintenant marqué comme terminé."
            } else {
                "Your appointment on $localizedDate is now marked as completed."
            }
            if (appointmentCalendar.before(todayCalendar) && it.status == "upcoming") {
                it.status = "completed"
                val newNotif = NotificationDisplayItem.NotificationItem(
                    type = "success",
                    title = "Appointment Completed",
                    message = message,
                    time = "",
                )
                existingNotifications.add(0, newNotif)
            }

        }
        sharedPref.edit()
            .putString("bookings", gson.toJson(bookings))
            .putString("notifications", gson.toJson(existingNotifications))
            .apply()

        checkLocationSettings()
        setupViewPager()
        displayhospitals()

        binding.seealldoc.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.gomap.setOnClickListener {
            val i = Intent(this@HomeActivity, MapsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.prfl.setOnClickListener {
            val i = Intent(this@HomeActivity, ProfileActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.clndr.setOnClickListener {
            val i = Intent(this@HomeActivity, BookingActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.ntf.setOnClickListener {
            val i = Intent(this@HomeActivity, NotificationActivity::class.java).apply {
                putExtra("where","home")
            }
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
        binding.searchinput.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.denti.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.dentiste))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.cardio.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.cardiologue))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.pulmo.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.pneumologie))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.general.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.general))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.neuro.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.neurologie))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.gastro.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.gastronomie))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.labo.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.laboratory))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.vacc.setOnClickListener {
            val i = Intent(this@HomeActivity, AllDoctorsActivity::class.java).apply {
                putExtra("enabled", getString(R.string.vaccination))
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0).toBundle()
            startActivity(i, options)
        }
        binding.allhospitals.setOnClickListener {
            val i = Intent(this@HomeActivity, AllHospitalsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            startActivity(i, options)
        }
    }

    private fun checkAppointmentsNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(request)
        Log.d("MainActivity", "✅ checkAppointmentsNow: Worker lancé manuellement")
    }
    val lang: String = Locale.getDefault().language
    val isFr = lang == "fr"
    private fun checkLocationSettings() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, if(isFr) "Activez la localisation dans les paramètres." else "Enable location in the settings.", Toast.LENGTH_LONG)
                .show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            if (hasLocationPermission()) {
                fetchLocation()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun setupViewPager() {
        val imgList = arrayListOf(
            Carouselmages(
                R.drawable.first_doc,
                if (isFr) "Votre santé\nmérite le meilleur" else "Your health\ndeserves the best",
                if (isFr) "Réservez une consultation\navec des médecins à l'écoute." else "Book a consultation\nwith caring doctors."
            ),
            Carouselmages(
                R.drawable.second_doc,
                if (isFr) "Évitez\nles longues attentes" else "Avoid\nlong waiting times",
                if (isFr) "Accès prioritaire à\nnos meilleurs spécialistes." else "Priority access to\nour top specialists."
            ),
            Carouselmages(
                R.drawable.third_doc,
                if (isFr) "Des soins experts\nà un clic" else "Expert care\njust one click away",
                if (isFr) "Prenez rendez-vous en ligne\nen moins d'une minute." else "Book an appointment online\nin under a minute."
            ),
            Carouselmages(
                R.drawable.fourth_doc,
                if (isFr) "Vous cherchez un\nmédecin spécialiste ?" else "Looking for a\nspecialist doctor?",
                if (isFr) "Prenez rendez-vous avec\nnos meilleurs médecins." else "Book an appointment with\nour top doctors."
            )
        )


        val adapter = CarouselmagesAdapter(this, imgList)
        viewPager.adapter = adapter
        setupIndicators(imgList.size)
        setCurrentIndicator(0)

        runnable = object : Runnable {
            override fun run() {
                if (currentPage >= adapter.itemCount) currentPage = 0
                viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, delay)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                setCurrentIndicator(position)
                resetAutoScroll()
            }
        })

        handler.postDelayed(runnable, delay)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Toast.makeText(this, if(isFr) "Permission refusée" else "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun fetchLocation() {
        if (!hasLocationPermission()) return

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)


                            runOnUiThread {
                                if (!addresses.isNullOrEmpty()) {
                                    var state = addresses[0].adminArea ?: ""
                                    state = state.replace(" province", "", ignoreCase = true)
                                    val country = addresses[0].countryName ?: "Pays inconnu"

                                    val result = listOf(state, country)
                                        .filter { it.isNotEmpty() }
                                        .joinToString(", ")


                                    binding.mylocation.text = result
                                } else {
                                    binding.mylocation.text = if (isFr) "Non Trouvé" else "Not Found"
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                binding.mylocation.text = if (isFr) "Adresse introuvable" else "Address not found"
                                Log.e("HomeActivity", "Geocoding error", e)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    if (isFr) "Localisation non disponible pour le moment" else "Location not available at the moment",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("HomeActivity", "Location fetch error", it)
            }
    }

    private fun setupIndicators(count: Int) {
        indicators.clear()
        binding.indicatorLayout.removeAllViews()

        for (i in 0 until count) {
            val indicator = View(this).apply {
                val size = 8.toPx()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginStart = 8.toPx()
                    marginEnd = 8.toPx()
                }
                setBackgroundResource(R.drawable.bg_rounded_grey)
            }
            indicators.add(indicator)
            binding.indicatorLayout.addView(indicator)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        indicators.forEachIndexed { i, view ->
            if (i == index) {
                view.setBackgroundResource(R.drawable.bg_dotted_white)
                animateIndicatorWidth(view, 30)
            } else {
                view.setBackgroundResource(R.drawable.bg_rounded_grey)
                animateIndicatorWidth(view, 10)
            }
        }
    }

    private fun animateIndicatorWidth(view: View, targetWidthDp: Int) {
        val scale = resources.displayMetrics.density
        val targetWidthPx = (targetWidthDp * scale + 0.5f).toInt()

        val widthAnimator = ValueAnimator.ofInt(view.width, targetWidthPx)
        widthAnimator.addUpdateListener { animator ->
            val params = view.layoutParams
            params.width = animator.animatedValue as Int
            view.layoutParams = params
        }
        widthAnimator.duration = 300
        widthAnimator.start()
    }


    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun resetAutoScroll() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, delay)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        viewPager.unregisterOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
    }

    private fun displayhospitals() {

        val adapter = HospitalCardAdapter(this, DataHolder.HospitalList, false) {}
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation ?: return
                val newLatLng = LatLng(loc.latitude, loc.longitude)
                userLocation = newLatLng
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 17f))


            }
        }
    }

}