package com.sideflipstudios.simpledatasend.presentation

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import com.sideflipstudios.simpledatasend.R
import androidx.preference.PreferenceManager
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sideflipstudios.simpledatasend.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMark.setOnClickListener { sendMarkOrDelete(true) }
        binding.btnDelete.setOnClickListener { sendMarkOrDelete(false) }

    }

    private fun sendMarkOrDelete(isMark: Boolean) {

        val dataMapRequest = PutDataMapRequest.create("/task_update")
        val dataMap = dataMapRequest.dataMap
        dataMap.putBoolean("is_mark", isMark)
        val request = dataMapRequest.asPutDataRequest()
        Wearable.getDataClient(this).putDataItem(request)

    }

    private fun loadSavedTask() {
        val savedTask: MinimalTask? = Gson().fromJson(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.txtSavedTaskKey), null),
            object : TypeToken<MinimalTask?>() {}.type
        )

        runOnUiThread { setUI(savedTask) }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/sync_data") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val byteArray = dataMap.getByteArray("sync_key")

                if (byteArray != null) {
                    val syncedData: MinimalTask = CustomParcelUtils.fromByteArray(byteArray)
                    runOnUiThread { setUI(syncedData) }
                } else {
                    runOnUiThread { setUI(null) }
                }
            }
        }
    }

    private fun setUI(task: MinimalTask?) {
        SharedPrefs.saveToSharedPreferences(task, this)
        if (task != null) {
            binding.tvName.text = task.name

            val fromDateTime = LocalDateTime.now()

            val toDateTime = task.dateTime

            var timeUntilDateTime = LocalDateTime.from(fromDateTime)

            val years = timeUntilDateTime.until(toDateTime, ChronoUnit.YEARS)
            timeUntilDateTime = timeUntilDateTime.plusYears(years)

            val months = timeUntilDateTime.until(toDateTime, ChronoUnit.MONTHS)
            timeUntilDateTime = timeUntilDateTime.plusMonths(months)

            val days = timeUntilDateTime.until(toDateTime, ChronoUnit.DAYS)
            timeUntilDateTime = timeUntilDateTime.plusDays(days)


            val hours = timeUntilDateTime.until(toDateTime, ChronoUnit.HOURS)
            timeUntilDateTime = timeUntilDateTime.plusHours(hours)

            val minutes = timeUntilDateTime.until(toDateTime, ChronoUnit.MINUTES)

            val until = if (years.toInt() != 0) {
                if (-6 <= months && months <= 6) {
                    "$years year" + if (1 < years) {
                        "s"
                    } else {
                        ""
                    }
                } else {
                    "$years.5 years"
                }
            } else if (months.toInt() != 0) {
                if (-14 <= days && days <= 14) {
                    "$months month" + if (months.toInt() != 1 && months.toInt() != -1) {
                        "s"
                    } else {
                        ""
                    }
                } else {
                    "$months.5 months"
                }
            } else if (14 <= days || days <= -14) {
                if (-3 <= (days % 7) && (days % 7) <= 3) {
                    "${days / 7} weeks"
                } else {
                    "${(days / 7)}.5 weeks"
                }
            } else if (days.toInt() != 0) {
                if (-12 <= hours && hours <= 12) {
                    "$days day" + if (days.toInt() != 1 && days.toInt() != -1) {
                        "s"
                    } else {
                        ""
                    }
                } else {
                    "$days.5 days"
                }
            } else if (hours.toInt() != 0) {
                if (-30 <= minutes && minutes <= 30) {
                    "$hours hour" + if (hours.toInt() != 1 && hours.toInt() != -1) {
                        "s"
                    } else {
                        ""
                    }
                } else {
                    "$hours.5 hours"
                }
            } else if (minutes.toInt() != 0) {
                "$minutes minute" + if (minutes.toInt() != 1 && minutes.toInt() != -1) {
                    "s"
                } else {
                    ""
                }
            } else {
                "NOW!!!"
            }

            binding.tvTime.text = if (until == "NOW!!!") {
                until
            } else if ("-" in until) {
                "${until.subSequence(1, until.length)} ago"

            } else {
                "In $until"
            }

        } else {
            binding.tvName.setText(R.string.txtNoSavedTask)
            val tempDateTime = LocalDateTime.now()
            binding.tvTime.text = if (tempDateTime.hour < 12) {
                getString(
                    R.string.txt12Time,
                    tempDateTime.hour.toString().padStart(2, '0'),
                    tempDateTime.minute.toString().padStart(2, '0'),
                    "AM"
                )
            } else if (tempDateTime.hour == 12) {
                getString(
                    R.string.txt12Time,
                    tempDateTime.hour.toString().padStart(2, '0'),
                    tempDateTime.minute.toString().padStart(2, '0'),
                    "PM"
                )
            } else {
                getString(
                    R.string.txt12Time,
                    (tempDateTime.hour - 12).toString().padStart(2, '0'),
                    tempDateTime.minute.toString().padStart(2, '0'),
                    "PM"
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SharedPrefs.mainActivityActive = true
        Wearable.getDataClient(this).addListener(this)
        loadSavedTask()
    }

    override fun onPause() {
        super.onPause()
        SharedPrefs.mainActivityActive = false
        Wearable.getDataClient(this).removeListener(this)
    }
}