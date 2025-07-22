package appdev.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import appdev.studybuddy.composables.NavSetup
import appdev.studybuddy.models.*
import appdev.studybuddy.ui.theme.StudyBuddyTheme
import appdev.studybuddy.viewModels.SessionVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyBuddyTheme {
                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavSetup() // Your main navigation composable
                }
            }
        }
    }
}
