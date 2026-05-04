package com.approagency.drug.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    isLoading: Boolean?,
    onClick: () -> Unit,
    height: Int? = null
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().height(height?.dp ?: 50.dp),
        onClick = {
            onClick()
        },
    ) {
        if (isLoading == true) {
          Text("... در حال جستجو" , textAlign = TextAlign.Right)
        } else {
            Text(text)
        }
    }
}