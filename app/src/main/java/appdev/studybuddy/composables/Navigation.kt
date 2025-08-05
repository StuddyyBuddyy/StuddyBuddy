package appdev.studybuddy.composables

import LeaderboardScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import appdev.studybuddy.ExampleDBScreen
import appdev.studybuddy.models.DAO
import appdev.studybuddy.viewModels.*
import androidx.lifecycle.viewmodel.compose.viewModel
import appdev.studybuddy.composables.home.HomeScreen

@Composable
fun NavSetup() {
    val navController = rememberNavController()

    val dataVM: DataVM = viewModel()
    val sessionVM : SessionVM = hiltViewModel()
    val homeVM: HomeVM = viewModel()
    val userVM: UserVM = hiltViewModel()

    val startDestination = if (userVM.autoLogin()) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("session") {
            SessionScreen(
                navController = navController,
                viewModel = sessionVM.apply { user = userVM.currentUser!! }
            )
        }
        composable("exampledb") {
            ExampleDBScreen(DAO())
        }

        composable("leaderboard") {
            LeaderboardScreen(navController = navController)
        }

    }
}