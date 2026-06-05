package com.approagency.pharmacy.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.vada.caller.ui.theme.LocalDime

@Composable
fun EmptySearchState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "جستجوی داروها",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
        Text(
            text = "نام دارو را وارد کنید",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = dime.sm)
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "خطا",
            color = MaterialTheme.colorScheme.error,
            fontSize = 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = dime.sm, bottom = dime.lg),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("تلاش مجدد")
        }
    }
}