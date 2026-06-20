package com.example.homeworkone

import android.app.Application
import com.example.homeworkone.utilities.ImageLoader
import com.example.homeworkone.utilities.SharedPreferencesManagerV3
import com.example.homeworkone.utilities.SignalManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManagerV3.init(this)
        SignalManager.init(this)
        ImageLoader.init(this)
    }
}