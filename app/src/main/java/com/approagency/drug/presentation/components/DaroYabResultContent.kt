package com.approagency.drug.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugSearchResult
import com.vada.caller.ui.theme.LocalDime

@Composable
fun DaroYabSearchResult(
    drug: DrugSearchResult,
    onClick: (DrugSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dime.xs)
            .clickable { onClick(drug) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(dime.lg)
        ) {
            Text(
                text = drug.persianName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            drug.englishName?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "کد: ${drug.genericId}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}