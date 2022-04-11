package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SessionModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("weekId")
    val weekId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("isComplete")
    val isComplete: Boolean,
    @SerializedName("exercises")
    val exercises: List<SessionExerciseModel>
) : Serializable {
    companion object {
        private const val serialVersionUID = 6L
    }
}