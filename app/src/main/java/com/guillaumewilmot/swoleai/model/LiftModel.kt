package com.guillaumewilmot.swoleai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class LiftModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("date")
    val date: Date,
    @SerializedName("reps")
    val reps: Int,
    @SerializedName("weight")
    val weight: Float,
    @SerializedName("rpe")
    val rpe: Float
) : Serializable {
    companion object {
        private const val serialVersionUID = 8L
    }
}