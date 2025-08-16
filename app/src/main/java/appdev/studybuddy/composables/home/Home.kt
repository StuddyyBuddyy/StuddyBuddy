package appdev.studybuddy.composables.home

import android.util.Log
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import appdev.studybuddy.R
import appdev.studybuddy.composables.StudyBuddyScaffold
import appdev.studybuddy.ui.theme.Purple40
import appdev.studybuddy.ui.theme.PurpleBackground
import appdev.studybuddy.ui.theme.PurpleBackground2
import appdev.studybuddy.ui.theme.PurpleDarkText
import appdev.studybuddy.ui.theme.logOutRed
import appdev.studybuddy.viewModels.DataVM
import appdev.studybuddy.viewModels.SessionVM
import appdev.studybuddy.viewModels.UserVM
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun HomeScreen(
    navController: NavController,
    userVM: UserVM = hiltViewModel(),
    sessionVM: SessionVM = hiltViewModel(),
    dataVM: DataVM = viewModel()
) {
    StudyBuddyScaffold {
        var displaySessionDialog by remember { mutableStateOf(false) }
        var displayLogoutDialog by remember { mutableStateOf(false) }

        if (displaySessionDialog) {
            SessionPropertiesDialog(
                onDismiss = { displaySessionDialog = false },
                viewModel = sessionVM
            )
        }

        var personalScoreboard: Map<String, Int> by remember { mutableStateOf(emptyMap()) }
        var userTotalPoints: Int by remember { mutableIntStateOf(0) }
        LaunchedEffect(userVM.currentUser) {
            userVM.currentUser?.let { user ->
                personalScoreboard = dataVM.sortSessionPoints(user)
                userTotalPoints = dataVM.addSessionPoints(user)
            }
        }

        BackHandler {
            //Do Nothing on Back Button/
        }

        if (displayLogoutDialog) {
            LogoutDialog(
                onDismiss = { displayLogoutDialog = false },
                onClick = {
                    Log.d("Logout", "Logout before ${userVM.currentUser}")
                    userVM.logout()
                    Log.d("Logout", "Logout after ${userVM.currentUser}")
                    displayLogoutDialog = false
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
        ) {
            Button(
                onClick = {
                    displayLogoutDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple40,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(text = "Logout")
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    navController.navigate("leaderboard")
                },
                modifier = Modifier
                    .background(Purple40, shape = RoundedCornerShape(15.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.trophyicon),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text = "Hello ${userVM.currentUser?.username}!",
                color = Purple40,
                fontSize = 30.sp,
                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
            )

            Spacer(modifier = Modifier.padding(10.dp))
            
            if(personalScoreboard.isEmpty() ){
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.loadingbook)
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(400.dp)
                    )
                }

            } else {

                Spacer(modifier = Modifier.padding(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .background(
                            color = PurpleBackground2,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp), // Innenabstand
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Your total score:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDarkText
                    )
                    Text(
                        text = "$userTotalPoints points",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDarkText
                    )
                }


                Spacer(modifier = Modifier.padding(10.dp))

                LazyColumn(
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .sizeIn(maxHeight = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(personalScoreboard.toList()) { (sessionDate, points) ->
                        PersonalScoreboardRow(sessionDate, points)
                    }
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row() {
                Button(
                    onClick = {
                        navController.navigate("session")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple40,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Start Session")
                }

                IconButton(
                    onClick = {
                        displaySessionDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Session Settings"
                    )

                }
            }

        }
    }
}


/**
 * Dialog um den Logout zu bestätigen/ zu canceln
 */
@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Logout")
            },
            text = {
                Text(text = "Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = logOutRed),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Yes, Logout", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(15.dp)
                )
                {
                    Text("No, Cancel", color = Purple40)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = PurpleBackground
        )
    }
}

@Composable
fun PersonalScoreboardRow(sessionDate: String, points: Int){

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
                text = sessionDate,
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
            Text(
                text = "$points points",
                style = MaterialTheme.typography.bodyLarge,
                color = Purple40
            )
        }
    }
}