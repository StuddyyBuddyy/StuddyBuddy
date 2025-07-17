package appdev.studybuddy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController){
    //todo design login screen

    /*
        Platzhalter als Startbildschirm
        Navigiere entweder zur ExampleDB oder zum provisorischen Homescreen
     */
    Column(
        modifier = Modifier.padding(10.dp)
                        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = {
                navController.navigate("exampledb")
            }
        ) {
            Text(text = "to Exampledb")
        }

        Spacer(modifier = Modifier.size(10.dp))

        Button(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Text(text = "to HomeScreen")
        }
    }


}