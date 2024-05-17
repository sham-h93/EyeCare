package example.hotaku.eyecareapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import example.hotaku.eyecareapp.ui.main.TimerScreen

@Composable
fun MainNav(
    modifier: Modifier = Modifier,
    navHost: NavHostController
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHost,
        startDestination = TimerScreen.Timer.route) {
        composable(TimerScreen.Timer.route) {
            TimerScreen()
        }
    }
}