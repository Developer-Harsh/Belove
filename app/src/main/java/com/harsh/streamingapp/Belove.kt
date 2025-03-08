package com.harsh.streamingapp

import android.app.Application
import com.sneva.easyprefs.EasyPrefs

class Belove : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyPrefs.initialize(this)
    }
}