package com.approagency.pharmacy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.approagency.pharmacy.presentation.common.CustomBox
import com.vada.caller.ui.theme.dime

@Composable
fun EmptyStateContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        , verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier.size(MaterialTheme.dime.iconLg)
        )
        CustomBox(
            child = {
                Text(
                    text = "برای جستجو، نام دارو را وارد کنید",
                    modifier = Modifier.padding(MaterialTheme.dime.sm)
                )
            }
        )
    }
}