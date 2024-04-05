package ir.hoseinsa.eyecareapp.ui.navigation

sealed class TimerScreen(val route: String) {

    data object Timer: TimerScreen(route = "main")

}