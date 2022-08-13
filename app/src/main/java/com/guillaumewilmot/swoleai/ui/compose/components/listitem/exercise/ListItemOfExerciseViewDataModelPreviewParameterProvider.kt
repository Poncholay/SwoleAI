package com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.guillaumewilmot.swoleai.R

class ListItemOfExerciseViewDataModelPreviewParameterProvider :
    PreviewParameterProvider<ListItemOfExercise.ViewDataModel> {
    override val values: Sequence<ListItemOfExercise.ViewDataModel> = sequenceOf(
        ListItemOfExercise.ViewDataModel(
            nameText = "New rep max attempt\nCompetition Squat",
            backgroundColor = R.color.hypertrophy,
            backgroundColorAlpha = 0.5f,
            infoButtonVisible = true,
            swapButtonVisible = true,
        ),
        ListItemOfExercise.ViewDataModel(
            nameText = "Extra wide 5:3:1 tempo Bench",
            backgroundColor = R.color.transparent,
            backgroundColorAlpha = 0f,
            infoButtonVisible = false,
            swapButtonVisible = true,
        ),
        ListItemOfExercise.ViewDataModel(
            nameText = "Sumo deadlift",
            backgroundColor = R.color.transparent,
            backgroundColorAlpha = 0f,
            infoButtonVisible = true,
            swapButtonVisible = false,
        )
    )
}

class ListItemOfExerciseListViewDataModelPreviewParameterProvider :
    PreviewParameterProvider<List<ListItemOfExercise.ViewDataModel>> {
    override val values: Sequence<List<ListItemOfExercise.ViewDataModel>> = sequenceOf(
        listOf(
            ListItemOfExercise.ViewDataModel(
                nameText = "New rep max attempt\nCompetition Squat",
                backgroundColor = R.color.hypertrophy,
                backgroundColorAlpha = 0.5f,
                infoButtonVisible = true,
                swapButtonVisible = true,
            ),
            ListItemOfExercise.ViewDataModel(
                nameText = "Extra wide 5:3:1 tempo Bench",
                backgroundColor = R.color.transparent,
                backgroundColorAlpha = 0f,
                infoButtonVisible = false,
                swapButtonVisible = true,
            )
        )
    )
}