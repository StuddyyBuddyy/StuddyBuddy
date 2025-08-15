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
import appdev.studybuddy.composables.home.HomeScreen
import appdev.studybuddy.composables.session.SessionScreen

@Composable
fun NavSetup() {
    val navController = rememberNavController()

    val sessionVM : SessionVM = hiltViewModel()
    val userVM: UserVM = hiltViewModel()

    val startDestination = if (userVM.autoLogin()) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        composable("login") {
            LoginScreen(
                navController = navController,
                userVM = userVM
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                userVM = userVM
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                userVM = userVM,
                sessionVM = sessionVM
            )
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
            LeaderboardScreen(
                navController = navController
            )
        }

    }
}