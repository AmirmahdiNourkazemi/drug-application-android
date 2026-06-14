package com.approagency.pharmacy.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.approagency.pharmacy.presentation.screens.DrugDetailScreen
import com.approagency.pharmacy.presentation.screens.HomeScreen
import com.approagency.pharmacy.presentation.screens.LabScreen
import com.approagency.pharmacy.presentation.screens.SearchScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {

        // ───────────────
        // BOTTOM TABS
        // ───────────────

        composable("home") {
            MainContainer(navController) {
                HomeScreen(navController , modifier = Modifier)
            }
        }

        composable("search") {
            MainContainer(navController) {
                SearchScreen(navController,)
            }
        }

        composable("lab") {
            MainContainer(navController) {
                LabScreen()
            }
        }

        // ───────────────
        // GLOBAL SCREEN
        // ───────────────

        composable(
            route = Screen.DrugDetail.route,
            arguments = listOf(
                navArgument("detailUrl") {
                    type = NavType.StringType
                }
            )
        ) {

            val detailUrl =
                Uri.decode(it.arguments?.getString("detailUrl") ?: "")

            DrugDetailScreen(
                navController = navController,
                detailUrl = detailUrl
            )
        }
    }
}