package appdev.studybuddy.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton
import appdev.studybuddy.viewModels.HomeVM
import appdev.studybuddy.viewModels.SessionVM
import appdev.studybuddy.viewModels.UserVM


@Composable
fun HomeScreen(navController: NavController,
               viewModel: HomeVM,
               username: String,
               email: String){

    var displaySessionDialog by remember{ mutableStateOf(false) }
    var displayLogoutDialog by remember { mutableStateOf(false) }
    val userVM: UserVM = hiltViewModel()

    if (displaySessionDialog){
        SessionSettingsDialog(
            onDismiss = {displaySessionDialog = false},
            onClick = {},
        )
    }

    if (displayLogoutDialog){
        LogoutDialog(
            onDismiss = { displayLogoutDialog = false },
            onClick = {
                userVM.logout()                     // Benutzer abmelden
                displayLogoutDialog = false        // Dialog schließen
                navController.navigate("login") {  // Navigation zum Login
                    popUpTo("home") { inclusive = true } // Zurück-Stack leeren
                }
            }
        )
    }

    //todo design home screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {

        Button(
            onClick = {
                displayLogoutDialog = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleButton,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(15.dp)
        ) {
            Text(text = "Logout")
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Hello $username!",
                color = PurpleButton,
            )

            Spacer(modifier = Modifier.padding(10.dp))

            Row() {
                Button(
                    onClick = {
                        navController.navigate("session")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleButton,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Start Session")
                }

                IconButton(
                    onClick = {
                        displaySessionDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Session Settings"
                    )

                }
            }

        }
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Logout")
            },
            text = {
                Text(text = "Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Yes, Logout", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("No, Cancel")
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * Dialog um generelle Einstellungen für eine Session einzustellen:
 * (Dauer, welche Sensoren verwendet werden sollen, ..)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSettingsDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionVM = hiltViewModel()
) {
    var useMicrophoneSensor = viewModel.useMicrophoneSensor.collectAsState()
    var useVibrationSensor = viewModel.useVibrationSensor.collectAsState()
    var useBrightnessSensor = viewModel.useBrightnessSensor.collectAsState()

    val timeInputState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 5,
        is24Hour = true,
    )

    LaunchedEffect(timeInputState.hour, timeInputState.minute) { //listen for changes in timeInputState
        viewModel.setDuration(timeInputState.hour, timeInputState.minute)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(text = "Session Settings",
                fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                TimeInput(
                    state = timeInputState
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Allow Volume Feedback:",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useMicrophoneSensor.value,
                        onCheckedChange = { viewModel.setUseMicrophoneSensor(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Allow Vibration Feedback:",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useVibrationSensor.value,
                        onCheckedChange = { viewModel.setUseVibrationSensor(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Allow Brightness Feedback:",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked =useBrightnessSensor.value,
                        onCheckedChange = { viewModel.setUseBrightnessSensor(it) }
                    )
                }
            }
        },

        confirmButton = {
        },

        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        },

        modifier = modifier
    )
}
