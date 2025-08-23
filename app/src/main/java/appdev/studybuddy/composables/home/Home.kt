package appdev.studybuddy.composables.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import appdev.studybuddy.models.Session
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
        var selectedSession by remember { mutableStateOf<Session?>(null) }


        if (displaySessionDialog) {
            SessionPropertiesDialog(
                onDismiss = { displaySessionDialog = false },
                viewModel = sessionVM
            )
        }

        var sortedSessions: List<Session> by remember { mutableStateOf(emptyList()) }
        var userTotalPoints: Int by remember { mutableIntStateOf(0) }

        LaunchedEffect(userVM.currentUser) {
            userVM.currentUser?.let { user ->
                sortedSessions = dataVM.sortSessionsByPoints(user)
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

        if (selectedSession != null) {
            SessionDetailsDialog(
                session = selectedSession!!,
                onDismiss = { selectedSession = null }
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
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(15.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.trophyicon),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
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
                color = MaterialTheme.colorScheme.primary,
                fontSize = 30.sp,
                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
            )

            Spacer(modifier = Modifier.padding(10.dp))
            
            if(sortedSessions.isEmpty() ){
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
                            color = MaterialTheme.colorScheme.primaryContainer,
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$userTotalPoints points",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }


                Spacer(modifier = Modifier.padding(10.dp))

                LazyColumn(
                    modifier = Modifier
                        //.fillMaxSize()
                        .height(200.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedSessions) { session ->
                        PersonalScoreboardRow(session.date, session.points, onClick = { selectedSession = session })
                    }
                }
            }

            Spacer(modifier = Modifier.padding(30.dp))

            Row() {
                Button(
                    onClick = {
                        navController.navigate("session")
                    },
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
                        contentDescription = "Session Settings",
                    )

                }
            }

        }
    }
}

/**
 * Dialog um den Session Details anzuzeigen
 */
@Composable
fun SessionDetailsDialog(
    session: Session,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Session Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "ID: ${session.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Date: ${session.date}",
                    fontSize = 18.sp
                )
                Text(
                    "Duration: ${session.duration / 60} minutes",
                    fontSize = 18.sp
                )
                Text(
                    "Points: ${session.points}",
                    fontSize = 18.sp
                )
                if (session.description != null) {
                    Text(
                        "Description: ${session.description}",
                        fontSize = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
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
                Text(text = "Logout", color = MaterialTheme.colorScheme.onBackground)
            },
            text = {
                Text(text = "Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
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
                    Text("No, Cancel", color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun PersonalScoreboardRow(sessionDate: String,
                          points: Int,
                          onClick: () -> Unit){

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$points points",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}