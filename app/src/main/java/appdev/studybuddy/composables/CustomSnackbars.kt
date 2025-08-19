package appdev.studybuddy.composables

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CustomErrorSnackbar(
    data: SnackbarData,
    backgroundColor: Color
) {
    Snackbar(
        snackbarData = data,
        containerColor = backgroundColor,
        contentColor = Color.White // Text color for the message
    )
}