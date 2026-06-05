package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.approagency.pharmacy.data.dto.DarmanList
import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.presentation.common.CustomBox
import com.vada.caller.ui.theme.dime
import kotlin.collections.emptyList

@Composable
fun DarmanContent(
    darmanData: Result<DarmanModel?>?,
    onDarmanClick: (Int) -> Unit,
    onRetryClick:()-> Unit,
){
    val drugs = darmanData?.getOrNull()?.data ?: emptyList()
    if (drugs.isEmpty()){
        RetryContent(
        modifier = Modifier,
            error = "مشکلی از سمت سرور پیش آمده",
            onClick = {
                onRetryClick()
            }
        )
    } else {
        LazyColumn {
            items(drugs.count() , itemContent ={
                    i ->  DarmanItem(
                darman = darmanData?.getOrNull()!!.data[i] ,
                onClick = { onDarmanClick(drugs[i].cod!!) }
            )
            } )
        }

    }


}

@Composable
fun DarmanItem(
    darman: DarmanList, // Adjust this based on your actual data class
    onClick: (Int?) -> Unit
) {
    CustomBox(
        modifier = Modifier
            .padding(bottom = MaterialTheme.dime.sm)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick(darman.cod)
            },
        child = {
            Row(
                modifier = Modifier.padding(all = MaterialTheme.dime.lg),
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = AbsoluteAlignment.Right
//                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(darman.nam_fa.toString() , style = MaterialTheme.typography.labelLarge , textAlign = TextAlign.Right )
                        Text(darman.nam_en.toString() , style = MaterialTheme.typography.bodySmall)
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {
                                onClick(darman.cod)
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft
                                    , contentDescription = "see more"
                                )
                                Text("مشاهده لیست")
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