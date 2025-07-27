package appdev.studybuddy.viewModels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.Session
import appdev.studybuddy.models.User
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
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

    lateinit var user : User

    val dao = DAO()

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
        viewModelScope.launch { userPreferences.saveLastUseMicrophoneSensor(useMicrophoneSensor)}
    }

    fun setUseVibrationSensor(useVibrationSensor: Boolean){
        _useVibrationSensor.value = useVibrationSensor
        viewModelScope.launch { userPreferences.saveLastUseVibrationSensor(useVibrationSensor)}
    }

    fun setUseBrightnessSensor(useBrightnessSensor: Boolean){
        _useBrightnessSensor.value = useBrightnessSensor
        viewModelScope.launch { userPreferences.saveLastUseBrightnessSensor(useBrightnessSensor) }
    }

    fun setDuration(hours: Int, minutes: Int){
        _duration.value = hours * 60 + minutes
        viewModelScope.launch { userPreferences.saveLastSessionDuration(hours * 60 + minutes) }
    }

    fun endSession(fail : Boolean = false){
        val points = calculatePoints(fail)
        val session = createCompanionObject(points)

        var successful : Boolean
        runBlocking{
            successful = dao.insertSession(session)
        }

        //TODO Error Handling
    }

    @SuppressLint("SimpleDateFormat")
    fun createCompanionObject(points : Int) : Session {

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        val current = formatter.format(date)

        return Session(
                userEmail = user.email,
                date = current,
                duration = _duration.value,
                points = points,
            )
    }

    fun calculatePoints(fail: Boolean) : Int {
        //TODO implement
        return 0
    }

}