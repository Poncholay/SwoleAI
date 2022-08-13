package com.guillaumewilmot.swoleai.ui.compose.components.domain.iconwithlegend

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.ui.compose.theme.SwoleAiTheme

@Composable
fun IconWithLegend(
    icon: ImageVector,
    legendStringId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(dimensionResource(id = R.dimen.marginTiny))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = legendStringId),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = legendStringId),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Previews
 */

@Composable
@Preview(name = "IconWithLegendPreviewLight", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "IconWithLegendPreviewDark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun IconWithLegendPreview() {
    SwoleAiTheme {
        Surface {
            IconWithLegend(
                icon = Icons.Default.Info,
                legendStringId = R.string.app_adapter_view_exercise_summary_info_icon
            )
        }
    }
}
