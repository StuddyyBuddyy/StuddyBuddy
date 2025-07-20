package appdev.studybuddy.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionVM : ViewModel() {

    private var _useMicrophoneSensor = MutableStateFlow<Boolean>(false)
    val useMicrophoneSensor: StateFlow<Boolean> = _useMicrophoneSensor

    private var _useVibrationSensor = MutableStateFlow<Boolean>(false)
    val useVibrationSensor: StateFlow<Boolean> = _useVibrationSensor

    private var _useBrightnessSensor = MutableStateFlow<Boolean>(false)
    val useBrightnessSensor: StateFlow<Boolean> = _useBrightnessSensor

    private var _duration = MutableStateFlow<Int>(120) //Duration in minutes
    val duration: StateFlow<Int> = _duration

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