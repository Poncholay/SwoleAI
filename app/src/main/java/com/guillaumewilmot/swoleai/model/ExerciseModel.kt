package com.guillaumewilmot.swoleai.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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

class ExerciseModelPreviewParameterProvider :
    PreviewParameterProvider<ExerciseModel> {
    override val values: Sequence<ExerciseModel> = sequenceOf(
        ExerciseModel(
            id = 1,
            name = "Comp Squat",
            weightIncrement = 2.5f,
            oneRM = null,
            tenRM = null,
            history = listOf(),
            cues = ""
        ),
        ExerciseModel(
            id = 1,
            name = "Comp Bench",
            weightIncrement = 2.5f,
            oneRM = null,
            tenRM = null,
            history = listOf(),
            cues = ""
        ),
        ExerciseModel(
            id = 1,
            name = "Comp Deadlift",
            weightIncrement = 2.5f,
            oneRM = null,
            tenRM = null,
            history = listOf(),
            cues = ""
        )
    )
}