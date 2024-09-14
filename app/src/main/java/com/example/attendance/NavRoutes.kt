package com.example.attendance


sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Home : NavRoutes("home")
}