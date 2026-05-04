package com.approagency.drug.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.approagency.drug.presentation.screens.HomeScreen
import androidx.navigation.compose.composable
import com.approagency.drug.presentation.screens.RootScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController ,
        startDestination = NavRoutes.Home.route
    ){
        composable(NavRoutes.Home.route) {
            RootScreen(
                navHostController = navController , modifier = Modifier
            )
        }
    }
}