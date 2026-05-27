package com.approagency.drug.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
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