package com.careplus.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
    @set:Exclude
    @get:Exclude
    var id: String = "",

    var name: String = "",
    var type: String = "",
    var icon: String = "",
    var mode: String = "",
    var value: String = "",
    var enabled: Boolean = true
)