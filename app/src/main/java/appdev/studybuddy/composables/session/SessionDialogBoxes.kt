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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults

@Composable
fun EndSessionDialogFail(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DialogBox {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("End Session?")
            Text("Are you sure you want to end this session early?")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor =  MaterialTheme.colorScheme.onError),
                ) {
                    Text("Confirm")
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
                Button(onClick = onConfirm, shape = RoundedCornerShape(15.dp)) {
                    Text("Confirm")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Spacer(modifier = Modifier.size(12.dp))
                Button(
                    onClick = onDownload, shape = RoundedCornerShape(15.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                value = text,
                onValueChange = { text = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if (dismissable) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Dismiss")
                    }
                }

                Button(
                    onClick = {
                        onConfirm()
                        sessionVM.sessionDescription = text
                    },
                    shape = RoundedCornerShape(15.dp)
                ) {
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
