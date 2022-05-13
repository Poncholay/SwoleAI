package com.guillaumewilmot.swoleai.modules.home.program

import android.util.Log
import com.google.gson.Gson
import com.guillaumewilmot.swoleai.model.ProgramBlockModel
import com.guillaumewilmot.swoleai.model.ProgramModel
import com.guillaumewilmot.swoleai.model.ProgramWeekModel
import com.guillaumewilmot.swoleai.util.DateHelper.minusDays
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
//TODO : Remove this ASAP, only used for blueprints and development help
object FakeProgram {

    val startDate = Calendar.getInstance().apply {
        time = Date()
        set(Calendar.DAY_OF_WEEK, 2)
    }.time
    var generateSessionindex = 1
    val fakeProgram = ProgramModel(
        id = 1,
        blocks = listOf(
            ProgramBlockModel(
                1,
                ProgramBlockModel.BlockType.HYPERTROPHY,
                listOf(
                    ProgramWeekModel(
                        1,
                        1,
                        "Week 1",
                        startDate.minusDays(2 * 7),
                        0.5f,
                        1.5f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        2,
                        1,
                        "Week 2",
                        startDate.minusDays(1 * 7),
                        2.5f,
                        2f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        3,
                        1,
                        "Week 3",
                        startDate,
                        3f,
                        3f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        4,
                        1,
                        "Week 4",
                        startDate.plusDays(1 * 7),
                        2.5f,
                        4f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        5,
                        1,
                        "Week 5",
                        startDate.plusDays(2 * 7),
                        1.8f,
                        1f,
                        listOf()
                    )
                ).onEach {
                    it.generateFakeSessions()
                }
            ),
            ProgramBlockModel(
                2,
                ProgramBlockModel.BlockType.HYPERTROPHY,
                listOf(
                    ProgramWeekModel(
                        6,
                        2,
                        "Week 6",
                        startDate.plusDays(3 * 7),
                        2f,
                        2f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        7,
                        2,
                        "Week 7",
                        startDate.plusDays(4 * 7),
                        2.8f,
                        3f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        8,
                        2,
                        "Week 8",
                        startDate.plusDays(5 * 7),
                        2.5f,
                        4f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        9,
                        2,
                        "Week 9",
                        startDate.plusDays(6 * 7),
                        1.8f,
                        1f,
                        listOf()
                    )
                ).onEach {
                    it.generateFakeSessions()
                }
            ),
            ProgramBlockModel(
                3,
                ProgramBlockModel.BlockType.STRENGTH,
                listOf(
                    ProgramWeekModel(
                        10,
                        3,
                        "Week 10",
                        startDate.plusDays(7 * 7),
                        4f,
                        3.5f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        11,
                        3,
                        "Week 11",
                        startDate.plusDays(8 * 7),
                        4.5f,
                        2.5f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        12,
                        3,
                        "Week 12",
                        startDate.plusDays(9 * 7),
                        5f,
                        1.5f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        13,
                        3,
                        "Week 13",
                        startDate.plusDays(10 * 7),
                        4f,
                        1f,
                        listOf()
                    )
                ).onEach {
                    it.generateFakeSessions()
                }
            ),
            ProgramBlockModel(
                4,
                ProgramBlockModel.BlockType.PEAKING,
                listOf(
                    ProgramWeekModel(
                        14,
                        4,
                        "Week 14",
                        startDate.plusDays(11 * 7),
                        7.5f,
                        2.5f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        15,
                        4,
                        "Week 15",
                        startDate.plusDays(12 * 7),
                        8f,
                        2f,
                        listOf()
                    ),
                    ProgramWeekModel(
                        16,
                        4,
                        "Week 16",
                        startDate.plusDays(13 * 7 - 1),
                        4f,
                        1.5f,
                        listOf()
                    ),
                ).onEach {
                    it.generateFakeSessions()
                }
            )
        ).also {
            val a = Gson().toJson(it)
            Log.d("Program", a)
        }
    )
}