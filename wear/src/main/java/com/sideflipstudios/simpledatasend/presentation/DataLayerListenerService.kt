package com.sideflipstudios.simpledatasend.presentation

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.sideflipstudios.common.CustomParcelUtils
import com.sideflipstudios.common.SendClass
import com.sideflipstudios.simpledatasend.presentation.SharedPrefs.saveToSharedPreferences

// Callback for handling data changes from the Data Layer, triggered when new data arrives
class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        if (!SharedPrefs.mainActivityActive) { // Prevents action if the app is open
            for (event in dataEvents) {
                //if the data is new and is on the path relevant to the wear app
                if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/sync_data") {

                    // Retrieve the byte array from the DataMap
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val byteArray = dataMap.getByteArray("sync_key")

                    if (byteArray != null) {
                        // If the byte array is not null, deserialize it into a SendClass object
                        val syncedData: SendClass = CustomParcelUtils.fromByteArray(byteArray)
                        // Save to SharedPreferences to be used when the app is opened
                        saveToSharedPreferences(this, syncedData)
                    } else {
                        // If the byte array is null, clear the saved task in SharedPreferences
                        saveToSharedPreferences(this, null)
                    }
                }
            }
        }
    }
}