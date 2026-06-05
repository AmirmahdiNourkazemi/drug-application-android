package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages <= 1) return

    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        PaginationButton(
            enabled = currentPage > 1,
            onClick = onPreviousPage,
            isPrevious = true
        )

        // Page numbers
        val pageRange = getVisiblePageRange(currentPage, totalPages)

        if (pageRange.first > 1) {
            PageNumberButton(page = 1, isSelected = false, onClick = onPageSelected)
            if (pageRange.first > 2) {
                Text("...", modifier = Modifier.padding(horizontal = 4.dp))
            }
        }

        pageRange.forEach { page ->
            PageNumberButton(
                page = page,
                isSelected = page == currentPage,
                onClick = onPageSelected
            )
        }

        if (pageRange.last < totalPages) {
            if (pageRange.last < totalPages - 1) {
                Text("...", modifier = Modifier.padding(horizontal = 4.dp))
            }
            PageNumberButton(page = totalPages, isSelected = false, onClick = onPageSelected)
        }

        // Next button
        PaginationButton(
            enabled = currentPage < totalPages,
            onClick = onNextPage,
            isPrevious = false
        )
    }
}

@Composable
private fun PageNumberButton(
    page: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF7D64BA) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF7D64BA)

    Text(
        text = page.toString(),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onClick(page) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = textColor,
        fontSize = 14.sp
    )
}

@Composable
private fun PaginationButton(
    enabled: Boolean,
    onClick: () -> Unit,
    isPrevious: Boolean
) {
    val icon = if (isPrevious) Icons.Default.ArrowBack else Icons.Default.ArrowForward
    val color = if (enabled) Color(0xFF7D64BA) else Color.Gray

    Icon(
        imageVector = icon,
        contentDescription = if (isPrevious) "Previous" else "Next",
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(8.dp),
        tint = color
    )
}

private fun getVisiblePageRange(currentPage: Int, totalPages: Int): IntRange {
    val maxVisible = 5
    val halfVisible = maxVisible / 2

    var start = currentPage - halfVisible
    var end = currentPage + halfVisible

    if (start < 1) {
        end += (1 - start)
        start = 1
    }
    if (end > totalPages) {
        start -= (end - totalPages)
        end = totalPages
    }
    if (start < 1) start = 1

    return start..end
}