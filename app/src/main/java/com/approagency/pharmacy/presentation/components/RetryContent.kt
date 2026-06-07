package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.approagency.pharmacy.presentation.common.CustomBox
import com.approagency.pharmacy.presentation.common.PrimaryButton
import com.vada.caller.ui.theme.dime

@Composable
fun RetryContent(modifier: Modifier = Modifier , error:String ,onClick:()-> Unit ) {
    CustomBox(
        child = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(MaterialTheme.dime.md)
            ) {
                Text(
                    text = "خطا در دریافت اطلاعات",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = MaterialTheme.dime.sm)
                )
                PrimaryButton(
                    text = "تلاش مجدد",
                    height = 40,
                    isLoading = false,
                    onClick = { onClick() }
                )
            }
        }
    )
}