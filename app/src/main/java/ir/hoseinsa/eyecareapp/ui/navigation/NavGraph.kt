package ir.hoseinsa.eyecareapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ir.hoseinsa.eyecareapp.ui.main.MainScreen

@Composable
fun MainNav(
    modifier: Modifier = Modifier,
    navHost: NavHostController
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHost,
        startDestination = MainScreen.Main.route) {
        composable(MainScreen.Main.route) {
            MainScreen()
        }
    }
}