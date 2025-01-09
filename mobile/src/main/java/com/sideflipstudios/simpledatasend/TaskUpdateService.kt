package com.sideflipstudios.simpledatasend

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class TaskUpdateService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.i("AAA","onDataChanged called")

        for (event in dataEvents) {
            Log.i("AAA",event.dataItem.uri.path.toString())

            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/task_update") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val isMark = dataMap.getBoolean("is_mark")

                Log.i("AAA",isMark.toString())

                // Handle the mark or delete action
                if (isMark) {
                    markTask()
                } else {
                    deleteTask()
                }
            }
        }
    }

    private fun markTask() {
        // Handle marking the task
        Log.i("AAA", "Task marked")
    }

    private fun deleteTask() {
        // Handle deleting the task
        Log.i("AAA", "Task deleted")
    }
}