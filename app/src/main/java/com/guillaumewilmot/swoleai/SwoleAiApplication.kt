package com.guillaumewilmot.swoleai

import android.app.Application
import android.preference.PreferenceManager
import com.guillaumewilmot.swoleai.util.storage.live.LiveStorage

class SwoleAiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LiveStorage.registerUpdateListener(this)
    }
}