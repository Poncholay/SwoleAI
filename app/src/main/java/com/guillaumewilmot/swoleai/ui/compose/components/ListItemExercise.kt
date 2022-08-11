package com.guillaumewilmot.swoleai.ui.compose.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
        Row {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(id = R.dimen.marginTiny))
                    .align(Alignment.CenterVertically)
            )
            Column {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(id = R.string.app_adapter_view_exercise_summary_info_icon),
                    tint = MaterialTheme.typography.h5.color,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = stringResource(R.string.app_adapter_view_exercise_summary_info_icon),
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
//    }
}