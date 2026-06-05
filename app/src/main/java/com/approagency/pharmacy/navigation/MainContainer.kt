package com.approagency.pharmacy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun MainContainer(
    navController: NavHostController,
    content: @Composable () -> Unit
) {

    Scaffold(
        bottomBar = {
                BottomBar(navController)
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}