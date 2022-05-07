package com.guillaumewilmot.swoleai.modules.home.program

import com.guillaumewilmot.swoleai.model.*
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CanLookupProgramImpl(dataStorage: DataStorage) : CanLookupProgram {

    private val _program = dataStorage.dataHolder.programField

    override val programBlocks: Flowable<List<ProgramBlockModel>> = _program.map {
        it.value?.blocks ?: listOf()
    }

    override val programWeeks: Flowable<List<ProgramWeekModel>> = programBlocks.map {
        it.flatMap { block ->
            block.weeks
        }
    }

    override val programSessions: Flowable<List<SessionModel>> = programWeeks.map {
        it.flatMap { week ->
            week.sessions
        }
    }

    override fun getProgramBlockFromProgramWeek(
        currentWeekFlowable: Flowable<Nullable<ProgramWeekModel>>
    ): Flowable<Nullable<ProgramBlockModel>> = Flowable.combineLatest(
        currentWeekFlowable,
        programBlocks
    ) { currentWeek, programBlocks ->
        programBlocks.find {
            it.id == currentWeek.value?.blockId
        }.asNullable()
    }.distinctUntilChanged()

    override fun getProgramWeekFromSession(
        currentSessionFlowable: Flowable<Nullable<SessionModel>>
    ): Flowable<Nullable<ProgramWeekModel>> = Flowable.combineLatest(
        currentSessionFlowable,
        programWeeks
    ) { currentSession, programWeeks ->
        programWeeks.find {
            it.id == currentSession.value?.weekId
        }.asNullable()
    }.distinctUntilChanged()
}