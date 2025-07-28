package appdev.studybuddy.viewModels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.models.SessionProperties
import appdev.studybuddy.models.User
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionVM @Inject  constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var _sessionProperties = MutableStateFlow<SessionProperties>(SessionProperties())
    val sessionProperties: StateFlow<SessionProperties> = _sessionProperties

    init {
        viewModelScope.launch {
            userPreferences.lastSessionProperties.collect { it -> _sessionProperties.value = it }
        }
    }

    fun setUseMicrophoneSensor(useMicrophoneSensor: Boolean){
        _sessionProperties.value.useMicrophoneSensor = useMicrophoneSensor
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setUseVibrationSensor(useVibrationSensor: Boolean){
        _sessionProperties.value.useVibrationSensor = useVibrationSensor
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setUseBrightnessSensor(useBrightnessSensor: Boolean){
        _sessionProperties.value.useBrightnessSensor = useBrightnessSensor
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value) }
    }

    fun setDuration(hours: Int, minutes: Int){
        _sessionProperties.value.duration = hours * 3600 + minutes * 60
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setNumBreaks(numBreaks: Int){
        _sessionProperties.value.numBreaks = numBreaks
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }
    fun setBreakDuration(hours: Int, minutes: Int){
        _sessionProperties.value.durationBreak = hours * 3600 + minutes * 60
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }


}

