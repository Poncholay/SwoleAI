package com.guillaumewilmot.swoleai.modules.home.dialogchooseexercise

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.guillaumewilmot.swoleai.model.ExerciseBookModel
import com.guillaumewilmot.swoleai.model.ExerciseModel

class ExerciseBookModelPreviewParameterProvider : PreviewParameterProvider<ExerciseBookModel> {
    override val values: Sequence<ExerciseBookModel> = sequenceOf(
        ExerciseBookModel(
            id = 1,
            exercises = listOf(
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
        )
    )
}