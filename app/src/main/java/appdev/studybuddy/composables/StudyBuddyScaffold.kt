package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import appdev.studybuddy.controller.SnackBarController

@Composable
fun StudyBuddyScaffold(
    content: @Composable ()-> Unit
) {
    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(SnackBarController.events) {
        SnackBarController.events.collect { event ->
            snackBarState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarState){ data ->
            CustomErrorSnackbar(
                data = data,
                backgroundColor = MaterialTheme.colorScheme.tertiary
            )
        } }
    ){innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}