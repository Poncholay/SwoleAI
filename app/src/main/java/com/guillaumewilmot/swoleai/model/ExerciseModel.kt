package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ExerciseModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("weightIncrement")
    val weightIncrement: Float,
    @SerializedName("oneRM")
    val oneRM: Float?,
    @SerializedName("tenRM")
    val tenRM: Float?,
    @SerializedName("history")
    val history: List<LiftModel>,
    @SerializedName("cues")
    val cues: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}