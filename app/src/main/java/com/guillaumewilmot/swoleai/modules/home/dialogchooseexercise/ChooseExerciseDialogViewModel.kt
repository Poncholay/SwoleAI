package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise.ListItemOfExercise
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ChooseExerciseDialogViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application),
    HasLoader by HasLoaderImpl() {

    private val _exerciseBook = dataStorage.dataHolder.exerciseBookField

    val exercises: Flowable<List<ListItemOfExercise.ViewDataModel>> = _exerciseBook.map {
        it.exercises.map { exercise ->
            ListItemOfExercise.ViewDataModel(
                nameText = exercise.name,
                backgroundColor = R.color.transparent,
                backgroundColorAlpha = 0f,
                infoButtonVisible = true,
                swapButtonVisible = false
            )
        }
    }

    /**
     * LOGIC
     */

    fun onExerciseClicked(index: Int) {
        //TODO
    }

    fun onExerciseInfoClicked(index: Int) {
        //TODO
    }
}