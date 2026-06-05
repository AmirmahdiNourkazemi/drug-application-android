package com.approagency.pharmacy.presentation.common

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loading(
color: Color = MaterialTheme.colorScheme.onPrimary,
modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        color = color,
        modifier = modifier,
        strokeWidth = 2.dp
    )
}