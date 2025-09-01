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
import kotlinx.coroutines.delay


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

        var sortedSessions: List<Session> by remember { mutableStateOf(emptyList()) }
        var userTotalPoints: Int by remember { mutableIntStateOf(0) }

        LaunchedEffect(userVM.currentUser) {
            userVM.currentUser?.let { user ->
                sortedSessions = dataVM.sortSessionsByPoints(user)
                userTotalPoints = dataVM.addSessionPoints(user)
            }
        }

        var showLoading by remember { mutableStateOf(sortedSessions.isEmpty()) }

        // If no sessions are found, show loading screen for 5 seconds
        LaunchedEffect(sortedSessions) {
            if (sortedSessions.isEmpty()) {
                delay(5000)
                showLoading = false
            } else {
                showLoading = false
            }
        }

        if (displaySessionDialog) {
            SessionPropertiesDialog(
                onDismiss = { displaySessionDialog = false },
                viewModel = sessionVM
            )
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

        BackHandler {
            //Do Nothing on Back Button/
        }

        Row(
            modifier = Modifier
                .padding(top = 30.dp, start = 15.dp, end = 15.dp)
        ) {
            //----------Logout Button----------------
            Button(
                onClick = {
                    displayLogoutDialog = true
                },
                shape = RoundedCornerShape(15.dp),
            ) {
                Text(text = "Logout")
            }

            Spacer(modifier = Modifier.weight(1f))

            //----------Button leading to Leaderboard Screen----------------
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

            //----------Loading Animation----------------
            if(showLoading){
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

                //----------Row for showing total score----------------
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

                //----------Lazy Column/ Scrollable list for showing all sessions (sorted by points descending)----------------
                LazyColumn(
                    modifier = Modifier
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
                //---------Button to start session---------
                Button(
                    onClick = {
                        navController.navigate("session")
                    },
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Start Session")
                }

                //---------Button to open SessionPropertiesDialog---------
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
 * Displays a dialog containing detailed information about a given [Session].
 *
 * This composable shows an [AlertDialog] with the session's ID, date, duration,
 * points, and optional description. It also provides a confirmation button
 * to close the dialog.
 *
 * @param session The [Session] object whose details will be displayed.
 * @param onDismiss Callback invoked when the dialog is dismissed or
 *                  the close button is pressed.
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
        //----------Button to close session details----------------
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
 * Displays a confirmation dialog for logging out.
 *
 * This composable shows an [AlertDialog] that asks the user to confirm or cancel
 * the logout action. It provides two buttons: one to confirm the logout and another
 * to cancel and dismiss the dialog.
 *
 * @param onDismiss Callback invoked when the user cancels the action or
 *                  dismisses the dialog.
 * @param onClick Callback invoked when the user confirms the logout action.
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
            //----------Button to confirm logout----------------
            confirmButton = {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("Yes, Logout", color = Color.White)
                }
            },
            //----------Button to cancel logout----------------
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

/**
 * Displays a row in the personal scoreboard with a session date and earned points.
 *
 * This composable uses a [Card] that shows the session's date on the left
 * and the corresponding points on the right. The entire row is clickable
 * and can trigger a given action when selected.
 *
 * @param sessionDate The formatted date string representing the session.
 * @param points The number of points earned in the session.
 * @param onClick Callback invoked when the row is clicked.
 */
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