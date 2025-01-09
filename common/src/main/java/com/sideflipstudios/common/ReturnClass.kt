package com.sideflipstudios.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// The class the mobile receives from the watch
@Parcelize
class ReturnClass(
    val returnData: SendClass,
    var action: Boolean // Can be changed to an Enum if more than two actions are required
) : Parcelable