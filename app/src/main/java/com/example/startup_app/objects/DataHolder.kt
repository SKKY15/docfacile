package com.example.startup_app.objects

import com.example.startup_app.viewmodels.Doctor
import com.example.startup_app.viewmodels.HospitalCard
import com.example.startup_app.viewmodels.Reviews

object DataHolder {
    var DoctorList: MutableList<Doctor> = mutableListOf()
    var HospitalList: MutableList<HospitalCard> = mutableListOf()
    var ReviewList : MutableList<Reviews> = mutableListOf()
    var loaded : Boolean = false
}