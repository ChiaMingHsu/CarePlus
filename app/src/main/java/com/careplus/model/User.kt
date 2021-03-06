package com.careplus.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @set:Exclude
    @get:Exclude
    var id: String = "",

    var name: String = "",
    var email: String = "",
    var avatarUrl: String? = null,
    var pushToken: String? = null
)