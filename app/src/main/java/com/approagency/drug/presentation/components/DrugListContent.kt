package com.approagency.drug.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.approagency.drug.data.dto.DrugDetail
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.presentation.common.CustomBox
import com.vada.caller.ui.theme.dime

@Composable
fun DrugListContent(
    drugsData: Result<DrugListResponse?>?,
    onDrugClick: (Int) -> Unit
) {
    val drugs = drugsData?.getOrNull()?.data ?: emptyList()

    if (drugs.isEmpty()) {
        CustomBox(
            child = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = MaterialTheme.dime.sm),
                ) {
                    Text(
                        text = "نتیجه‌ای یافت نشد",
                        fontWeight = FontWeight.W500
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.dime.xs))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
    } else {
        LazyColumn(

        ){
            items(drugs.count() , itemContent ={
                    i ->  DrugItem(
                drug = drugs[i],
                onClick = { onDrugClick(drugs[i].cod!!) }
            )
            } )
        }

    }
}


@Composable
fun DrugItem(
    drug: DrugDetail, // Adjust this based on your actual data class
    onClick: (Int?) -> Unit
) {
    CustomBox(
        modifier = Modifier
            .padding(bottom = MaterialTheme.dime.sm)
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick(drug.cod)
            },
        child = {
            Row(
                modifier = Modifier.padding(all = MaterialTheme.dime.lg),
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(drug.nam_en.toString() , style = MaterialTheme.typography.bodySmall)
                        Text(drug.nam_fa.toString() , style = MaterialTheme.typography.labelLarge,textAlign = TextAlign.Right)
                    }
                    if (drug.goroh_darmani != null )


                        Row(
                            modifier = Modifier.fillMaxWidth()
                            , horizontalArrangement = Arrangement.End
                        ) {Text(
                            "گروه دارویی: ${drug.goroh_darmani.nam_fa.toString()}",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Right
                        ) }



                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {
                                onClick(drug.cod)
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft
                                    , contentDescription = "see more"
                                )
                                Text("مشاهده جزییات")
                            }
                        }
                    }
                }

            }
        }
    )
//    Button(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//    ) {
//        drug.nam_fa?.let { Text(text = it) } // Adjust based on your drug model
//    }
}