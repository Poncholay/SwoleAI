package com.guillaumewilmot.swoleai.ui.compose.components.defaults

import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import com.guillaumewilmot.swoleai.R

@Composable
fun SwoleAiCard(content: @Composable () -> Unit) = Card(
    elevation = dimensionResource(id = R.dimen.elevationCardView),
    content = content
)