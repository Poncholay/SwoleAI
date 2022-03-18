package com.guillaumewilmot.swoleai

import android.app.Application
import com.guillaumewilmot.swoleai.util.storage.rxlive.RxLiveStorage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SwoleAiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        RxLiveStorage.registerUpdateListener(this)
    }
}