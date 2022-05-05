package com.guillaumewilmot.swoleai.modules.home.program

import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.ProgramBlockModel
import com.guillaumewilmot.swoleai.model.ProgramWeekModel
import com.guillaumewilmot.swoleai.model.SessionModel
import io.reactivex.rxjava3.core.Flowable

interface CanLookupProgram {
    val programBlocks: Flowable<List<ProgramBlockModel>>
    val programWeeks: Flowable<List<ProgramWeekModel>>
    val programSessions: Flowable<List<SessionModel>>

    fun getProgramWeekFromSession(
        currentSessionFlowable: Flowable<Nullable<SessionModel>>
    ): Flowable<Nullable<ProgramWeekModel>>

    fun getProgramBlockFromProgramWeek(
        currentWeekFlowable: Flowable<Nullable<ProgramWeekModel>>
    ): Flowable<Nullable<ProgramBlockModel>>
}