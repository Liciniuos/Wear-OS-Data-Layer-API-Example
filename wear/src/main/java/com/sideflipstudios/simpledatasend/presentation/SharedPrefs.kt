package com.sideflipstudios.simpledatasend.presentation

import android.content.Context

import androidx.core.content.ContextCompat.getString
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sideflipstudios.common.SendClass
import com.sideflipstudios.simpledatasend.R


object SharedPrefs {

    // Flag to indicate if the main activity is currently active
    var mainActivityActive = false

    // Saves the given SendClass object as a JSON string in shared preferences
    fun saveToSharedPreferences(context: Context, data: SendClass?) {
        // Convert the SendClass object (or null) into a JSON string
        val jsonString = Gson().toJson(data)
        // Get the default shared preferences for the given context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Edit the shared preferences to store the JSON string
        val editor = sharedPreferences?.edit()
        // Store the JSON string under a specific key (retrieved from resources)
        editor?.putString(getString(context, R.string.txtSavedTaskKey), jsonString)
        // Apply the changes asynchronously
        editor?.apply()
    }

    // Retrieves the saved SendClass object from shared preferences
    fun getFromSharedPreferences(context: Context): SendClass? {
        // Retrieve the JSON string stored in shared preferences under the specific key (retrieved from resources)
        // The key is saved in resources to ensure consistency between uses
        val jsonString = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(context.getString(R.string.txtSavedTaskKey), null)

        // Deserialize the JSON string into a SendClass object and return it
        return Gson().fromJson(
            jsonString, // The JSON string to deserialize
            object :
                TypeToken<SendClass>() {}.type // TypeToken to handle deserialization of SendClass
        )
    }
}