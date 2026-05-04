package com.approagency.drug.navigation

abstract class NavRoutes (val route:String) {
    object Home: NavRoutes("home")
    object Detail: NavRoutes("detail/{cod}"){
        fun createRoute(cod: Int) = "detail/$cod"
    }
    object Lab: NavRoutes("lab")
}