package com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import com.guillaumewilmot.swoleai.R

class ListItemOfExerciseViewDataModelPreviewParameterProvider :
    CollectionPreviewParameterProvider<ListItemOfExercise.ViewDataModel>(ITEMS) {

    companion object {
        val ITEMS = listOf(
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
    }
}

class ListItemOfExerciseListViewDataModelPreviewParameterProvider :
    PreviewParameterProvider<List<ListItemOfExercise.ViewDataModel>> {
    override val values: Sequence<List<ListItemOfExercise.ViewDataModel>> = sequenceOf(
        ListItemOfExerciseViewDataModelPreviewParameterProvider.ITEMS
    )
}
