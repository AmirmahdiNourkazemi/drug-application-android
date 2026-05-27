package com.approagency.drug.navigation

import android.net.Uri

sealed class MainRoute(
    val route: String
) {

    object HomeGraph : MainRoute("home_graph")

    object SearchGraph : MainRoute("search_graph")

    object LabGraph : MainRoute("lab_graph")
}

sealed class Screen(
    val route: String
) {

    object Home : Screen("home")

    object Search : Screen("search")

    object Lab : Screen("lab")

    object DrugDetail : Screen("drug_detail/{detailUrl}") {

        fun createRoute(detailUrl: String): String {
            return "drug_detail/${Uri.encode(detailUrl)}"
        }
    }
}