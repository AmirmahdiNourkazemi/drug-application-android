package com.approagency.drug.presentation.common
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip


@Composable

fun CustomBox(
    modifier: Modifier? = Modifier,
    child:  @Composable ()-> Unit
) {
    if (modifier != null) {
        Box(
            modifier = modifier.clip(shape = MaterialTheme.shapes.large).background(
                color =  MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            child()
        }
    }
}