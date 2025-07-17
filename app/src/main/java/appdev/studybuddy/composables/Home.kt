package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController){

    var displayDialog by remember{ mutableStateOf(false) }

    if (displayDialog){
        StartSessionDialog(
            onDismiss = {displayDialog = false},
            onConfirm = {},
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


@Composable
fun StartSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(text = "Start New Session?")
        },
        text = {
            Text(text = "Are you ready to begin a new game session? This action cannot be undone.") // Content of the dialog
        },
        confirmButton = {
            Button(
                onClick = onConfirm // When confirmed, call the onConfirm callback
            ) {
                Text("Start Session") // Text for the confirm button
            }
        },
        dismissButton = {
            Button( // Using TextButton for a secondary action
                onClick = onDismiss // When dismissed, call the onDismiss callback
            ) {
                Text("Cancel") // Text for the dismiss button
            }
        },
        modifier = modifier // Apply the external modifier to the AlertDialog itself
    )
}