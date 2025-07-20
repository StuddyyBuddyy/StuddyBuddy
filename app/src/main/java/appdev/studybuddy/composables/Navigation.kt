package appdev.studybuddy.composables

import androidx.compose.runtime.Composable
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
    val sessionVM : SessionVM = viewModel()
    val homeVM: HomeVM = viewModel()
    val userVM : UserVM = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login")
    {
        composable("login"){
            LoginScreen(navController, userVM)
        }
        composable("home/{username}/{email}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            val email = backStackEntry.arguments?.getString("email")

            if (username!= null && email!= null) HomeScreen(navController = navController,homeVM,username,email)
        }
        composable("session") {
            SessionScreen(
                navController = navController, viewModel = sessionVM
            )
        }
        composable("exampledb") {
            ExampleDBScreen(DAO())
        }
    }
}