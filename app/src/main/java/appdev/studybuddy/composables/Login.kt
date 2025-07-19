package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import appdev.studybuddy.viewModels.UserVM

@Composable
fun LoginScreen(navController: NavController, userVM: UserVM){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "StudyBuddy"
            )

            Spacer(modifier = Modifier.padding(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail Address")}
            )

            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {Text("Password")}
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {}
            ) {
                Text(text = "Login")
            }

            Button(
                    onClick = {}
                    ) {
                Text(text = "Register")
            }

        }
    }
}