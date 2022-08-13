package com.guillaumewilmot.swoleai.ui.compose.components.list.exercise

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise.ListItemOfExerciseListViewDataModelPreviewParameterProvider
import com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise.ListItemOfExercise
import com.guillaumewilmot.swoleai.ui.compose.theme.SwoleAiTheme

object ListOfExercise {
    @Composable
    fun View(
        exercises: List<ListItemOfExercise.ViewDataModel>,
        modifier: Modifier = Modifier,
        onClick: (Int) -> Unit = {},
        onClickInfo: (Int) -> Unit = {},
        onClickSwap: (Int) -> Unit = {}
    ) {
        val exerciseListState: LazyListState = rememberLazyListState()

        LazyColumn(
            state = exerciseListState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.marginSmall))
        ) {
            this.itemsIndexed(exercises) { i, exercise ->
                ListItemOfExercise.View(
                    dataModel = exercise,
                    onClick = { onClick(i) },
                    onClickInfo = { onClickInfo(i) },
                    onClickSwap = { onClickSwap(i) },
                )
            }
        }
    }

}

/**
 * PREVIEWS
 */

@Composable
@Preview(name = "ListOfExercisePreviewLight", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "ListOfExercisePreviewDark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ListOfExercisePreview(
    @PreviewParameter(ListItemOfExerciseListViewDataModelPreviewParameterProvider::class)
    exercises: List<ListItemOfExercise.ViewDataModel>
) {
    SwoleAiTheme {
        Surface {
            ListOfExercise.View(exercises = exercises)
        }
    }
}