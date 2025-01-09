package com.sideflipstudios.simpledatasend

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.sideflipstudios.common.CustomParcelUtils
import com.sideflipstudios.common.ReturnClass

class TaskUpdateService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            //if the data is new and is on the path relevant to the mobile app
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/mobile_update") {

                // Retrieve the byte array from the DataMap
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val byteArray = dataMap.getByteArray("return_class")

                if (byteArray != null) {
                    // Deserialize the byte array into a SendClass object
                    val syncedData: ReturnClass = CustomParcelUtils.fromByteArray(byteArray)

                    // Your apps functionality
                    Log.i(
                        "SendData",
                        "Watch returned: ${syncedData.returnData.name} Action: ${syncedData.action}"
                    )
                }
            }
        }
    }
}