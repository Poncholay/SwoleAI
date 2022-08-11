package com.guillaumewilmot.swoleai.ui.compose.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.model.ExerciseModel
import com.guillaumewilmot.swoleai.model.ExerciseModelPreviewParameterProvider
import com.guillaumewilmot.swoleai.ui.compose.components.defaults.SwoleAiCard

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun ListItemExercise(
    @PreviewParameter(ExerciseModelPreviewParameterProvider::class)
    exercise: ExerciseModel
) {
//    SwoleAiTheme {
    SwoleAiCard {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.marginTiny))
        )
    }
//    }
}