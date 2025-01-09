package com.sideflipstudios.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// The class the mobile sends to the watch
@Parcelize
class SendClass(
    val id: Int,
    var name: String,
    private var dateTimeString: String // A dateTime object as a formatted string, used internally for parcelization
) : Parcelable {

    // This is necessary in order to Parcelize complex data, such as datetime

    var dateTime: LocalDateTime
        // Converts the stored dateTimeString into a LocalDateTime object when accessed
        get() = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        set(value) {
            // Converts and stores the given LocalDateTime object into the string format when modified
            dateTimeString = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

    // Secondary constructor to allow initializing the class with a LocalDateTime object directly
    constructor(
        id: Int, name: String, dateTime: LocalDateTime
    ) : this(
        id, name, dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    // Override the '==' equals method
    // This ensures objects sent that are sent to the watch and then sent back return true when compared to the original object
    override fun equals(other: Any?): Boolean {
        // If the two objects reference the same instance, they are equal
        if (this === other) return true

        // If the other object is not an instance of SendClass, they are not equal
        // It can be useful to remove this if you send a reduced version of your main class to the watch
        // but still want to compare to the full version if it is sent back
        if (other !is SendClass) return false

        // Compare the id, name, and dateTime properties for equality
        return id == other.id && name == other.name && dateTime == other.dateTime
    }

    // Override the hashCode method to generate a hash code for SendClass instances
    // This is also useful to ensure '==' works as expected after parcelizing and de-parcelizing an object
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}


