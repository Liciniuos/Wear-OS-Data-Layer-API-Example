package com.sideflipstudios.simpledatasend

import java.time.LocalDateTime
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.format.DateTimeFormatter

@Parcelize
class MinimalTask(
    var absolute: Boolean,
    var name: String,
    private var dateTimeString: String,
    var zone: String,
) : Parcelable {

    var dateTime: LocalDateTime
        get() = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        set(value) {
            dateTimeString = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

    constructor(absolute: Boolean, name: String, dateTime: LocalDateTime, zone: String) : this(
        absolute,
        name,
        dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), // Convert LocalDateTime to string
        zone
    )
}