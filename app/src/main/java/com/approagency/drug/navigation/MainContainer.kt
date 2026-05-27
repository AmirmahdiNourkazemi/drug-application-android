package com.approagency.drug.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.approagency.drug.navigation.graphs.homeGraph
import com.approagency.drug.navigation.graphs.labGraph
import com.approagency.drug.navigation.graphs.searchGraph

@Composable
fun MainContainer() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when (currentRoute) {
        Screen.Home.route,
        Screen.Search.route,
        Screen.Lab.route -> true
        else -> false
    }
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = MainRoute.HomeGraph.route,
            modifier =if (showBottomBar ) Modifier.padding(padding) else Modifier

        ) {

            homeGraph(navController)

            searchGraph(navController)

            labGraph(navController)
        }
    }
}