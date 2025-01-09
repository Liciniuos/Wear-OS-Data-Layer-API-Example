package com.sideflipstudios.simpledatasend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.sideflipstudios.common.CustomParcelUtils
import com.sideflipstudios.common.SendClass
import com.sideflipstudios.simpledatasend.databinding.ActivityMainBinding
import java.time.LocalDateTime

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

        //Create example of SendClass
        val temp = SendClass(
            id = 0,
            name = data,
            dateTime = LocalDateTime.now()
        )

        // Use CustomParcelUtils to create a ByteArray suitable for the data layer
        val byteArray = CustomParcelUtils.toByteArray(temp)

        // Create a DataMapRequest with a specified path ("/sync_data")
        // '/sync_data' can be changed as long as it is consistent  in all usages
        val dataMap = PutDataMapRequest.create("/sync_data")

        // Add the serialized data (ByteArray) to the DataMap with a key ("sync_key")
        //Again the key can be changed as long as it is consistent
        dataMap.dataMap.putByteArray("sync_key", byteArray)

        // Convert the DataMap into a PutDataRequest and send to the watch
        val request = dataMap.asPutDataRequest()
        Wearable.getDataClient(this).putDataItem(request)
    }
}