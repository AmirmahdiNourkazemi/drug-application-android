package com.approagency.drug.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.approagency.drug.navigation.MainRoute
import com.approagency.drug.navigation.Screen
import com.approagency.drug.presentation.screens.LabScreen

fun NavGraphBuilder.labGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.LabGraph.route,
        startDestination = Screen.Lab.route
    ) {

        composable(Screen.Lab.route) {

            LabScreen()
        }
    }
}