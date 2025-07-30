package appdev.studybuddy.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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

    private var _isInvalidBreak = MutableStateFlow<Boolean>(false)
    val isInvalidBreak: StateFlow<Boolean> = _isInvalidBreak

    init {
        viewModelScope.launch {
            userPreferences.lastSessionProperties.collect { it -> _sessionProperties.value = it }
        }
    }

    fun getHours(): Int{
        return sessionProperties.value.duration / 3600
    }

    fun getMinutes(): Int{
        return (sessionProperties.value.duration % 3600) / 60
    }

    fun getBreakHours(): Int{
        return sessionProperties.value.durationBreak / 3600
    }

    fun getBreakMinutes(): Int{
        return (sessionProperties.value.durationBreak % 3600) / 60
    }

    fun setUseMicrophoneSensor(useMicrophoneSensor: Boolean){
        _sessionProperties.value = _sessionProperties.value.copy(useMicrophoneSensor = useMicrophoneSensor)
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setUseVibrationSensor(useVibrationSensor: Boolean){
        _sessionProperties.value = _sessionProperties.value.copy(useVibrationSensor = useVibrationSensor)
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setUseBrightnessSensor(useBrightnessSensor: Boolean){
        _sessionProperties.value = _sessionProperties.value.copy(useBrightnessSensor = useBrightnessSensor)
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value) }
    }

    fun setDuration(hours: Int, minutes: Int){
        _sessionProperties.value = _sessionProperties.value.copy(duration = (hours * 3600) + (minutes * 60))
        viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
    }

    fun setNumBreaks(numBreaks: Int){
        if(sessionProperties.value.duration<=numBreaks*sessionProperties.value.durationBreak){
            _isInvalidBreak.value = true
        }else {
            _isInvalidBreak.value = false
            _sessionProperties.value = _sessionProperties.value.copy(numBreaks = numBreaks)
            viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value) }
        }
    }

    fun setBreakDuration(hours: Int, minutes: Int) {
        if (sessionProperties.value.duration <= sessionProperties.value.numBreaks*(hours * 3600 + minutes * 60)) {
            _isInvalidBreak.value = true
        } else {
            _isInvalidBreak.value = false
            _sessionProperties.value = _sessionProperties.value.copy(durationBreak = (hours * 3600) + (minutes * 60))
            viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value) }
        }
    }
}

