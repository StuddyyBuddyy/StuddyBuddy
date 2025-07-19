package appdev.studybuddy.composables

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import appdev.studybuddy.viewModels.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavSetup(){
    val navController = rememberNavController()
    val dataVM : DataVM = viewModel()
    val sessionVM : SessionVM = viewModel()
    val userVM : UserVM = viewModel()

    NavHost(navController, startDestination = "login"){
        composable("login"){LoginScreen(navController, userVM)}
        composable("register"){RegisterScreen(navController, userVM)}
        composable("home"){}
    }
}