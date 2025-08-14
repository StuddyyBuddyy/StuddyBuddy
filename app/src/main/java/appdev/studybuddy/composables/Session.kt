package appdev.studybuddy.composables

import androidx.compose.runtime.rememberCoroutineScope
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.viewModels.SessionVM
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun SessionScreen(
    navController: NavController,
    viewModel: SessionVM = hiltViewModel()
) {
    val context = LocalContext.current

    var showFailDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorToast by remember { mutableStateOf(false) }

    val sessionProperties by viewModel.sessionProperties.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isBreak by viewModel.isBreak.collectAsState()
    val breakNotifier by viewModel.breakNotifier.collectAsState()

    val isTooDark by viewModel.isTooDark.collectAsState()
    val isTooLoud by viewModel.isTooLoud.collectAsState()
    val wasMobileMoved by viewModel.wasMobileMoved.collectAsState()

    val imageUrl by viewModel.dogImageUrl.collectAsState()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val coroutineScope = rememberCoroutineScope()

    StudyBuddyScaffold {


        BackHandler {
            showFailDialog = true
        }

        LaunchedEffect(imageUrl) {
            imageUrl?.let {
                imageBitmap = viewModel.loadBitmapFromUrl(it)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.startTimer()
        }

        DisposableEffect(viewModel) {
            viewModel.onResume()
            onDispose {
                viewModel.onPause()
            }
        }

        //Timer Berechnungen
        val progress = elapsedSeconds / sessionProperties.duration.toFloat()
        val remainingSeconds = sessionProperties.duration - elapsedSeconds
        val minutesLeft = remainingSeconds / 60
        val secondsLeft = remainingSeconds % 60

        if (remainingSeconds <= 0) {
            showSuccessDialog = true
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column( //Background Column für Timer
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_light_mode_24),
                        contentDescription = "Light Feedback",
                        modifier = Modifier.size(24.dp),
                        tint = if (isTooDark) Color.Red else Color.Green,
                    )

                    Spacer(Modifier.padding(10.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.speaker_filled_audio),
                        contentDescription = "Sound Feedback",
                        modifier = Modifier.size(24.dp),
                        tint = if (isTooLoud) Color.Red else Color.Green,
                    )
                }

                if (wasMobileMoved) {

                    //todo was soll alles passieren wenn Handy bewegt wird? Session abbrechen?

                    Text(
                        text = "Put your phone away!",
                        color = Color.Red
                    )
                }

                if (breakNotifier > 0) {
                    Text(
                        text = String.format("Break starts in: %d ", breakNotifier),
                        color = Color.Red,
                    )
                }

                Box(
                    modifier = Modifier.padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f - progress },
                        modifier = Modifier.size(250.dp),
                        color = Color(0xFF000000),
                        strokeWidth = 12.dp,
                        trackColor = if (isBreak) Color.Red else Color.Green,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )

                    Text(
                        text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                        color = if (isBreak) Color.Red else Color.Green,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                if (isBreak) {
                    Text(
                        text = String.format("Take a break! XXX todo ", isBreak),
                        color = Color.Red,
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

            Column( //Column am oberen Bildschirmrand für Notification Banner
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                if (isTooDark) {
                    Banner(text = "Its to dark, you might wanna turn on some light!")
                }

                if (isTooLoud) {
                    Banner(text = "Its to loud, you might wanna change your location!")
                }
            }

            if (showFailDialog) {

                EndSessionDialogFail(
                    onConfirm = {
                        val successful = viewModel.endSession(fail = true)
                        if (successful) {
                            coroutineScope.launch {
                                alarm(context)
                            }
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
                val context = LocalContext.current

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
                    onDownload = {
                        imageBitmap?.let {
                            val success = viewModel.saveImageToGallery(
                                context = context,
                                image = it,
                                fileName = "dog_${System.currentTimeMillis()}.jpg"
                            )

                            Toast.makeText(
                                context,
                                if (success) "Download successful" else "Download failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    image = imageBitmap
                )
            }

            if (showErrorToast) {
                ErrorToast(context)
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
        Column(modifier = Modifier.padding(16.dp)) {
            Text("End Session?")
            Text("Are you sure you want to end this session early?")
            Row {
                Button(onClick = onConfirm) {
                    Text("Confirm")
                }

                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun EndSessionDialogSuccess(
    onConfirm: () -> Unit,
    onDownload: () -> Unit,
    image: ImageBitmap?
) {
    DialogBox {
        Column(modifier = Modifier.padding(16.dp)) {
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

                Spacer(modifier = Modifier.size(12.dp))
                Button(
                    onClick = onDownload
                ) {
                    Text("Download")
                }
            }
        }
    }
}

@Composable
fun DialogBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .padding(48.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = PurpleBackground)
    ) {
        content()
    }
}

@Composable
fun ErrorToast(context : Context) {
    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "Something went wrong",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun Banner(
    text: String,
    buttonText: String? = null,
    buttonClickListener: (() -> Unit)? = null,
) {
    Column(
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(8.dp)
                .background(Color.Red),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            buttonText?.let {
                TextButton(
                    onClick = if (buttonClickListener != null) {
                        buttonClickListener
                    } else {
                        {}
                    }
                ) {
                    Text(
                        text = buttonText,
                    )
                }
            }
        }
    }
}

fun alarm(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val timings = longArrayOf(0, 500, 300, 500, 300, 500, 300, 500, 300)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1)) // -1 = no repeat

    val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
    mediaPlayer.start()
}
