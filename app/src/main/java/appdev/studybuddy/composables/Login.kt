package appdev.studybuddy.composables

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import appdev.studybuddy.viewModels.UserVM

@Composable
fun LoginScreen(
    navController: NavController,
    userVM: UserVM = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var failed by remember { mutableStateOf(false) }

    var hasPermission by remember { mutableStateOf(false) }
    var context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Log.i("Recording Audio", "already granted")
            hasPermission = true
        } else {
            hasPermission = false
            permissionLauncher.launch(RECORD_AUDIO)
        }
    }

    StudyBuddyScaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "StudyBuddy"
            )

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (failed) {
                    Text("Email or Password is wrong!")
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail Address") }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    if (userVM.login(email, password)) {
                        navController.navigate("home")
                    } else {
                        failed = true
                    }
                }
            ) {
                Text(text = "Login")
            }

            Button(
                onClick = {
                    navController.navigate("register")
                }
            ) {
                Text(text = "Register")
            }

            //debug Button: Später wieder entfernen nur das man direkt zum Home-Screen kommt
            Button(
                onClick = {
                    navController.navigate("home")

                }
            ) {
                Text(text = "debug: directly to Home")
            }

            //debug Button: Später wieder entfernen nur das man direkt zum ExampleDB kommt
            Button(
                onClick = {
                    navController.navigate("exampledb")

                }
            ) {
                Text(text = "debug: directly to Example db")
            }

        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    userVM: UserVM = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var failed by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "StudyBuddy"
            )

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (failed) {
                    Text("User already exists!")
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-Mail Address") }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    if (userVM.register(email, password, username)) {
                        navController.navigate("home")
                    } else {
                        failed = true
                    }
                }
            ) {
                Text(text = "Register")
            }

        }
    }
}
