package appdev.studybuddy.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import appdev.studybuddy.ui.theme.DarkGrey
import appdev.studybuddy.viewModels.SessionVM

/**
 * Dialog um generelle Einstellungen für eine Session einzustellen:
 * (Dauer, welche Sensoren verwendet werden sollen, ..)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionPropertiesDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionVM = hiltViewModel()
) {
    val sessionProperties by viewModel.sessionProperties.collectAsState()
    val isInvalidBreak by viewModel.isInvalidBreak.collectAsState()

    val durationTimerInput = rememberTimePickerState(
        initialHour = viewModel.getHours(),
        initialMinute = viewModel.getMinutes(),
        is24Hour = true,
    )

    val breakDurationTimerInput = rememberTimePickerState(
        initialHour = viewModel.getBreakHours(),
        initialMinute = viewModel.getBreakMinutes(),
        is24Hour = true,
    )

    LaunchedEffect(
        durationTimerInput.hour,
        durationTimerInput.minute
    ) {
        viewModel.setDuration(durationTimerInput.hour, durationTimerInput.minute)
    }

    LaunchedEffect(
        breakDurationTimerInput.hour,
        breakDurationTimerInput.minute
    ) {
        viewModel.setBreakDuration(breakDurationTimerInput.hour, breakDurationTimerInput.minute)
    }


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Session Settings",
                fontSize = 20.sp,
                color = Color.White
            )
        },

        text = {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item{
                    LabeledBox("Session Duration:") {
                        TimeInput(
                            state = durationTimerInput
                        )
                    }
                }

                item {
                    LabeledBox("Breaks:") {
                        TimeInput(
                            state = breakDurationTimerInput
                        )

                        SessionSettingsRow("Num Breaks"){

                            var numBreaks by remember { mutableStateOf<String>("${sessionProperties.numBreaks}") }

                            OutlinedTextField(
                                modifier = Modifier.wrapContentWidth(),
                                singleLine = true,
                                value = numBreaks,
                                onValueChange = { value ->
                                    viewModel.setNumBreaks(value)
                                    numBreaks = value
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next),
                            )

                        }

                        if(isInvalidBreak){
                            Text(
                                text = "Break(s) cant be longer than session!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    LabeledBox("Sensor Feedback:") {
                        SessionSettingsRow("Allow Sound Feedback:") {
                            Switch(
                                checked = sessionProperties.useSoundSensor,
                                onCheckedChange = { viewModel.setUseSoundSensor(it) }
                            )
                        }

                        SessionSettingsRow("Allow Movement Feedback:") {
                            Switch(
                                checked = sessionProperties.useMovementSensor,
                                onCheckedChange = { viewModel.setUseMovementSensor(it) }
                            )
                        }

                        SessionSettingsRow("Allow Brightness Feedback:") {
                            Switch(
                                checked = sessionProperties.useBrightnessSensor,
                                onCheckedChange = { viewModel.setUseBrightnessSensor(it) }
                            )
                        }
                    }
                }

            }
        },

        confirmButton = {},

        dismissButton = {
            Button(
                onClick = {
                    if (!isInvalidBreak){
                        onDismiss()
                    }
                }
            ) {
                Text("Confirm")
            }
        },

        modifier = modifier,
        containerColor = DarkGrey,
        textContentColor = Color.White
    )
}

@Composable
fun HotizontalNumberPicker(
    listState: LazyListState,
    list: List<Int> = (0..100).toList(),
) {

    Box(
        modifier = Modifier.background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(10.dp)
        ).border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(10.dp))
    ){
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 8.dp),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            items(list.size) { index ->

                Text(
                    text = "${list[index]}",
                    modifier = Modifier.padding(5.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }

}


/**
 * Labeld Box to group Session Properties
 */
@Composable
fun LabeledBox(
    labelText: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = labelText,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer,shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }

    }
}

/**
 * Zeile innerhalb einer LabeldBox()
 */
@Composable
fun SessionSettingsRow(
    descriptionText: String,
    content: @Composable () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = descriptionText,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.weight(1f)
        ){
            content()
        }

    }
}