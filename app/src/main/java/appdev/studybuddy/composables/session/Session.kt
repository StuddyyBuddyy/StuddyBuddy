package appdev.studybuddy.composables.session

import androidx.compose.runtime.rememberCoroutineScope
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.viewModels.SessionVM
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun SessionScreen(
    navController: NavController,
    viewModel: SessionVM = hiltViewModel()
) {
    val context = LocalContext.current

    var showErrorToast by remember { mutableStateOf(false) }
    var dialogOption by remember { mutableStateOf(DialogOption.NONE) }

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


    /*
    _root_ide_package_.appdev.studybuddy.composables.StudyBuddyScaffold {


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

        //Timer Berechnungen
        val progress = elapsedSeconds / sessionProperties.duration.toFloat()
        val remainingSeconds = sessionProperties.duration - elapsedSeconds
        val minutesLeft = remainingSeconds / 60
        val secondsLeft = remainingSeconds % 60

        if (remainingSeconds == 0 && dialogOption == DialogOption.NONE) {
            dialogOption = DialogOption.DESCRIPTION_SUCCESS
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
                if (isTooDark) {
                    Banner(text = "Its too dark, you might wanna turn on some light!")
                }

                if (isTooLoud) {
                    Banner(text = "Its too loud, you might wanna change your location!")
                }
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
                                coroutineScope.launch {
                                    viewModel.alarm(context)
                                }
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
            }

            if (showErrorToast) {
                ErrorToast(context)
                showErrorToast = false
            }
        }
    }
    */
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


enum class DialogOption {
    NONE,
    SUCCESS,
    INTERRUPT,
    DESCRIPTION_SUCCESS,
    DESCRIPTION_FAIL
}