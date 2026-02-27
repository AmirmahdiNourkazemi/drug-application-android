package com.approagency.drug.presentation.common

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarManager {
    private var snackbarHostState: SnackbarHostState? = null
    private var coroutineScope: CoroutineScope? = null

    fun init(scope: CoroutineScope, hostState: SnackbarHostState) {
        coroutineScope = scope
        snackbarHostState = hostState
    }

    fun showMessage(message: String) {
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(message)
        }
    }
}
