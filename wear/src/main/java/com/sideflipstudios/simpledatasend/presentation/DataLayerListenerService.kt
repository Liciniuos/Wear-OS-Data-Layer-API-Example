package com.sideflipstudios.simpledatasend.presentation

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.sideflipstudios.simpledatasend.presentation.SharedPrefs.saveToSharedPreferences

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        if (!SharedPrefs.mainActivityActive) {
            for (event in dataEvents) {
                if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/sync_data") {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val byteArray = dataMap.getByteArray("sync_key")

                    if (byteArray != null) {
                            val syncedData: MinimalTask = CustomParcelUtils.fromByteArray(byteArray)
                            saveToSharedPreferences(syncedData, this)
                    } else {
                        saveToSharedPreferences(null, this)
                    }
                }
            }
        }
    }
}