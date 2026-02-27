package com.approagency.drug.presentation.common

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loading() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier,
        strokeWidth = 1.dp
    )
}