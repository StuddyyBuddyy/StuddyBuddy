package appdev.studybuddy.composables.session

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import appdev.studybuddy.viewModels.SessionVM

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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DescriptionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    sessionVM: SessionVM,
    dismissable: Boolean = true
) {
    var text by remember { mutableStateOf("") }

    DialogBox {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Please Enter Description for this Session!")
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                value = text,
                onValueChange = { text = it }
            )
            Row(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        onConfirm()
                        sessionVM.sessionDescription = text
                    }
                ) {
                    Text("Confirm")
                }

                if (dismissable) {
                    Button(onClick = onDismiss) {
                        Text("Dismiss")
                    }
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
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        content()
    }
}
