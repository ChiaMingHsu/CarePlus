package com.careplus.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    var id: String = "",
    var createdAt: Long = 0,
    var type: String = "",
    var priority: String = "",
    var content: String = ""
)