package com.approagency.drug.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.approagency.drug.navigation.graphs.homeGraph
import com.approagency.drug.navigation.graphs.labGraph
import com.approagency.drug.navigation.graphs.searchGraph

@Composable
fun MainContainer() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = MainRoute.HomeGraph.route,
            modifier = Modifier.padding(padding)
        ) {

            homeGraph(navController)

            searchGraph(navController)

            labGraph(navController)
        }
    }
}