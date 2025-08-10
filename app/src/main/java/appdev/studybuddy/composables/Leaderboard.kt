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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import appdev.studybuddy.composables.StudyBuddyScaffold
import appdev.studybuddy.ui.theme.Purple40
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton

@Composable
fun LeaderboardScreen(navController: NavController){

    val leaderboard = listOf(
        LeaderboardEntry("Anna", 450),
        LeaderboardEntry("Ben", 390),
        LeaderboardEntry("Clara", 350),
        LeaderboardEntry("David", 320),
        LeaderboardEntry("Eva", 300)
    )

    StudyBuddyScaffold {
        Column {
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

            Spacer(modifier = Modifier.padding(25.dp))

            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40,
                fontSize = 50.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.padding(25.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(leaderboard) { entry ->
                    LeaderboardRow(entry)
                }
            }
        }
    }
}
data class LeaderboardEntry(
    val username: String,
    val points: Int
)

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.username,
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
            Text(
                text = "${entry.points} Punkte",
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
        }
    }
}