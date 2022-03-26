package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import java.io.Serializable
import java.util.*

data class ProgramWeekModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("blockId")
    val blockId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("date")
    val date: Date,
    @SerializedName("intensity")
    val intensity: Float,
    @SerializedName("volume")
    val volume: Float,
    @SerializedName("sessions")
    var sessions: List<ProgramSessionModel>,
    @SerializedName("isComplete")
    val isComplete: Boolean,
) : Serializable {
    //FIXME: Remove
    fun generateFakeSessions(index: Int) {
        sessions = listOf(
            ProgramSessionModel(
                id = index,
                weekId = id,
                name = "Day 1 - Lower body",
                isComplete = date.before(Date()),
                exercises = listOf(),
            ),
            ProgramSessionModel(
                id = index,
                weekId = id,
                name = "Day 2 - Upper body",
                isComplete = date.plusDays(2).before(Date()),
                exercises = listOf(),
            ),
            ProgramSessionModel(
                id = index,
                weekId = id,
                name = "Day 3 - Lower body",
                isComplete = date.plusDays(3).before(Date()),
                exercises = listOf(),
            ),
            ProgramSessionModel(
                id = index,
                weekId = id,
                name = "Day 4 - Upper body",
                isComplete = date.plusDays(5).before(Date()),
                exercises = listOf(),
            )
        )
    }
}