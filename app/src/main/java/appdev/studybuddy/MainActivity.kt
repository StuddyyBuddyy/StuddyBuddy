package appdev.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import appdev.studybuddy.composables.NavSetup
import appdev.studybuddy.models.*
import appdev.studybuddy.ui.theme.StudyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyBuddyTheme {
                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> }
                NavSetup()
            }
        }
    }
}
