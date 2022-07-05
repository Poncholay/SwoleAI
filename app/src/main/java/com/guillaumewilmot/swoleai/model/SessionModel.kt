package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SessionModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("weekId")
    val weekId: Int,
    @SerializedName("day")
    val day: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("isComplete")
    val isComplete: Boolean,
    @SerializedName("isSkipped")
    val isSkipped: Boolean,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("exercises")
    val exercises: List<SessionExerciseModel>
) : Serializable {

    val fullName: String
        get() = "$day - $name"

    val status: Status
        get() = when {
            isComplete -> Status.COMPLETE
            isSkipped -> Status.SKIPPED
            isActive -> Status.ACTIVE
            else -> Status.PENDING
        }

    enum class Status {
        COMPLETE,
        SKIPPED,
        ACTIVE,
        PENDING
    }

    companion object {
        private const val serialVersionUID = 6L
    }
}