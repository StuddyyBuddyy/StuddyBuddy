package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StudyBuddyScaffold(
    content: @Composable ()-> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ){innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}