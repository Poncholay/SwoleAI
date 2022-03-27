package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SessionExerciseModel(
    @SerializedName("id")
    val id: Int
) : Serializable {
    companion object {
        private const val serialVersionUID = 5L
    }
}