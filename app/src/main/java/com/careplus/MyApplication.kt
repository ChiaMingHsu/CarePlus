package com.careplus

import android.app.Application
import com.careplus.model.User

class MyApplication : Application() {
    companion object {
        lateinit var user: User
    }
}