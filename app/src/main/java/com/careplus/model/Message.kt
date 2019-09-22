package com.careplus.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    var type: String = "",
    var priority: Int = 0,
    var date: String = "",
    var time: String = "",
    var content: String = ""
)