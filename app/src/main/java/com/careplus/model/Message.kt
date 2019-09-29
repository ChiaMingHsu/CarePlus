package com.careplus.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    @set:Exclude
    @get:Exclude
    var id: String = "",

    var createdAt: Long = 0,
    var type: String = "",
    var priority: String = "",
    var content: String = ""
)