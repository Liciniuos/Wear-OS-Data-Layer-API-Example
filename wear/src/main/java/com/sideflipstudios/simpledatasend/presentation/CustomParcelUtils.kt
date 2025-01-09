package com.sideflipstudios.simpledatasend.presentation

import android.os.Parcel
import android.os.Parcelable

object CustomParcelUtils {

    fun toByteArray(parcelable: Parcelable): ByteArray {
        val parcel = Parcel.obtain()
        try {
            parcelable.writeToParcel(parcel, 0)
            return parcel.marshall()
        } finally {
            parcel.recycle()
        }
    }

    fun <T : Parcelable> fromByteArray(byteArray: ByteArray, clazz: Class<T>): T {
        val parcel = Parcel.obtain()
        try {
            parcel.unmarshall(byteArray, 0, byteArray.size)
            parcel.setDataPosition(0)
            // Use the CREATOR to create the object from the parcel
            val creator = clazz.getField("CREATOR").get(null) as Parcelable.Creator<T>
            return creator.createFromParcel(parcel)
        } finally {
            parcel.recycle()
        }
    }

    inline fun <reified T : Parcelable> fromByteArray(byteArray: ByteArray): T {
        return fromByteArray(byteArray, T::class.java)
    }
}


