package appdev.studybuddy.composables.session

import androidx.compose.runtime.rememberCoroutineScope
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.composables.StudyBuddyScaffold
import appdev.studybuddy.ui.theme.Pink40
import appdev.studybuddy.ui.theme.Pink80
import appdev.studybuddy.viewModels.SessionVM
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale", "CoroutineCreationDuringComposition")
@Composable
fun SessionScreen(
    navController: NavController,
    viewModel: SessionVM = hiltViewModel()
) {
    val context = LocalContext.current

    var showErrorToast by remember { mutableStateOf(false) }
    var dialogOption by remember { mutableStateOf(DialogOption.NONE) }

    val sessionProperties by viewModel.sessionProperties.collectAsState()
    val overallElapsedSeconds by viewModel.overallElapsedSeconds.collectAsState()
    val segmentElapsedSeconds by viewModel.segmentElapsedSeconds.collectAsState()
    val isBreak by viewModel.isBreak.collectAsState()

    val isTooDark by viewModel.isTooDark.collectAsState()
    val isTooLoud by viewModel.isTooLoud.collectAsState()
    val wasMobileMoved by viewModel.wasMobileMoved.collectAsState()

    if(wasMobileMoved==viewModel.MOVEMENT_LIMIT){
        dialogOption = DialogOption.MOVED
    }

    val imageUrl by viewModel.dogImageUrl.collectAsState()
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current


    StudyBuddyScaffold {

        BackHandler {
            dialogOption = DialogOption.INTERRUPT
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

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE && navController.previousBackStackEntry!=null) {
                    val successful = viewModel.endSession(fail = true)
                    if (successful) {
                        navController.popBackStack()
                        dialogOption = DialogOption.NONE
                    } else {
                        showErrorToast = true
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
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
                //Timer Berechnungen
                val progress = overallElapsedSeconds / sessionProperties.duration.toFloat()

                val overallRemainingSeconds = sessionProperties.duration - overallElapsedSeconds
                val overallHoursLeft = overallRemainingSeconds / 3600
                val overallMinutesLeft = overallRemainingSeconds % 3600 / 60
                val overallSecondsLeft = overallRemainingSeconds % 60

                val segmentRemainingSeconds = if(isBreak) (sessionProperties.durationBreak - segmentElapsedSeconds) else (viewModel.sessionTimeSegment - segmentElapsedSeconds)
                val segmentHoursLeft = segmentRemainingSeconds / 3600
                val segmentMinutesLeft = segmentRemainingSeconds % 3600 / 60
                val segmentSecondsLeft = segmentRemainingSeconds % 60

                if (overallRemainingSeconds == 0 && dialogOption == DialogOption.NONE) {
                    dialogOption = DialogOption.DESCRIPTION_SUCCESS
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_light_mode_24),
                        contentDescription = "Light Feedback",
                        modifier = Modifier.size(24.dp),
                        tint = if (isTooDark && !isBreak) Pink40 else Pink80,
                    )

                    Spacer(Modifier.padding(10.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.speaker_filled_audio),
                        contentDescription = "Sound Feedback",
                        modifier = Modifier.size(24.dp),
                        tint = if (isTooLoud && !isBreak) Pink40 else Pink80,
                    )
                }

                Box(
                    modifier = Modifier.padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f - progress },
                        modifier = Modifier.size(250.dp),
                        color = Color(0xFF000000),
                        strokeWidth = 12.dp,
                        trackColor = if (isBreak) Pink40 else Pink80,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )

                    Text(
                        text = String.format("${if (isBreak) "Break Segment" else "Learn Segment"}\n" +
                                "%02d:%02d:%02d", segmentHoursLeft, segmentMinutesLeft, segmentSecondsLeft),
                        textAlign = TextAlign.Center,
                        color = if (isBreak) Pink40 else Pink80,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 25.sp
                    )
                }

                Text(
                    text = String.format("Overall: %02d:%02d:%02d", overallHoursLeft, overallMinutesLeft, overallSecondsLeft),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 15.sp,
                    color = Pink80
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Button(
                    onClick = {
                        dialogOption = DialogOption.INTERRUPT
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
                if (isTooDark && !isBreak) {
                    Banner(text = "Its too dark, you might wanna turn on some light!")
                }

                if (isTooLoud && !isBreak) {
                    Banner(text = "Its too loud, you might wanna change your location!")
                }
            }

            Column( //Column am unteren Bildschirmrand für Sensor Debugging
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                val lightLevel by viewModel.lightLevel.collectAsState()
                val soundAmplitude by viewModel.soundAmplitude.collectAsState()
                val movementMagnitude by viewModel.movementMagnitude.collectAsState()

                Text(
                    text = "Debug Light: $lightLevel"
                )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = "Debug Sound: $soundAmplitude"
                )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = "Debug Movement: $movementMagnitude"
                )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = "Was Moved Counter: $wasMobileMoved"
                )
            }

            when (dialogOption) {
                DialogOption.NONE -> {}

                DialogOption.SUCCESS -> {
                    LaunchedEffect(Unit) {
                        viewModel.fetchDogImage()
                    }

                    EndSessionDialogSuccess(
                        onConfirm = {
                            val successful = viewModel.endSession()
                            if (successful) {
                                navController.popBackStack()
                                dialogOption = DialogOption.NONE
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

                DialogOption.INTERRUPT -> {
                    EndSessionDialogFail(
                        onConfirm = {
                            dialogOption = DialogOption.DESCRIPTION_FAIL
                        },
                        onDismiss = { dialogOption = DialogOption.NONE }
                    )
                }

                DialogOption.DESCRIPTION_FAIL -> {
                    DescriptionDialog(
                        onConfirm = {
                            val successful = viewModel.endSession(fail = true)
                            if (successful) {
                                navController.popBackStack()
                                dialogOption = DialogOption.NONE
                            } else {
                                showErrorToast = true
                            }
                        },
                        onDismiss = {
                            dialogOption = DialogOption.NONE
                        },
                        sessionVM = viewModel
                    )
                }

                DialogOption.DESCRIPTION_SUCCESS -> {
                    DescriptionDialog(
                        onConfirm = {
                            dialogOption = DialogOption.SUCCESS
                        },
                        onDismiss = {},
                        dismissable = false,
                        sessionVM = viewModel
                    )
                }

                DialogOption.MOVED -> {
                    val successful = viewModel.endSession(fail = true)
                    if (successful) {
                        Log.e("LIFECYCLE DEBUG","onPause triggerd ${navController.currentBackStackEntry.toString()}")
                        navController.popBackStack()
                        dialogOption = DialogOption.NONE
                    } else {
                        showErrorToast = true
                    }
                }
            }

            if (showErrorToast) {
                ErrorToast(context)
                showErrorToast = false
            }
        }
    }

}


@Composable
fun ErrorToast(context: Context) {
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
                .background(Pink40),
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


enum class DialogOption {
    NONE,
    SUCCESS,
    INTERRUPT,
    DESCRIPTION_SUCCESS,
    DESCRIPTION_FAIL,
    MOVED
}