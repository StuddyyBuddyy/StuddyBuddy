package appdev.studybuddy.composables

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import appdev.studybuddy.viewModels.*

@Composable
fun NavSetup(){
    val navController = rememberNavController()
    val dataVM  = DataVM()
    val sessionVM = SessionVM()
    val userVM = UserVM()

    NavHost(navController, startDestination = "login"){
        composable("login"){}
        composable("home"){}
    }
}