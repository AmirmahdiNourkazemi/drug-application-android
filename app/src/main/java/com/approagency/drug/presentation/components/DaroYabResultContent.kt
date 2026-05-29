package com.approagency.drug.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.approagency.drug.domain.model.DaroYabSearchResult
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.presentation.common.CustomBox
import com.vada.caller.ui.theme.LocalDime
import com.vada.caller.ui.theme.dime

@Composable
fun DaroYabSearchResult(
    drug: DrugSearchResult,
    onClickDetail: (DrugSearchResult) -> Unit,
    onClickDrugStore:() ->Unit,
    modifier: Modifier = Modifier
) {
    val dime = LocalDime.current

    CustomBox (
        modifier = modifier
            .padding(bottom = MaterialTheme.dime.sm)
            .fillMaxWidth()
            .clickable { onClickDetail(drug) },

    ) {
        Row (
            modifier = modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.padding(dime.lg),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = drug.persianName,
                    style = MaterialTheme.typography.labelLarge
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
               Row(
                   modifier = modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween
               ) {
                   TextButton(onClick = {onClickDetail(drug)} , content = {
                       Text("اطلاعات دارویی")
                   })
                   TextButton(onClick = {onClickDetail(drug)} , content = {
                       Text("موجودی داروخانه")
                   })
               }
            }
        }
    }
}