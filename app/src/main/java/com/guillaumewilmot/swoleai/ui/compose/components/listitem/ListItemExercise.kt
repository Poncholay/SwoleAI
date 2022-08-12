package com.guillaumewilmot.swoleai.ui.compose.components.listitem

import android.content.res.Configuration
import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.ui.compose.components.domain.iconwithlegend.IconWithLegend
import com.guillaumewilmot.swoleai.ui.compose.theme.SwoleAiTheme

data class ListItemExerciseViewDataModel(
    val nameText: String,
    @ColorRes
    val backgroundColor: Int,
    val backgroundColorAlpha: Float,
    val infoButtonVisible: Boolean,
    val swapButtonVisible: Boolean
)

@Composable
fun ListItemExercise(
    dataModel: ListItemExerciseViewDataModel,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = dimensionResource(id = R.dimen.elevationCardView),
        backgroundColor = colorResource(
            id = dataModel.backgroundColor
        ).copy(
            alpha = dataModel.backgroundColorAlpha
        ),
        modifier = modifier
    ) {
        Row {
            Text(
                text = dataModel.nameText,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(id = R.dimen.marginTiny))
                    .align(Alignment.CenterVertically)
            )
            if (dataModel.infoButtonVisible) {
                IconWithLegend(
                    icon = Icons.Default.Info,
                    legendStringId = R.string.app_adapter_view_exercise_summary_info_icon
                )
            }
            if (dataModel.swapButtonVisible) {
                IconWithLegend(
                    icon = Icons.Default.SwapHoriz,
                    legendStringId = R.string.app_adapter_view_exercise_summary_swap_icon
                )
            }
        }
    }
}

/**
 * Previews
 */

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun ListItemExercisePreview(
    @PreviewParameter(ListItemExerciseViewDataModelPreviewParameterProvider::class)
    exercise: ListItemExerciseViewDataModel
) {
    SwoleAiTheme {
        Surface {
            ListItemExercise(dataModel = exercise)
        }
    }
}