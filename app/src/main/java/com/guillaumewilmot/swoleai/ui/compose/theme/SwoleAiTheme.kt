package com.guillaumewilmot.swoleai.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.guillaumewilmot.swoleai.R

@Composable
fun lightColorPalette() = lightColors(
    primary = colorResource(id = R.color.main),
    secondary = colorResource(id = R.color.secondary),
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun darkColorPalette() = darkColors(
    primary = colorResource(id = R.color.mainLight),
    secondary = colorResource(id = R.color.secondaryLight),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
)

@Composable
fun SwoleAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) darkColorPalette() else lightColorPalette(),
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(16.dp),
        ),
        content = content
    )
}