import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton

@Composable
fun LeaderboardScreen(navController: NavController){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Column() {
            Row (
                modifier = Modifier
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
            ){
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .background(PurpleButton, shape = RoundedCornerShape(15.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.padding(50.dp))

            Row(
                modifier = Modifier
                    .padding(top = 30.dp, start = 15.dp, end = 15.dp)
            ) {
                Text(
                    text = "This is the LeaderboardScreen!"
                )
            }
        }
    }
}
