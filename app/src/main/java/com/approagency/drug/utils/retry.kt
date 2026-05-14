package com.approagency.drug.utils

import kotlinx.coroutines.delay

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    maxDelayMs: Long = 5000,
    factor: Double = 2.0,
    onRetry: ((attempt: Int, delayMs: Long) -> Unit)? = null,
    block: suspend () -> T
): Result<T> {
    var currentDelay = initialDelayMs
    repeat(maxRetries - 1) { attempt ->
        try {
            return Result.success(block())
        } catch (e: Exception) {
            onRetry?.invoke(attempt + 1, currentDelay)
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
        }
    }
    // Last attempt
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
