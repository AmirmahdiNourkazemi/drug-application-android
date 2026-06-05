package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.approagency.pharmacy.domain.model.TestGroup
import com.approagency.pharmacy.presentation.common.CustomBox
import com.vada.caller.ui.theme.dime


@Composable
fun TestGroupItemContent(
    group: TestGroup,
    onClick: () -> Unit
) {

    CustomBox(
        modifier = Modifier
            .padding(bottom = MaterialTheme.dime.sm)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
    ) {
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
                    Text(group.fname ?: "" , style = MaterialTheme.typography.labelLarge , textAlign = TextAlign.Right )
                    Text(group.ename ?: ""  , style = MaterialTheme.typography.bodySmall ,  textAlign = TextAlign.Left)
                    Spacer(modifier = Modifier.padding(vertical = MaterialTheme.dime.xs))
                    Text(group.detail ?: "" , style = MaterialTheme.typography.bodySmall.copy(
                        textDirection = TextDirection.Rtl
                    ) , textAlign = TextAlign.Right , maxLines = 2 , softWrap = true , overflow = TextOverflow.Ellipsis)
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            onClick()
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

}