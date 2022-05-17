package com.guillaumewilmot.swoleai.modules.home.program

import com.guillaumewilmot.swoleai.model.*
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.function.UnaryOperator

@ExperimentalCoroutinesApi
class CanInteractWithProgramImpl(private val dataStorage: DataStorage) : CanInteractWithProgram {

    private val _selectedSessionId = dataStorage.dataHolder.selectedSessionIdField
    private val _program = dataStorage.dataHolder.programField

    override val programBlocks: Flowable<List<ProgramBlockModel>> = _program.map {
        it.value?.blocks ?: listOf()
    }

    override val programWeeks: Flowable<List<ProgramWeekModel>> = _program.map {
        it.value?.weeks ?: listOf()
    }

    override val programSessions: Flowable<List<SessionModel>> = _program.map {
        it.value?.sessions ?: listOf()
    }

    override val selectedSession: Flowable<Nullable<SessionModel>> = Flowable.combineLatest(
        programSessions,
        _selectedSessionId
    ) { sessions, selectedSessionId ->
        val id = selectedSessionId.value
        sessions.find { id != null && it.id == id }.asNullable()
    }.distinctUntilChanged()

    override val activeSession: Flowable<Nullable<SessionModel>> = programSessions.map { sessions ->
        sessions.find {
            it.status == SessionModel.Status.ACTIVE
        }.asNullable()
    }

    override fun getProgramBlockFromProgramWeek(
        weekFlowable: Flowable<Nullable<ProgramWeekModel>>
    ): Flowable<Nullable<ProgramBlockModel>> = Flowable.combineLatest(
        weekFlowable,
        programBlocks
    ) { currentWeek, programBlocks ->
        getProgramBlockFromProgramWeek(currentWeek.value, programBlocks).asNullable()
    }.distinctUntilChanged()

    override fun getProgramWeekFromSession(
        sessionFlowable: Flowable<Nullable<SessionModel>>
    ): Flowable<Nullable<ProgramWeekModel>> = Flowable.combineLatest(
        sessionFlowable,
        programWeeks
    ) { session, programWeeks ->
        getProgramWeekFromSession(session.value, programWeeks).asNullable()
    }.distinctUntilChanged()

    /**
     * Insert session
     */

    private fun blockReplacer(
        newSession: SessionModel,
        newSessionBlock: ProgramBlockModel?,
        newSessionWeek: ProgramWeekModel?
    ): UnaryOperator<ProgramBlockModel> = UnaryOperator { block ->
        if (block.id == newSessionBlock?.id) {
            ProgramBlockModel(
                id = block.id,
                type = block.type,
                weeks = block.weeks.toMutableList().apply {
                    replaceAll(weekReplacer(newSession, newSessionWeek))
                }
            )
        } else {
            block
        }
    }

    private fun weekReplacer(
        newSession: SessionModel,
        newSessionWeek: ProgramWeekModel?
    ): UnaryOperator<ProgramWeekModel> = UnaryOperator { week ->
        if (week.id == newSessionWeek?.id) {
            ProgramWeekModel(
                id = week.id,
                blockId = week.blockId,
                name = week.name,
                date = week.date,
                intensity = week.intensity,
                volume = week.volume,
                sessions = week.sessions.toMutableList().apply {
                    replaceAll(sessionReplacer(newSession))
                }
            )
        } else {
            week
        }
    }

    private fun sessionReplacer(
        newSession: SessionModel,
    ): UnaryOperator<SessionModel> = UnaryOperator { session ->
        if (session.id == newSession.id) {
            newSession
        } else {
            session
        }
    }

    override fun insertSession(newSession: SessionModel): Completable = _program
        .take(1)
        .switchMapCompletable {
            val program = it.value
            if (program != null) {
                val newSessionWeek = getProgramWeekFromSession(newSession, program.weeks)
                val newSessionBlock = getProgramBlockFromProgramWeek(newSessionWeek, program.blocks)

                val newBlocks = program.blocks.toMutableList().apply {
                    replaceAll(blockReplacer(newSession, newSessionBlock, newSessionWeek))
                }

                val updatedProgram = ProgramModel(
                    id = program.id,
                    blocks = newBlocks
                )

                dataStorage.toStorage(DataDefinition.PROGRAM, updatedProgram)
            } else {
                Completable.complete()
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Helpers
     */

    private fun getProgramBlockFromProgramWeek(
        week: ProgramWeekModel?,
        programBlocks: List<ProgramBlockModel>?
    ): ProgramBlockModel? = programBlocks?.find {
        it.id == week?.blockId
    }

    private fun getProgramWeekFromSession(
        session: SessionModel?,
        programWeeks: List<ProgramWeekModel>?
    ): ProgramWeekModel? = programWeeks?.find {
        it.id == session?.weekId
    }
}