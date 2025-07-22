package appdev.studybuddy.viewModels

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionVM @Inject  constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var _useMicrophoneSensor = MutableStateFlow<Boolean>(false)
    val useMicrophoneSensor: StateFlow<Boolean> = _useMicrophoneSensor

    private var _useVibrationSensor = MutableStateFlow<Boolean>(false)
    val useVibrationSensor: StateFlow<Boolean> = _useVibrationSensor

    private var _useBrightnessSensor = MutableStateFlow<Boolean>(false)
    val useBrightnessSensor: StateFlow<Boolean> = _useBrightnessSensor

    private var _duration = MutableStateFlow<Int>(120) //Duration in minutes
    val duration: StateFlow<Int> = _duration

    init {
        viewModelScope.launch {
            userPreferences.lastSessionDuration.collect { _duration.value = it }
        }
        viewModelScope.launch {
            userPreferences.lastUseMicrophoneSensor.collect { _useMicrophoneSensor.value = it }
        }
        viewModelScope.launch {
            userPreferences.lastUseVibrationSensor.collect { _useVibrationSensor.value = it }
        }
        viewModelScope.launch {
            userPreferences.lastUseBrightnessSensor.collect { _useBrightnessSensor.value = it }
        }
    }

    fun setUseMicrophoneSensor(useMicrophoneSensor: Boolean){
        _useMicrophoneSensor.value = useMicrophoneSensor
    }

    fun setUseVibrationSensor(useVibrationSensor: Boolean){
        _useVibrationSensor.value = useVibrationSensor
    }

    fun setUseBrightnessSensor(useBrightnessSensor: Boolean){
        _useBrightnessSensor.value = useBrightnessSensor
    }

}