package appdev.studybuddy.composables


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.viewModels.SessionVM
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Composable
fun SessionScreen(
    navController: NavController,
    viewModel: SessionVM = hiltViewModel()
){
    StudyBuddyScaffold {

        //todo move logic to viewmodel and make screen pretty

        val durationMinutes = viewModel.duration.collectAsState().value

        val totalDurationSeconds = durationMinutes * 60

        var elapsedSeconds by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (elapsedSeconds < totalDurationSeconds) {
                delay(1000)
                elapsedSeconds++
            }
        }

        val progress = elapsedSeconds / totalDurationSeconds.toFloat()
        val remainingSeconds = totalDurationSeconds - elapsedSeconds
        val minutesLeft = remainingSeconds / 60
        val secondsLeft = remainingSeconds % 60

        if (secondsLeft < 0){
            navController.popBackStack() //TODO recap PopUp
            viewModel.endSession(fail = false)
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f - progress },
                    modifier = Modifier.size(250.dp),
                    color = Color(0xFF000000),
                    strokeWidth = 12.dp,
                    trackColor = Color(0xFFD295DB),
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )

                Text(
                    text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = {
                    navController.popBackStack() //TODO recap PopUp
                    viewModel.endSession(fail = true)
                }
            ) {
                Text("End Session")
            }
        }
    }
}