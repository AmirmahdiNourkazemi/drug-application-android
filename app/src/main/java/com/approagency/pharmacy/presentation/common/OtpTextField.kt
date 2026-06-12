package com.approagency.pharmacy.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * فیلد ورود کدِ یک‌بارمصرف به‌صورت خانه‌های مجزا با انیمیشن خانه‌ی فعال.
 * همیشه LTR است و رنگ‌ها از تم گرفته می‌شوند تا در حالت تاریک/روشن هماهنگ باشد.
 *
 * @param onOtpTextChange (متن، آیا کامل شد) — با هر تغییر صدا زده می‌شود.
 * @param onComplete وقتی همه‌ی خانه‌ها پر شد یک‌بار صدا زده می‌شود.
 */
@Composable
fun OtpTextField(
    otpText: String,
    onOtpTextChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    otpCount: Int = 5,
    size: Dp = 48.dp,
    focusedSize: Dp = 56.dp,
    onComplete: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isCompleted by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        BasicTextField(
            modifier = modifier.focusRequester(focusRequester),
            value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
            onValueChange = { newValue ->
                if (newValue.text.length <= otpCount) {
                    onOtpTextChange(newValue.text, newValue.text.length == otpCount)
                    if (newValue.text.length == otpCount && !isCompleted) {
                        isCompleted = true
                        focusManager.clearFocus()
                        onComplete()
                        isCompleted = false
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = if (otpText.length == otpCount) ImeAction.Done else ImeAction.Next
            ),
            decorationBox = {
                Row(
                    modifier = Modifier.animateContentSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(otpCount) { index ->
                        val isFocused = otpText.length == index
                        val animatedSize by animateDpAsState(
                            targetValue = if (isFocused) focusedSize else size,
                            animationSpec = tween(durationMillis = 300),
                            label = "otpCellSize"
                        )
                        val char = when {
                            index == otpText.length -> "_"
                            index > otpText.length -> ""
                            else -> otpText[index].toString()
                        }
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (isFocused)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant,
                                    shape = MaterialTheme.shapes.small
                                )
                                .size(animatedSize),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (index < otpCount - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        )
    }
}
