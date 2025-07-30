package com.example.startup_app.activities

import com.example.startup_app.viewmodels.DirectionsResponse
import com.example.startup_app.retrofit.RetrofitClient
import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.BuildConfig
import com.example.startup_app.R
import com.example.startup_app.adapters.HospitalCardAdapter
import com.example.startup_app.databinding.ActivityMapsBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.Doctor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MapsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var shouldFollowUser = true
    private var isLocationPermissionGranted = false
    private var markerAnimator: ValueAnimator? = null
    private var userLocation: LatLng? = null
    private val locationpermissioncoderequest = 1
    private var lastClickedDoctorId: Int? = null
    private var selectedDoctorForMaps: Doctor? = null
    private val routePolylines = mutableListOf<Polyline>()
    private val doctorMarkers = mutableListOf<Marker>()
    val lang: String = Locale.getDefault().language
    val isFr = lang == "fr"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        setupLocationCallback()
        val recycler = binding.recc
        val adapter = HospitalCardAdapter(this, DataHolder.HospitalList, false) {}
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.locateme.setOnClickListener {
            userLocation?.let {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
            }
        }
        binding.prfl.setOnClickListener {
            startActivity(
                Intent(this@MapsActivity, ProfileActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.clndr.setOnClickListener {
            startActivity(
                Intent(this@MapsActivity, BookingActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.gohome.setOnClickListener {
            startActivity(
                Intent(this@MapsActivity, HomeActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        binding.searchinput1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    val matchingDoctor = DataHolder.DoctorList.firstOrNull {
                        it.name.contains(query, ignoreCase = true)
                    }

                    if (matchingDoctor != null) {
                        val doctorLatLng = LatLng(matchingDoctor.latitude, matchingDoctor.longitude)
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(doctorLatLng, 17f)
                        )

                        Toast.makeText(
                            this@MapsActivity,
                            "Médecin trouvé : ${matchingDoctor.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        userLocation?.let {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
                            Toast.makeText(
                                this@MapsActivity,
                                "Aucun médecin trouvé",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })

        binding.btnOpenMaps.setOnClickListener {
            selectedDoctorForMaps?.let { doctor ->
                val uri =
                    Uri.parse("geo:0,0?q=${doctor.latitude},${doctor.longitude}(${doctor.name})")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, if (isFr) "Google Maps n'est pas installé" else "Google Maps is not installed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )

            if (!success) {
                Log.e("MapStyle", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapStyle", "Can't find style. Error: ", e)
        }
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        if (hasLocationPermission()) {
            isLocationPermissionGranted = true
            startLocationUpdates()
        } else {
            requestLocationPermission()
        }
        addDoctorMarkers()
        googleMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                shouldFollowUser = false
            }
        }
        googleMap.setOnMarkerClickListener { marker ->
            val selectedDoctor = marker.tag as? Doctor ?: return@setOnMarkerClickListener true
            if (selectedDoctor.id == lastClickedDoctorId) {
                routePolylines.forEach { it.remove() }
                routePolylines.clear()
                marker.setIcon(getCustomMarker(this, selectedDoctor.image))
                lastClickedDoctorId = null
                binding.recc.visibility = View.VISIBLE
                binding.dd.visibility = View.GONE
                binding.locateme.visibility = View.GONE
                binding.btnOpenMaps.visibility = View.GONE
                selectedDoctorForMaps = null
                addDoctorMarkers()
            } else {
                routePolylines.forEach { it.remove() }
                routePolylines.clear()
                drawRouteWithRetrofit(marker.position) {
                    binding.btnOpenMaps.visibility = View.VISIBLE
                    selectedDoctorForMaps = selectedDoctor
                }
                hideOtherDoctorMarkers(selectedDoctor.id)
                lastClickedDoctorId = selectedDoctor.id
            }

            true
        }

    }


    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationpermissioncoderequest
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationpermissioncoderequest
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
            if (::googleMap.isInitialized) startLocationUpdates()
        } else {
            Toast.makeText(this, if (isFr) "Permission localisation refusée" else "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation ?: return
                val newLatLng = LatLng(loc.latitude, loc.longitude)
                userLocation = newLatLng
                if (shouldFollowUser) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 17f))
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!::googleMap.isInitialized) return

        googleMap.isMyLocationEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(1000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }





    private fun drawRouteWithRetrofit(
        destinationLatLng: LatLng,
        onRouteDrawn: (() -> Unit)? = null
    ) {
        val originLatLng = userLocation
        if (originLatLng == null) {
            Toast.makeText(this, if (isFr) "Position utilisateur inconnue" else "User location unknown"
                , Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(
            this,
            if (isFr) "Veuillez patienter, l'itinéraire est en cours de génération..." else "Please wait, the route is being generated..."
            ,
            Toast.LENGTH_SHORT
        ).show()
        val origin = "${originLatLng.latitude},${originLatLng.longitude}"
        val destination = "${destinationLatLng.latitude},${destinationLatLng.longitude}"

        val call = RetrofitClient.directionsApiService.getDirections(
            origin = origin,
            destination = destination,
            apiKey = BuildConfig.GOOGLE_API_KEY

        )

        call.enqueue(object : Callback<DirectionsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                if (!response.isSuccessful || response.body()?.routes.isNullOrEmpty()) {
                    lastClickedDoctorId = null
                    selectedDoctorForMaps = null
                    addDoctorMarkers()
                    Toast.makeText(this@MapsActivity, if (isFr) "Aucun itinéraire trouvé" else "No route found", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                routePolylines.forEach { it.remove() }
                routePolylines.clear()
                val route = response.body()?.routes?.firstOrNull()
                val leg = route?.legs?.firstOrNull()
                if (leg == null) {
                    Toast.makeText(
                        this@MapsActivity,
                        if (isFr) "Pas d'étapes dans l'itinéraire" else "No steps in the route"
                        ,
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val distanceText = leg.distance.text
                val durationText = leg.duration.text
                binding.recc.visibility = View.GONE
                binding.dandd.text = "$durationText / $distanceText"
                binding.dd.visibility = View.VISIBLE
                binding.locateme.visibility = View.VISIBLE
                for (step in leg.steps) {
                    val decodedPoints = PolyUtil.decode(step.polyline.points)
                    val normalDuration = step.duration.value
                    val trafficDuration = step.durationInTraffic?.value ?: normalDuration
                    val delayRatio = trafficDuration.toDouble() / normalDuration

                    val trafficColor = when {
                        delayRatio > 1.5 -> Color.RED
                        delayRatio > 1.2 -> Color.parseColor("#FF9A00")
                        else -> Color.GREEN
                    }

                    val polyline = googleMap.addPolyline(
                        PolylineOptions()
                            .addAll(decodedPoints)
                            .color(trafficColor)
                            .width(10f)
                            .startCap(RoundCap())
                            .endCap(RoundCap())
                            .jointType(JointType.ROUND)
                    )
                    routePolylines.add(polyline)
                    onRouteDrawn?.invoke()

                }


            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Toast.makeText(
                    this@MapsActivity,
                    if (isFr) "Erreur réseau : ${t.localizedMessage}" else "Network error: ${t.localizedMessage}"
                    ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    @SuppressLint("InflateParams")
    fun getCustomMarker(context: Context, @DrawableRes profileImageResId: Int): BitmapDescriptor {
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)
        val profileImage: ImageView = markerView.findViewById(R.id.profile_image)
        profileImage.setImageResource(profileImageResId)
        markerView.scaleX = 0.6f
        markerView.scaleY = 0.6f
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            markerView.measuredWidth,
            markerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addDoctorMarkers() {
        for (doctor in DataHolder.DoctorList) {
            val markerOptions = MarkerOptions()
                .position(LatLng(doctor.latitude, doctor.longitude))
                .title(doctor.name)
                .icon(getCustomMarker(this, doctor.image))
            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = doctor
            marker?.let { doctorMarkers.add(it) }
        }
    }

    private fun hideOtherDoctorMarkers(selectedId: Int) {
        for (marker in doctorMarkers) {
            val doctor = marker.tag as? Doctor
            if (doctor != null) {
                marker.isVisible = (doctor.id == selectedId)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if (isLocationPermissionGranted) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        markerAnimator?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}
