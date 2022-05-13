package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProgramModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("blocks")
    val blocks: List<ProgramBlockModel>,
) : Serializable {

    val weeks = blocks.flatMap {
        it.weeks
    }
    val sessions = weeks.flatMap { week ->
        week.sessions
    }

    companion object {
        private const val serialVersionUID = 3L
    }
}