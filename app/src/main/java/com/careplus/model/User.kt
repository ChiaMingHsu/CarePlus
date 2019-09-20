package com.careplus.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var id: String = "",
    var name: String = "",
    var avatarUrl: String? = null
)