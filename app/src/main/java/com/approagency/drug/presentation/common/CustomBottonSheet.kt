package com.approagency.drug.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vada.caller.ui.theme.dime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    scope: CoroutineScope = rememberCoroutineScope(),
    onDismiss: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss?.invoke()
        },
        sheetState = sheetState,
        modifier = modifier,
        shape = RoundedCornerShape(topStart = MaterialTheme.dime.lg, topEnd =  MaterialTheme.dime.lg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            content()
        }
    }
}
