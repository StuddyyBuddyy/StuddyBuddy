package appdev.studybuddy.composables


import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Picture
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.viewModels.SessionVM

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

        val sessionProperties by viewModel.sessionProperties.collectAsState()
        val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()

        val imageUrl by viewModel.dogImageUrl.collectAsState()
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(imageUrl) {
            imageUrl?.let {
                imageBitmap = viewModel.loadBitmapFromUrl(it)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.startTimer()
        }

        val progress = elapsedSeconds / sessionProperties.duration.toFloat()
        val remainingSeconds = sessionProperties.duration - elapsedSeconds
        val minutesLeft = remainingSeconds / 60
        val secondsLeft = remainingSeconds % 60

        if (remainingSeconds <= 0){
            showSuccessDialog = true
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                LaunchedEffect(Unit) {
                    viewModel.fetchDogImage()
                }
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
                    onDismiss = {},
                    imageBitmap
                )
            }

            if (showErrorToast) {
                ErrorToast()
                showErrorToast = false
            }
        }
    }
}

@Composable
fun EndSessionDialogFail(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DialogBox {
        Column (modifier = Modifier.padding(16.dp)) {
            Text("End Session?")
            Text("Are you sure you want to end this session early?")
            Row {
                Button(onClick = onConfirm){
                    Text("Confirm")
                }

                Button(onClick = onDismiss){
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun EndSessionDialogSuccess(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    image : ImageBitmap?
) {
    DialogBox {
        Column (modifier = Modifier.padding(16.dp)) {
            Text("Congratulations!")
            Text("Here is a cute Dog Picture for you!")
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Random Dog",
                    modifier = Modifier.size(300.dp)
                )
            } else {
                Text("Loading...")
            }
            Row {
                Button(onClick = onConfirm) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
fun DialogBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Box(
        modifier = modifier
            .padding(48.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = PurpleBackground)
    ){
        content()
    }
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

