package com.app.tastybuds

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TastyBudsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}