package com.example.startup_app.activities

import com.example.startup_app.viewmodels.NotificationDisplayItem
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.startup_app.R
import com.example.startup_app.adapters.NotificationAdapter
import com.example.startup_app.databinding.ActivityNotificationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationActivity : BaseActivity() {
    lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    lateinit var i : Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val from = intent.getStringExtra("where")
        i = if(from == "home") {
            Intent(this@NotificationActivity,
                HomeActivity::class.java
            )
        } else {
            Intent(this@NotificationActivity,
                ProfileActivity::class.java
            )
        }
        binding.rtn.setOnClickListener {
            startActivity(i, ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }
        val recycler = binding.recyclerViewNotifications
        adapter = NotificationAdapter(this, emptyList())
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val notifType = object : TypeToken<MutableList<NotificationDisplayItem.NotificationItem>>() {}.type
        val notifications: MutableList<NotificationDisplayItem.NotificationItem> =
            gson.fromJson(sharedPref.getString("notifications", null), notifType) ?: mutableListOf()
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = recycler.layoutManager as LinearLayoutManager
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                for (i in firstVisible..lastVisible) {
                    val item = notifications.getOrNull(i) ?: continue

                    if (!item.isRead) {
                        item.isRead = true
                        adapter.notifyItemChanged(i)
                    }
                }
                sharedPref.edit()
                    .putString("notifications", gson.toJson(notifications))
                    .apply()
            }
        })
        loadNotifications()
    }

    private fun loadNotifications() {
        val gson = Gson()
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val notifJson = sharedPref.getString("notifications", null)
        val type = object : TypeToken<List<NotificationDisplayItem.NotificationItem>>() {}.type

        val storedNotifications: List<NotificationDisplayItem.NotificationItem> =
            gson.fromJson(notifJson, type) ?: emptyList()

        val resultList = mutableListOf<NotificationDisplayItem>()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val grouped = storedNotifications.groupBy { notif ->
            val cal = Calendar.getInstance().apply { timeInMillis = notif.timestamp }
            "%04d-%02d-%02d".format(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        val sortedGroups = grouped.toSortedMap(compareByDescending { it })

        for ((dateKey, notifications) in sortedGroups) {
            val dateCal = Calendar.getInstance()
            dateCal.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateKey)!!

            val header = when {
                isSameDay(dateCal, today) -> NotificationDisplayItem.Header(getString(R.string.today))
                isSameDay(dateCal, yesterday) -> NotificationDisplayItem.Header(getString(R.string.yesterday))
                else -> {
                    val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    NotificationDisplayItem.Header(format.format(dateCal.time))
                }
            }

            resultList.add(header)
            val updatedNotifs = notifications.map {
                it.copy(time = getRelativeTime(it.timestamp))
            }

            resultList.addAll(updatedNotifs)
        }

        adapter.updateItems(resultList)
    }
    private fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> getString(R.string.time_less_than_minute)
            minutes == 1L -> getString(R.string.time_one_minute)
            minutes < 60 -> getString(R.string.time_minutes, minutes)
            hours == 1L -> getString(R.string.time_one_hour)
            hours < 24 -> getString(R.string.time_hours, hours)
            days == 1L -> getString(R.string.time_yesterday)
            else -> getString(R.string.time_days, days)
        }
    }
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


}