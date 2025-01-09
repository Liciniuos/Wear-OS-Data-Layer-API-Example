package com.sideflipstudios.simpledatasend.presentation

import android.content.Context

import androidx.core.content.ContextCompat.getString
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.sideflipstudios.simpledatasend.R


object SharedPrefs {

    var mainActivityActive = false

    fun saveToSharedPreferences(data: MinimalTask?, context: Context) {
        val jsonString = Gson().toJson(data)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences?.edit()
        editor?.putString(getString(context, R.string.txtSavedTaskKey), jsonString)
        editor?.apply()
    }
}