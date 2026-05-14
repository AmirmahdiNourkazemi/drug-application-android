package com.approagency.drug.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.vada.caller.ui.theme.dime

@Composable
fun CustomTextFilled(
    value: String = "",
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
    placeholder: String = "جستجو...",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    showClearButton: Boolean = true,
    showSearchButton: Boolean = true,
    autoSearch: Boolean = false,
    searchDelay: Long = 500L,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Search
    ),
    keyboardActions: KeyboardActions = KeyboardActions(
        onSearch = {
            if (value.isNotBlank()) {
                onSearch(value)
            }
        }
    ),
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    hintStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    borderColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
    height: Int = 45,
    onFocusChange: ((Boolean) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var localValue by remember(value) { mutableStateOf(value) }

    // Auto-search functionality
    androidx.compose.runtime.LaunchedEffect(autoSearch, searchDelay, localValue) {
        if (autoSearch && localValue.isNotBlank()) {
            kotlinx.coroutines.delay(searchDelay)
            onSearch(localValue)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height.dp)
                .background(
                    color = containerColor,
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = if (isFocused)
                        MaterialTheme.colorScheme.primary
                    else
                        borderColor,
                    shape = shape
                )
                .padding(horizontal = MaterialTheme.dime.md, vertical = MaterialTheme.dime.sm)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocusChange?.invoke(focusState.isFocused)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Clear button (shows when there's text and clear button is enabled)
                if (showClearButton && localValue.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            localValue = ""
                            onValueChange("")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Text field
                BasicTextField(
                    value = localValue,
                    onValueChange = { newValue ->
                        localValue = newValue
                        onValueChange(newValue)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = if (showClearButton && localValue.isNotEmpty()) 4.dp else 0.dp),
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    textStyle = textStyle,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (localValue.isEmpty() && !isFocused) {
                                Text(
                                    text = placeholder,
                                    style = hintStyle
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Search button
                if (showSearchButton) {
                    IconButton(
                        onClick = {
                            if (localValue.isNotBlank()) {
                                onSearch(localValue)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp),
                            tint = if (localValue.isNotBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}