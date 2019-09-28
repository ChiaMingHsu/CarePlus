package com.careplus

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

class Util {

    companion object {
        fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
            return Base64.decode(base64Str, Base64.DEFAULT)
                .run { BitmapFactory.decodeByteArray(this, 0, this.size) }
        }
    }

}