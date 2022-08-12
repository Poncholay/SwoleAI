package com.guillaumewilmot.swoleai.ui.compose.components.listitem

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.guillaumewilmot.swoleai.R

class ListItemExerciseViewDataModelPreviewParameterProvider :
    PreviewParameterProvider<ListItemExerciseViewDataModel> {
    override val values: Sequence<ListItemExerciseViewDataModel> = sequenceOf(
        ListItemExerciseViewDataModel(
            nameText = "New rep max attempt\nCompetition Squat",
            backgroundColor = R.color.hypertrophy,
            backgroundColorAlpha = 0.5f,
            infoButtonVisible = true,
            swapButtonVisible = true,
        ),
        ListItemExerciseViewDataModel(
            nameText = "Extra wide 5:3:1 tempo Bench",
            backgroundColor = R.color.hypertrophy,
            backgroundColorAlpha = 1f,
            infoButtonVisible = false,
            swapButtonVisible = true,
        ),
        ListItemExerciseViewDataModel(
            nameText = "Sumo deadlift",
            backgroundColor = R.color.hypertrophy,
            backgroundColorAlpha = 1f,
            infoButtonVisible = true,
            swapButtonVisible = false,
        ),
        ListItemExerciseViewDataModel(
            nameText = "Overhead press",
            backgroundColor = R.color.hypertrophy,
            backgroundColorAlpha = 1f,
            infoButtonVisible = false,
            swapButtonVisible = false,
        )
    )
}

class ListItemExerciseListViewDataModelPreviewParameterProvider :
    PreviewParameterProvider<List<ListItemExerciseViewDataModel>> {
    override val values: Sequence<List<ListItemExerciseViewDataModel>> = sequenceOf(
        listOf(
            ListItemExerciseViewDataModel(
                nameText = "New rep max attempt\nCompetition Squat",
                backgroundColor = R.color.hypertrophy,
                backgroundColorAlpha = 0.5f,
                infoButtonVisible = true,
                swapButtonVisible = true,
            ),
            ListItemExerciseViewDataModel(
                nameText = "Extra wide 5:3:1 tempo Bench",
                backgroundColor = R.color.hypertrophy,
                backgroundColorAlpha = 1f,
                infoButtonVisible = false,
                swapButtonVisible = true,
            )
        )
    )
}