package appdev.studybuddy.composables

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.ui.theme.DarkGrey
import appdev.studybuddy.ui.theme.OtherGrey
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton
import appdev.studybuddy.ui.theme.logOutRed
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
                onDismiss = { displaySessionDialog = false }
            )
        }

        BackHandler {
            //Do Nothing on Back Button
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
    modifier: Modifier = Modifier,
    viewModel: SessionVM = hiltViewModel()
) {
    val sessionProperties by viewModel.sessionProperties.collectAsState()
    val isInvalidBreak by viewModel.isInvalidBreak.collectAsState()

    val durationTimerInput = rememberTimePickerState(
        initialHour = viewModel.getHours(),
        initialMinute = viewModel.getMinutes(),
        is24Hour = true,
    )

    val breakDurationTimerInput = rememberTimePickerState(
        initialHour = viewModel.getBreakHours(),
        initialMinute = viewModel.getBreakMinutes(),
        is24Hour = true,
    )

    LaunchedEffect(
        durationTimerInput.hour,
        durationTimerInput.minute
    ) {
        viewModel.setDuration(durationTimerInput.hour, durationTimerInput.minute)
    }

    LaunchedEffect(
        breakDurationTimerInput.hour,
        breakDurationTimerInput.minute
    ) {
        viewModel.setBreakDuration(breakDurationTimerInput.hour, breakDurationTimerInput.minute)
    }


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Session Settings",
                fontSize = 20.sp,
                color = Color.White
            )
        },

        text = {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item{
                    LabeledBox("Session Duration:") {
                        TimeInput(
                            state = durationTimerInput
                        )
                    }
                }

                item {
                    LabeledBox("Breaks:") {
                        TimeInput(
                            state = breakDurationTimerInput
                        )

                        SessionSettingsRow("Num Breaks"){
                            OutlinedTextField(
                                modifier = Modifier.wrapContentWidth(),
                                singleLine = true,
                                value = sessionProperties.numBreaks.toString(),
                                onValueChange = { value ->
                                    if(value.isEmpty()){
                                        viewModel.setNumBreaks(0)
                                    }else{
                                        viewModel.setNumBreaks(value.toInt())
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next),
                            )
                        }

                        if(isInvalidBreak){
                            Text(
                                text = "Break(s) cant be longer then session!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    LabeledBox("Sensor Feedback:") {
                        SessionSettingsRow("Allow Volume Feedback:") {
                            Switch(
                                checked = sessionProperties.useMicrophoneSensor,
                                onCheckedChange = { viewModel.setUseMicrophoneSensor(it) }
                            )
                        }

                        SessionSettingsRow("Allow Vibration Feedback:") {
                            Switch(
                                checked = sessionProperties.useVibrationSensor,
                                onCheckedChange = { viewModel.setUseVibrationSensor(it) }
                            )
                        }

                        SessionSettingsRow("Allow Brightness Feedback:") {
                            Switch(
                                checked = sessionProperties.useBrightnessSensor,
                                onCheckedChange = { viewModel.setUseBrightnessSensor(it) }
                            )
                        }
                    }
                }

            }
        },

        confirmButton = {},

        dismissButton = {
            Button(
               onClick = {
                   if (!isInvalidBreak){
                       onDismiss()
                   }
               }
            ) {
                Text("Confirm")
            }
        },

        modifier = modifier,
        containerColor = DarkGrey,
        textContentColor = Color.White
    )
}

/**
 * Labeld Box to group Session Properties
 */
@Composable
fun LabeledBox(
    labelText: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = labelText,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                                .background(OtherGrey,shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }

    }
}

@Composable
fun SessionSettingsRow(
    descriptionText: String,
    content: @Composable () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = descriptionText,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.weight(1f)
        ){
            content()
        }

    }
}
