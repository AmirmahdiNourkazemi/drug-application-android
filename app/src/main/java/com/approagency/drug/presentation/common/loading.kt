package com.approagency.drug.presentation.common

import android.content.res.Resources.Theme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vada.caller.ui.theme.dime

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