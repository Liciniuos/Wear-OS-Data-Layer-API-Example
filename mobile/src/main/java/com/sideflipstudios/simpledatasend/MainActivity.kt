package com.sideflipstudios.simpledatasend

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.sideflipstudios.simpledatasend.databinding.ActivityMainBinding
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendButton.setOnClickListener {
            sendDataToWear(binding.editText.text.toString())
        }
    }


    private fun sendDataToWear(data: String) {

        val temp = MinimalTask(
            absolute = false,
            name = data,
            dateTime = LocalDateTime.now().minusMinutes(20),
            zone = ZoneId.systemDefault().id
        )


        val byteArray = CustomParcelUtils.toByteArray(temp)

        val dataMap = PutDataMapRequest.create("/sync_data")
        dataMap.dataMap.putByteArray("sync_key", byteArray)
        val request = dataMap.asPutDataRequest()
        Wearable.getDataClient(this).putDataItem(request)
    }
}