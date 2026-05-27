package com.approagency.drug.navigation.graphs

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.approagency.drug.navigation.MainRoute
import com.approagency.drug.navigation.Screen
import com.approagency.drug.presentation.screens.DrugDetailScreen
import com.approagency.drug.presentation.screens.SearchScreen

fun NavGraphBuilder.searchGraph(
    navController: NavHostController
) {

    navigation(
        route = MainRoute.SearchGraph.route,
        startDestination = Screen.Search.route
    ) {

        composable(Screen.Search.route) {

            SearchScreen(
                navController = navController
            )
        }

        composable(
            route = Screen.DrugDetail.route,
            arguments = listOf(
                navArgument("detailUrl") {
                    type = NavType.StringType
                }
            )
        ) {

            val detailUrl =
                Uri.decode(
                    it.arguments?.getString("detailUrl") ?: ""
                )

            DrugDetailScreen(
                navController = navController,
                detailUrl = detailUrl
            )
        }
    }
}