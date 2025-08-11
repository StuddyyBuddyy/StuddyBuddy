import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import appdev.studybuddy.composables.StudyBuddyScaffold
import appdev.studybuddy.ui.theme.Purple40
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleButton
import appdev.studybuddy.viewModels.DataVM
import appdev.studybuddy.viewModels.UserVM

@Composable
fun LeaderboardScreen(navController: NavController,
                      userVM: UserVM = hiltViewModel(),
                      dataVM: DataVM = viewModel()
){
    StudyBuddyScaffold {

        var leaderboard: Map<String, Int> by remember { mutableStateOf(emptyMap()) }
        LaunchedEffect(Unit) {
            leaderboard = dataVM.sortUsersByPoints()
        }

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

            if(leaderboard.isEmpty()){
                //TODO add loading icon or Animation
            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(leaderboard.toList()) { (username, points) ->
                        LeaderboardRow(username, points)
                    }
                }
            }
        }
    }
}


@Composable
fun LeaderboardRow(username: String, points: Int) {
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
                text = username,
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
            Text(
                text = "$points Punkte",
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
        }
    }
}
