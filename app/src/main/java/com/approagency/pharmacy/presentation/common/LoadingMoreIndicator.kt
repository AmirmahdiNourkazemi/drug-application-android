package com.approagency.pharmacy.presentation.common

import androidx.compose.ui.unit.sp


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vada.caller.ui.theme.LocalDime

@Composable
fun LoadingMoreIndicator(
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(dime.lg),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EndOfListIndicator(
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(dime.xl),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "پایان نتایج",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontSize = 12.sp
        )
    }
}