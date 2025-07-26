package appdev.studybuddy.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton
import appdev.studybuddy.ui.theme.logOutRed
import appdev.studybuddy.viewModels.HomeVM
import appdev.studybuddy.viewModels.SessionVM
import appdev.studybuddy.viewModels.UserVM


@Composable
fun HomeScreen(
    navController: NavController,
    userVM: UserVM = hiltViewModel()
) {
    StudyBuddyScaffold {
        var displaySessionDialog by remember { mutableStateOf(false) }
        var displayLogoutDialog by remember { mutableStateOf(false) }

        if (displaySessionDialog) {
            SessionSettingsDialog(
                onDismiss = { displaySessionDialog = false },
                onClick = {},
            )
        }

        if (displayLogoutDialog) {
            LogoutDialog(
                onDismiss = { displayLogoutDialog = false },
                onClick = {
                    Log.d("Logout", "Logout before ${userVM.currentUser}")
                    userVM.logout()
                    Log.d("Logout", "Logout after ${userVM.currentUser}")
                    displayLogoutDialog = false
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
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
            ) {
                Text(text = "Logout")
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    navController.navigate("leaderboard")
                },
                modifier = Modifier
                    .background(PurpleButton, shape = RoundedCornerShape(15.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.trophyicon),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Hello ${userVM.currentUser}!",
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


/**
 * Dialog um den Logout zu bestätigen/ zu canceln
 */
@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                    colors = ButtonDefaults.buttonColors(containerColor = logOutRed),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Yes, Logout", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(15.dp)
                )
                {
                    Text("No, Cancel", color = PurpleButton)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = PurpleBackground
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

    LaunchedEffect(
        timeInputState.hour,
        timeInputState.minute
    ) { //listen for changes in timeInputState
        viewModel.setDuration(timeInputState.hour, timeInputState.minute)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Session Settings",
                fontSize = 20.sp
            )
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
                        checked = useBrightnessSensor.value,
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
