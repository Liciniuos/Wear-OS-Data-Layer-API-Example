package com.sideflipstudios.simpledatasend.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.sideflipstudios.common.CustomParcelUtils
import com.sideflipstudios.common.ReturnClass
import com.sideflipstudios.common.SendClass
import com.sideflipstudios.simpledatasend.R
import com.sideflipstudios.simpledatasend.databinding.ActivityMainBinding
import java.time.LocalDateTime

// Main activity for the Wear app, implements DataClient.OnDataChangedListener to handle data updates from the Data Layer
class MainWearActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMark.setOnClickListener { sendObjectWithAction(true) }
        binding.btnDelete.setOnClickListener { sendObjectWithAction(false) }

    }

    // Sends SendClass back to mobile with the required action attached
    private fun sendObjectWithAction(action: Boolean) {
        // Get data from SharedPreferences
        // sentData is non null asserted (!!) because
        // the buttons that trigger this function are hidden if the data is null
        val sentData = SharedPrefs.getFromSharedPreferences(this)!!

        // Create return data
        val returnData = ReturnClass(
            sentData,
            action
        )

        // Use CustomParcelUtils to create a ByteArray suitable for the data layer
        val byteArray = CustomParcelUtils.toByteArray(returnData)

        // Create a DataMapRequest with a specified path ("/mobile_update")
        // '/mobile_update' can be changed as long as it is consistent in all usages
        val dataMap = PutDataMapRequest.create("/mobile_update")

        // Add the serialized data (ByteArray) to the DataMap with a key ("return_class")
        //Again the key can be changed as long as it is consistent
        dataMap.dataMap.putByteArray("return_class", byteArray)
        val request = dataMap.asPutDataRequest()

        // Convert the DataMap into a PutDataRequest and send to the watch
        Wearable.getDataClient(this).putDataItem(request)

    }

    // Callback for when data is received through the Data Layer
    // This is here as well as the TaskUpdateService as it allows easier access to update the UI
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            // Check if the event is a data change and matches the "/sync_data" path
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/sync_data") {

                // Retrieve the byte array from the DataMap
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val byteArray = dataMap.getByteArray("sync_key")

                if (byteArray != null) {
                    // Deserialize the byte array into a SendClass object
                    val syncedData: SendClass = CustomParcelUtils.fromByteArray(byteArray)
                    // Update the UI with the synced data
                    runOnUiThread { setUI(syncedData) }
                } else {
                    // Clear the UI if no data is received
                    runOnUiThread { setUI(null) }
                }
            }
        }
    }

    // Updates the UI with the given data
    private fun setUI(data: SendClass?) {
        SharedPrefs.saveToSharedPreferences(this, data)

        if (data != null) {
            // Display task name and time
            binding.tvName.text = data.name
            binding.tvTime.text = "${data.dateTime.hour}:${data.dateTime.minute}"
            // Make the two buttons visible
            binding.llButtons.visibility = View.VISIBLE

        } else {
            // Clear UI
            binding.tvName.text = "Nothing to show"

            // Sets tvTime to the current time in 12 hour HH:MM AM/PM format
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
            // Make the two buttons invisible so the app doesn't try to send a null ReturnClass
            binding.llButtons.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Indicate that the main activity is active
        // This prevents TaskUpdateService from receiving the data layer change
        SharedPrefs.mainActivityActive = true
        // Register the DataClient listener
        Wearable.getDataClient(this).addListener(this)
        // Set the UI to the saved data
        runOnUiThread { setUI(SharedPrefs.getFromSharedPreferences(this)) }
    }

    override fun onPause() {
        super.onPause()
        // Indicate that the main activity is no longer active
        // TaskUpdateService should handle any data layer changes
        SharedPrefs.mainActivityActive = false
        // Unregister the DataClient listener
        // The listener from this activity should not stay open because:
        // It may cause a memory leak
        // It will try to update a UI that may not exist which may cause errors
        Wearable.getDataClient(this).removeListener(this)
    }
}