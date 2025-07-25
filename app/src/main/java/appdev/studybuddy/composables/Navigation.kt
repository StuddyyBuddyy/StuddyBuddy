package appdev.studybuddy.composables

import LeaderboardScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import appdev.studybuddy.ExampleDBScreen
import appdev.studybuddy.models.DAO
import appdev.studybuddy.viewModels.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import appdev.studybuddy.models.User

@Composable
fun NavSetup(){
    val navController = rememberNavController()

    val dataVM : DataVM = viewModel()
    // sessionVM : SessionVM = viewModel()
    val homeVM: HomeVM = viewModel()
    val userVM : UserVM = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login")
    {
        composable("login"){
            LoginScreen(navController, userVM)
        }
        composable("register"){
            RegisterScreen(navController, userVM)
        }
        composable("home") {
            HomeScreen(navController = navController,userVM)
        }
        composable("session") {
            SessionScreen(
                navController = navController
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