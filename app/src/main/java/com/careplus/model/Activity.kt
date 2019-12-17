package com.careplus.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Activity(
    @set:Exclude
    @get:Exclude
    var id: String = "",

    var date: String = "",
    var start_time: String = "",
    var end_time: String = "",
    var region: String = ""
)