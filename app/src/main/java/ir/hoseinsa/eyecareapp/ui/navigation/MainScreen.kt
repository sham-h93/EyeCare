package ir.hoseinsa.eyecareapp.ui.navigation

sealed class MainScreen(val route: String) {

    data object Main: MainScreen(route = "main")

}