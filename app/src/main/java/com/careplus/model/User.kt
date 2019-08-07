package com.careplus.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var id: String? = "",
    var username: String? = "",
    var password: String? = "",
    var name: String? = ""
)