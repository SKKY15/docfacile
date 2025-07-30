package com.example.startup_app.activities

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.startup_app.R
import com.example.startup_app.adapters.DoctorAdapter
import com.example.startup_app.databinding.ActivityAllDoctorsBinding
import com.example.startup_app.objects.DataHolder
import com.example.startup_app.viewmodels.Doctor
import com.google.gson.Gson
import java.util.Locale

class AllDoctorsActivity : BaseActivity() {
    lateinit var binding: ActivityAllDoctorsBinding
    private lateinit var fullList: MutableList<Doctor>
    private lateinit var currentList: MutableList<Doctor>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAllDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.getStringExtra("enabled")
        val buttons = listOf(
            binding.buttonAll,
            binding.buttonGeneral,
            binding.buttonDentist,
            binding.buttonLaboratory,
            binding.buttonGastrology,
            binding.buttonNeurologist,
            binding.buttonVaccination,
            binding.buttonPulmonologist,
            binding.buttonCardiologist
        )
        fullList = DataHolder.DoctorList
        currentList = fullList
        val lang = Locale.getDefault().language
        val isFr = lang == "fr"
        binding.tresult.text = if(isFr) "${fullList.size} trouvés" else "${fullList.size} founds"
        val adapter = DoctorAdapter(this, fullList, false) {}
        binding.recy.adapter = adapter
        binding.recy.isNestedScrollingEnabled = false
        binding.recy.layoutManager = LinearLayoutManager(this)
        binding.returnarr.setOnClickListener {
            startActivity(
                Intent(this@AllDoctorsActivity
                ,HomeActivity::class.java),
                ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ).toBundle()
            )
        }
        var selectedSpeciality: String?
        var searchText = ""
        var sortOption: Int = R.id.order_default
        selectedSpeciality = if (intent == null) {
            getString(R.string.allspc)
        } else {
            intent.toString()
        }
        fun applyFiltersAndSort() {
            var filtered = fullList


            if (!selectedSpeciality.isNullOrBlank() && selectedSpeciality != getString(R.string.allspc)) {
                filtered =
                    filtered.filter { doc ->
                        doc.speciality.equals(
                            selectedSpeciality,
                            ignoreCase = true
                        )
                    }
                        .toMutableList()
            }


            if (searchText.isNotBlank()) {
                filtered =
                    filtered.filter { doc -> doc.name.contains(searchText, ignoreCase = true) }
                        .toMutableList()
            }


            filtered = when (sortOption) {
                R.id.order_rating -> filtered.sortedByDescending { it.rating }.toMutableList()
                R.id.order_reviews -> filtered.sortedByDescending { it.reviews }.toMutableList()
                R.id.order_name -> filtered.sortedBy { it.name.lowercase() }.toMutableList()
                else -> filtered
            }
            var txt = searchText
            if (searchText.isEmpty()) {
                txt = selectedSpeciality.toString()
            }
            if (filtered.isEmpty()) {
                binding.recy.visibility = View.GONE
                binding.msg.visibility = View.VISIBLE
                binding.msg.text = if(isFr) "Aucun résultat trouvé pour \"$txt\"" else "No result found for \"$txt\""
                binding.sort.visibility = View.GONE
            } else {
                binding.recy.visibility = View.VISIBLE
                binding.msg.visibility = View.GONE
            }

            currentList = filtered
            adapter.updateList(filtered)
            binding.tresult.text = if(isFr) "${filtered.size} trouvés" else "${filtered.size} founds"
        }

        buttons.forEach { button ->

            button.isSelected = false
            button.setTextColor(Color.parseColor("#1C2A3A"))
            button.setOnClickListener { view ->
                buttons.forEach {
                    it.isSelected = false
                    it.setTextColor(Color.parseColor("#1C2A3A"))
                }

                view.isSelected = true
                (view as Button).setTextColor(Color.WHITE)

                selectedSpeciality = if (view.text != getString(R.string.allspc)) view.text.toString() else getString(R.string.allspc)
                applyFiltersAndSort()

            }
        }
        if (selectedSpeciality == getString(R.string.allspc)) {
            binding.buttonAll.isSelected = true
            binding.buttonAll.setTextColor(Color.WHITE)
        } else {
            val name = intent.toString()
            val btn = buttons.find { it.text == name }
            if (btn != null) {
                btn.isSelected = true
                btn.setTextColor(Color.WHITE)
                applyFiltersAndSort()
            } else {
                binding.buttonAll.isSelected = true
                binding.buttonAll.setTextColor(Color.WHITE)
            }
        }
        binding.searchinput1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString().lowercase().trim()
                applyFiltersAndSort()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        adapter.onItemClick = {

            val i = Intent(this@AllDoctorsActivity, DoctorDetailsActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            i.putExtra("name", it.name)
            i.putExtra("id", "${it.id}")
            i.putExtra("rating", it.rating)
            i.putExtra("reviews", it.reviews)
            i.putExtra("speciality", it.speciality)
            i.putExtra("street", it.street)
            i.putExtra("image", it.image)
            i.putExtra("exp", it.experienceYear)
            val gson = Gson()
            val workingTimeJson = gson.toJson(it.workingTime)
            i.putExtra("workingTime", workingTimeJson)
            i.putExtra("patients", it.numPatients)
            i.putExtra("bio", it.bio)
            startActivity(i, options)
        }

        binding.sort.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_order, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                sortOption = item.itemId
                binding.sort.text = when (item.itemId) {
                    R.id.order_rating -> getString(R.string.obra)
                    R.id.order_reviews -> getString(R.string.obre)
                    R.id.order_name -> getString(R.string.obn)
                    else -> getString(R.string.dflt)
                }
                applyFiltersAndSort()
                true
            }
            popup.show()
        }


    }
}