package appdev.studybuddy.viewModels

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.models.SessionProperties
import appdev.studybuddy.models.User
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.DogResponse
import appdev.studybuddy.models.Session
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class SessionVM @Inject  constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var _sessionProperties = MutableStateFlow<SessionProperties>(SessionProperties())
    val sessionProperties: StateFlow<SessionProperties> = _sessionProperties

    private var _isInvalidBreak = MutableStateFlow<Boolean>(false)
    val isInvalidBreak: StateFlow<Boolean> = _isInvalidBreak

    private var _elapsedSeconds = MutableStateFlow<Int>(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    var interrupt : Boolean = false


    lateinit var user : User
    val dao = DAO()

    init {
        viewModelScope.launch {
            userPreferences.lastSessionProperties.collect { it -> _sessionProperties.value = it }
        }
    }

    suspend fun startTimer(){
        interrupt = false
        while (elapsedSeconds.value < sessionProperties.value.duration  && !interrupt) {
            delay(1000)
            _elapsedSeconds.value++
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

    fun setElapsedSeconds(elapsedSeconds: Int){
        _elapsedSeconds.value = elapsedSeconds
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
        if((hours*3600+ minutes*60)<=sessionProperties.value.numBreaks*sessionProperties.value.durationBreak){
            _isInvalidBreak.value = true
        }else {
            _isInvalidBreak.value = false
            _sessionProperties.value = _sessionProperties.value.copy(duration = (hours * 3600) + (minutes * 60))
            viewModelScope.launch { userPreferences.saveSessionProperties(sessionProperties.value)}
        }
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
    fun endSession(fail : Boolean = false) : Boolean{
        val points = calculatePoints(fail)
        val session = createCompanionObject(points)

        var successful : Boolean
        runBlocking{
            successful = dao.insertSession(session)
        }

        if (successful){
            interrupt = true
            _elapsedSeconds.value = 0
        }

        return successful
    }

    @SuppressLint("SimpleDateFormat")
    fun createCompanionObject(points : Int) : Session {

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        val current = formatter.format(date)

        return Session(
            userEmail = user.email,
            date = current,
            duration = sessionProperties.value.duration,
            points = points,
        )
    }

    fun calculatePoints(fail: Boolean) : Int {
        var points = sessionProperties.value.duration
        if (fail)
            return -points / 2

        if (sessionProperties.value.useMicrophoneSensor) points += 5
        if (sessionProperties.value.useVibrationSensor) points += 5
        if (sessionProperties.value.useBrightnessSensor) points += 5

        return points
    }

    //-----------Dog API--------------
    private val _dogImageUrl = MutableStateFlow<String?>(null)
    val dogImageUrl : StateFlow<String?> = _dogImageUrl

    val apiKey = ""

    fun fetchDogImage() {
        viewModelScope.launch {
            try {
                val response: DogResponse = dao.dogClient.get("https://dog.ceo/api/breeds/image/random") {
                    headers {
                        append("Authorization", "Bearer $apiKey")
                    }
                }.body()
                _dogImageUrl.value = response.message
            } catch (e: Exception) {
                _dogImageUrl.value = null
            }
        }
    }

    suspend fun loadBitmapFromUrl(url: String): ImageBitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = dao.dogClient.get(url)
                val byteArray = response.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
}

