package appdev.studybuddy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.Session
import appdev.studybuddy.models.User
import kotlinx.coroutines.launch

@Composable
fun ExampleDBScreen(dao: DAO) {
    val coroutineScope = rememberCoroutineScope()
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var sessions by remember { mutableStateOf<List<Session>>(emptyList()) }
    var emailInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("API Test")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = {
                coroutineScope.launch {
                    users = dao.getAllUsers()
                    message = "Fetched ${users.size} users"
                }
            }) {
                Text("Get All Users")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                coroutineScope.launch {
                    sessions = dao.getUserSessions(emailInput)
                    message = "Fetched ${sessions.size} sessions for $emailInput"
                }
            }) {
                Text("Get Sessions")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                val user = User("TestUser", emailInput, "testpass")
                val result = dao.insertUser(user)
                message = if (result) "User added" else "Failed to add user"
            }
        }) {
            Text("Add User")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                val session = Session(
                    id = 0,
                    userEmail = emailInput,
                    date = "2025-07-11",
                    duration = 60,
                    points = 10,
                    description = "Test session"
                )
                val result = dao.insertSession(session)
                message = if (result) "Session added" else "Failed to add session"
            }
        }) {
            Text("Add Session")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Message: $message")

        Spacer(modifier = Modifier.height(16.dp))

        if (users.isNotEmpty()) {
            Text("Users:")
            users.forEach { Text("- ${it.username} (${it.email})") }
        }

        if (sessions.isNotEmpty()) {
            Text("Sessions:")
            sessions.forEach {
                Text("- ${it.date}: ${it.description} (${it.duration} mins, ${it.points} pts)")
            }
        }
    }
}