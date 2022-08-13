package com.guillaumewilmot.swoleai.ui.compose.components.listitem.exercise

import android.content.res.Configuration
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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

object ListItemOfExercise {

    data class ViewDataModel(
        val nameText: String,
        @ColorRes
        val backgroundColor: Int,
        val backgroundColorAlpha: Float,
        val infoButtonVisible: Boolean,
        val swapButtonVisible: Boolean
    )

    @Composable
    fun View(
        dataModel: ViewDataModel,
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
        onClickInfo: () -> Unit = {},
        onClickSwap: () -> Unit = {},
    ) {
        Card(
            elevation = dimensionResource(id = R.dimen.elevationCardView),
        ) {
            Box(
                modifier = modifier
                    .clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier.background(
                        colorResource(
                            id = dataModel.backgroundColor
                        ).copy(
                            alpha = dataModel.backgroundColorAlpha
                        )
                    )
                ) {
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
                            legendStringId = R.string.app_adapter_view_exercise_summary_info_icon,
                            onClick = onClickInfo
                        )
                    }
                    if (dataModel.swapButtonVisible) {
                        IconWithLegend(
                            icon = Icons.Default.SwapHoriz,
                            legendStringId = R.string.app_adapter_view_exercise_summary_swap_icon,
                            onClick = onClickSwap
                        )
                    }
                }
            }
        }
    }
}

/**
 * Previews
 */

@Composable
@Preview(name = "ListItemExercisePreviewLight", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "ListItemExercisePreviewDark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ListItemOfExercisePreview(
    @PreviewParameter(ListItemOfExerciseViewDataModelPreviewParameterProvider::class)
    exercise: ListItemOfExercise.ViewDataModel
) {
    SwoleAiTheme {
        Surface {
            ListItemOfExercise.View(dataModel = exercise)
        }
    }
}