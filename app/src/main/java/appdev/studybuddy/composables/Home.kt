package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import appdev.studybuddy.models.Session
import java.util.Calendar
import kotlin.Int

@Composable
fun HomeScreen(navController: NavController){

    var displayDialog by remember{ mutableStateOf(false) }

    if (displayDialog){
        StartSessionDialog(
            onDismiss = {displayDialog = false},
            navController = navController
        )
    }

    //todo design home screen

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Text(text = "HomeScreen")

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            onClick = {
                displayDialog = true
            }
        ) {
            Text(text = "Start Session")
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartSessionDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var useMicrophoneSensor by remember {mutableStateOf(false)}
    var useVibrationSensor by remember {mutableStateOf(false)}
    var useBrightnessSensor by remember {mutableStateOf(false)}

    val timeInputState = rememberTimePickerState(
        initialHour = 2,
        initialMinute = 0,
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(text = "Start New Session",
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
                        checked = useMicrophoneSensor,
                        onCheckedChange = { useMicrophoneSensor = it }
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
                        checked = useVibrationSensor,
                        onCheckedChange = { useVibrationSensor = it }
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
                        checked =useBrightnessSensor,
                        onCheckedChange = { useBrightnessSensor = it }
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {

                }
            ) {
                Text("Start Session")
            }
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
