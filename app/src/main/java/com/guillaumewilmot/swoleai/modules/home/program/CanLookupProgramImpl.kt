package com.guillaumewilmot.swoleai.modules.home.program

import com.guillaumewilmot.swoleai.model.*
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CanLookupProgramImpl : CanLookupProgram {

    //FIXME : TMP hardcoded data, shold be in DataStorage
    override val programBlocksData: Flowable<List<ProgramBlockModel>> = Flowable.create({
        it.onNext(FakeProgram.fakeProgram)
    }, BackpressureStrategy.LATEST)

    override val programWeeks: Flowable<List<ProgramWeekModel>> = programBlocksData.map {
        it.flatMap { block ->
            block.weeks
        }
    }

    override fun getProgramBlockFromProgramWeek(
        currentWeekFlowable: Flowable<Nullable<ProgramWeekModel>>
    ): Flowable<Nullable<ProgramBlockModel>> = Flowable.combineLatest(
        currentWeekFlowable,
        programBlocksData
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