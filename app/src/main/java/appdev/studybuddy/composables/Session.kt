package appdev.studybuddy.composables


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
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

        var showFailDialog by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var showErrorToast by remember { mutableStateOf(false) }


        //todo move logic to viewmodel and make screen pretty

        val durationMinutes = viewModel.sessionProperties.collectAsState()

        val totalDurationSeconds = durationMinutes.value!!.duration

        var elapsedSeconds by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (elapsedSeconds < totalDurationSeconds!!) {
                delay(1000)
                elapsedSeconds++
            }
        }

        val progress = elapsedSeconds / totalDurationSeconds.toFloat()
        val remainingSeconds = totalDurationSeconds - elapsedSeconds
        val minutesLeft = remainingSeconds / 60
        val secondsLeft = remainingSeconds % 60

        if (remainingSeconds <= 0){
            showSuccessDialog = true
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
                    showFailDialog = true
                }
            ) {
                Text("End Session")
            }
        }
        if (showFailDialog) {
            EndSessionDialogFail(
                onConfirm = {
                    val successful = viewModel.endSession(fail = true)
                    if (successful) {
                        navController.popBackStack()
                        showFailDialog = false
                    } else {
                        showErrorToast = true
                    }
                },
                onDismiss = { showFailDialog = false }
            )
        }

        if (showSuccessDialog) {
            EndSessionDialogSuccess(
                onConfirm = {
                    val successful = viewModel.endSession()
                    if (successful) {
                        navController.popBackStack()
                        showSuccessDialog = false
                    } else {
                        showErrorToast = true
                    }
                },
                onDismiss = {}
            )
        }

        if (showErrorToast) {
            ErrorToast()
            showErrorToast = false
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndSessionDialogFail(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("End Session?") },
        text = { Text("Are you sure you want to end this session early?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndSessionDialogSuccess(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Congratulations") },
        text = { Text("You successfully ended a StudySession") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Return to Home")
            }
        }
    )
}

@Composable
fun ErrorToast() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "Something went wrong",
            Toast.LENGTH_SHORT
        ).show()
    }
}

