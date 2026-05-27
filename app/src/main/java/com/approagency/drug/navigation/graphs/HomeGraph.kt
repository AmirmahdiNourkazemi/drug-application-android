package com.approagency.drug.navigation.graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.approagency.drug.navigation.MainRoute
import com.approagency.drug.navigation.Screen
import com.approagency.drug.presentation.screens.HomeScreen

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.HomeGraph.route,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {

            HomeScreen(
                navController = navController , modifier = Modifier
            )
        }
    }
}