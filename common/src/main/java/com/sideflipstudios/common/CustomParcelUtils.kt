package com.sideflipstudios.common

import android.os.Parcel
import android.os.Parcelable

object CustomParcelUtils {

    // Converts a Parcelable object into a ByteArray, suitable for transmission through the Data Layer
    fun toByteArray(parcelable: Parcelable): ByteArray {
        val parcel = Parcel.obtain()
        try {
            parcelable.writeToParcel(parcel, 0)
            return parcel.marshall()
        } finally {
            parcel.recycle()
        }
    }

    // Converts a ByteArray back into a Parcelable object of the specified class type
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

    // Inline version of `fromByteArray` that infers the class type automatically using reified generics
    inline fun <reified T : Parcelable> fromByteArray(byteArray: ByteArray): T {
        return fromByteArray(byteArray, T::class.java)
    }


    // Converts a MutableList of Parcelable objects into a ByteArray
    fun <T : Parcelable> toByteArrayList(list: MutableList<T>): ByteArray {
        val parcel = Parcel.obtain()
        try {
            parcel.writeTypedList(list)
            return parcel.marshall()
        } finally {
            parcel.recycle()
        }
    }

    // Converts a ByteArray back into a MutableList of Parcelable objects of the specified class type
    fun <T : Parcelable> fromByteArrayList(byteArray: ByteArray, clazz: Class<T>): MutableList<T> {
        val parcel = Parcel.obtain()
        try {
            parcel.unmarshall(byteArray, 0, byteArray.size)
            parcel.setDataPosition(0)
            val creator = clazz.getField("CREATOR").get(null) as Parcelable.Creator<T>
            return mutableListOf<T>().apply {
                parcel.readTypedList(this, creator)
            }
        } finally {
            parcel.recycle()
        }
    }

    // Inline version of `fromByteArrayList` that infers the class type automatically using reified generics
    inline fun <reified T : Parcelable> fromByteArrayList(byteArray: ByteArray): MutableList<T> {
        return fromByteArrayList(byteArray, T::class.java)
    }
}