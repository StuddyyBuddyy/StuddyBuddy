package appdev.studybuddy.composables

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import appdev.studybuddy.ExampleDBScreen
import appdev.studybuddy.models.DAO

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("home") {
           HomeScreen(navController = navController)
        }

        composable("exampledb") {
            ExampleDBScreen(DAO())
        }

    }
}