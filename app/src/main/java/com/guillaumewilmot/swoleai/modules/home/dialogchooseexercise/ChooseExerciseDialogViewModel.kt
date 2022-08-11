package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ChooseExerciseDialogViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage),
    HasLoader by HasLoaderImpl() {
    val exerciseBook = dataStorage.dataHolder.exerciseBookField
}