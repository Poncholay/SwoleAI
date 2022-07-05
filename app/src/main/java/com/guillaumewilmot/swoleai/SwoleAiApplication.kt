package com.guillaumewilmot.swoleai

import android.app.Application
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidApp
class SwoleAiApplication : Application() {

    @Inject
    lateinit var dataStorage: DataStorage

    override fun onCreate() {
        super.onCreate()
        dataStorage.toStorage(DataDefinition.SELECTED_SESSION_ID, null)

        //FIXME : TMP the program is reinitialized with hardcoded data each time we launch the app
//        dataStorage.toStorage(DataDefinition.PROGRAM, FakeProgram.fakeProgram)
    }
}