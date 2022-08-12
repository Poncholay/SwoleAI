package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.ListItemExerciseViewDataModel
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
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage),
    HasLoader by HasLoaderImpl() {

    private val _exerciseBook = dataStorage.dataHolder.exerciseBookField
    private val _currentWeek = getProgramWeekFromSession(selectedSession)
    private val _currentBlock = getProgramBlockFromProgramWeek(_currentWeek)

    //TODO : Get the real exercises from the book
    val exercises: Flowable<List<ListItemExerciseViewDataModel>> =
        Flowable.combineLatest(
            _exerciseBook,
            _currentBlock
        ) { exerciseBook, currentBlock ->
            listOf(
                ListItemExerciseViewDataModel(
                    nameText = "New rep max attempt\nCompetition deadlift",
                    backgroundColor = currentBlock.value?.type?.colorId ?: R.color.hypertrophy,
                    backgroundColorAlpha = 0.5f,
                    infoButtonVisible = true,
                    swapButtonVisible = true
                ),
                ListItemExerciseViewDataModel(
                    nameText = "Pause front squat",
                    backgroundColor = R.color.transparent,
                    backgroundColorAlpha = 1f,
                    infoButtonVisible = true,
                    swapButtonVisible = true
                ),
                ListItemExerciseViewDataModel(
                    nameText = "Pendulum squat",
                    backgroundColor = R.color.transparent,
                    backgroundColorAlpha = 1f,
                    infoButtonVisible = true,
                    swapButtonVisible = true
                )
            )
        }
}