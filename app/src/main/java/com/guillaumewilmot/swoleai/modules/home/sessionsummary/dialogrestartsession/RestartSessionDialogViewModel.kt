package com.guillaumewilmot.swoleai.modules.home.sessionsummary.dialogrestartsession

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class RestartSessionDialogViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage),
    HasLoader by HasLoaderImpl() {

    fun reinitSession(status: RestartSessionDialog.Status): Completable = when (status) {
        RestartSessionDialog.Status.COMPLETED,
        RestartSessionDialog.Status.SKIPPED -> selectedSession
        RestartSessionDialog.Status.ACTIVE -> activeSession
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable {
            val sessionToUpdate = it.value ?: return@switchMapCompletable Completable.complete()

            val updatedSession = SessionModel(
                id = sessionToUpdate.id,
                weekId = sessionToUpdate.weekId,
                day = sessionToUpdate.day,
                name = sessionToUpdate.name,
                isComplete = false,
                isSkipped = false,
                isActive = false,
                exercises = sessionToUpdate.exercises
            )

            insertSession(updatedSession)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}