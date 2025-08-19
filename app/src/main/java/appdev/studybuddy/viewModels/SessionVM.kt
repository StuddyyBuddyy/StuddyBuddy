package appdev.studybuddy.viewModels

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.BuildConfig
import appdev.studybuddy.models.SessionProperties
import appdev.studybuddy.models.User
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.DogResponse
import appdev.studybuddy.controller.SensorRepository
import appdev.studybuddy.models.Session
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.math.absoluteValue
import android.media.MediaPlayer
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.core.content.ContextCompat.getSystemService
import appdev.studybuddy.R
import appdev.studybuddy.controller.SnackBarController
import appdev.studybuddy.controller.SnackBarEvent
import dagger.hilt.android.qualifiers.ApplicationContext


@HiltViewModel
class SessionVM @Inject  constructor(
    private val userPreferences: UserPreferences,
    private val sensorRepository: SensorRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val BRIGHTNESS_THRESHOLD = 10
    private val SOUND_THRESHOLD = 750
    private val MOVEMENT_TRHESOLD = 1f //Grenzwert wenn Handy mehr als x bewegt wird/ bzw. von BASE_ACCELERATION abweicht
    private val BASE_ACCELERATION = 9.81f
    val MOVEMENT_LIMIT = 2

    private var _sessionProperties = MutableStateFlow<SessionProperties>(SessionProperties())
    val sessionProperties: StateFlow<SessionProperties> = _sessionProperties

    private var _isInvalidBreak = MutableStateFlow<Boolean>(false) //Indikator ob Pausen valid sind im Verhältnis zur Session-Dauer
    val isInvalidBreak: StateFlow<Boolean> = _isInvalidBreak

    private var _overallElapsedSeconds = MutableStateFlow<Int>(0)
    val overallElapsedSeconds: StateFlow<Int> = _overallElapsedSeconds

    private var _segmentElapsedSeconds = MutableStateFlow<Int>(0)
    val segmentElapsedSeconds: StateFlow<Int> = _segmentElapsedSeconds

    var efficientTime: Int = 0
    var sessionTimeSegment: Int = 0

    private var _isBreak = MutableStateFlow<Boolean>(false)
    val isBreak: StateFlow<Boolean> = _isBreak

    private var _isTooDark = MutableStateFlow<Boolean>(false)
    val isTooDark : StateFlow<Boolean> = _isTooDark

    private var _isTooLoud = MutableStateFlow<Boolean>(false)
    val isTooLoud : StateFlow<Boolean> =  _isTooLoud

    private var _wasMobileMoved = MutableStateFlow<Int>(0)
    val wasMobileMoved : StateFlow<Int> = _wasMobileMoved

    //For Debug:
    private var _lightLevel = MutableStateFlow<Float>(0f)
    val lightLevel : StateFlow<Float> = _lightLevel
    private var _soundAmplitude = MutableStateFlow<Int?>(0)
    val soundAmplitude : StateFlow<Int?> = _soundAmplitude
    private var _movementMagnitude = MutableStateFlow<Float>(0f)
    val movementMagnitude : StateFlow<Float> = _movementMagnitude

    var sessionDescription : String = ""

    var interrupt : Boolean = false

    lateinit var user : User
    val dao = DAO()

    init {
        viewModelScope.launch {
            userPreferences.lastSessionProperties.collect { it -> _sessionProperties.value = it }
        }

        viewModelScope.launch {
            combine(
                sensorRepository.lightLevel,
                sensorRepository.soundAmplitude,
            ) { lightLevel, soundAmplitude ->

                _lightLevel.value = lightLevel //for Debug
                _soundAmplitude.value = soundAmplitude

                _isTooDark.value = lightLevel < BRIGHTNESS_THRESHOLD
                soundAmplitude?.let { _isTooLoud.value = it > SOUND_THRESHOLD }

            }.collect()
        }

        viewModelScope.launch {
            sensorRepository.accelerationMagnitude.collect { it ->
                _movementMagnitude.value = it

                val accelerationChange = (it - BASE_ACCELERATION).absoluteValue
                if ((accelerationChange > MOVEMENT_TRHESOLD) && !isBreak.value) {
                    _wasMobileMoved.value++
                    if(wasMobileMoved.value<MOVEMENT_LIMIT){
                        SnackBarController.sendEvent(
                            SnackBarEvent(
                                message = "If you move your phone ${MOVEMENT_LIMIT-wasMobileMoved.value} more time(s), the session will be terminated!"
                            )
                        )
                    }
                    delay(3000)
                }

            }
        }
    }

    /**
     * Starte den Timer im SessionScreen. Trackt dabei Pausen (wechselt isBreak)
     */
    suspend fun startTimer(){
        interrupt = false
        _isBreak.value = false

        efficientTime = if (sessionProperties.value.numBreaks>0) sessionProperties.value.duration-(sessionProperties.value.numBreaks*sessionProperties.value.durationBreak) else sessionProperties.value.duration
        sessionTimeSegment = efficientTime/(sessionProperties.value.numBreaks+1)

        while (overallElapsedSeconds.value < sessionProperties.value.duration  && !interrupt) {
            delay(1000)
            _overallElapsedSeconds.value++

            if (!_isBreak.value) { //keine Pause
                _segmentElapsedSeconds.value++

                if (_segmentElapsedSeconds.value >= sessionTimeSegment) {
                    notifyBreak()
                    _isBreak.value = true
                    _segmentElapsedSeconds.value = 0
                }

            } else { //in einer Pause
                _segmentElapsedSeconds.value++

                if (_segmentElapsedSeconds.value >= sessionProperties.value.durationBreak) {
                    _isBreak.value = false
                    _segmentElapsedSeconds.value = 0
                }
            }

        }
    }

    /**
     * Session beenden und in Datenbank speichern
     */
    fun endSession(fail : Boolean = false) : Boolean{
        val points = calculatePoints(fail)
        val session = createCompanionObject(points)

        var successful : Boolean
        runBlocking{
            successful = dao.insertSession(session)
        }

        if (successful){
            interrupt = true
        }

        _overallElapsedSeconds.value = 0
        _segmentElapsedSeconds.value = 0
        _isTooDark.value = false
        _isTooLoud.value = false
        _wasMobileMoved.value = 0

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
            description = sessionDescription
        )
    }

    fun calculatePoints(fail: Boolean) : Int {
        var points = sessionProperties.value.duration
        if (fail)
            return -points / 2

        if (sessionProperties.value.useSoundSensor) points += 5
        if (sessionProperties.value.useMovementSensor) points += 5
        if (sessionProperties.value.useBrightnessSensor) points += 5

        return points
    }

    //-----------Dog API--------------
    private val _dogImageUrl = MutableStateFlow<String?>(null)
    val dogImageUrl : StateFlow<String?> = _dogImageUrl

    val apiKey = BuildConfig.DOG_API_KEY


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

    fun saveImageToGallery(
        context: Context,
        image: ImageBitmap,
        fileName: String
    ): Boolean {
        val bitmap: Bitmap = image.asAndroidBitmap()

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DogImages")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        return try {
            val success = resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            } ?: false

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            success
        } catch (e: Exception) {
            false
        }
    }

    //-----------Sensors --------------
    fun onResume(){
        if(sessionProperties.value.useSoundSensor){
            sensorRepository.registerSoundSensor(sessionProperties.value.useSoundSensor)
            viewModelScope.launch {
                while(true){
                    delay(1000)
                    sensorRepository.recordSound()
                }
            }
        }

        if(sessionProperties.value.useBrightnessSensor){
            sensorRepository.registerBrightnessSensor()
        }

        if(sessionProperties.value.useMovementSensor){
            sensorRepository.registerMovementSensor()
        }
    }

    fun onPause(){
        sensorRepository.unregisterSoundSensor()
        sensorRepository.unregisterBrightnessSensor()
        sensorRepository.unregisterMovementSensor()
    }

    //-----------Getter & Setter --------------
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
        _overallElapsedSeconds.value = elapsedSeconds
    }

    fun setUseSoundSensor(useSoundSensor: Boolean){
        viewModelScope.launch {
            userPreferences.saveSessionProperties(
                _sessionProperties.value.copy(useSoundSensor = useSoundSensor)
            )
        }
    }

    fun setUseMovementSensor(useMovementSensor: Boolean){
        viewModelScope.launch {
            userPreferences.saveSessionProperties(
                _sessionProperties.value.copy(useMovementSensor = useMovementSensor)
            )
        }
    }

    fun setUseBrightnessSensor(useBrightnessSensor: Boolean){
        viewModelScope.launch {
            userPreferences.saveSessionProperties(
                _sessionProperties.value.copy(useBrightnessSensor = useBrightnessSensor)
            )
        }
    }

    fun setDuration(hours: Int, minutes: Int){
        if((hours*3600+ minutes*60)/2<=sessionProperties.value.numBreaks*sessionProperties.value.durationBreak){
            _isInvalidBreak.value = true
        }else {
            _isInvalidBreak.value = false
            viewModelScope.launch {
                userPreferences.saveSessionProperties(
                    _sessionProperties.value.copy(duration = (hours * 3600) + (minutes * 60))
                )
            }
        }
    }

    fun setNumBreaks(numBreaks: String){

        if(numBreaks.isEmpty()||sessionProperties.value.duration/2<=numBreaks.toInt()*sessionProperties.value.durationBreak || numBreaks.isEmpty()){
            _isInvalidBreak.value = true
        }else {
            _isInvalidBreak.value = false
            viewModelScope.launch {
                userPreferences.saveSessionProperties(
                    _sessionProperties.value.copy(numBreaks = numBreaks.toInt())
                )
            }
        }
    }

    fun setBreakDuration(hours: Int, minutes: Int) {
        if (sessionProperties.value.duration/2 <= sessionProperties.value.numBreaks*(hours * 3600 + minutes * 60)) {
            _isInvalidBreak.value = true
        } else {
            _isInvalidBreak.value = false
            viewModelScope.launch {
                userPreferences.saveSessionProperties(
                    _sessionProperties.value.copy(durationBreak = (hours * 3600) + (minutes * 60))
                )
            }
        }
    }


    /**
     * Sound wenn eine Pause beginnt.
     */
    fun notifyBreak(){
        val mediaPlayer = MediaPlayer.create(context, R.raw.simple_breaksound)
        mediaPlayer.start()
    }

    /**
     * Alarm Vibration + Sound wenn eine Session abgebrochen wird
     */
    fun alarm() {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        val timings = longArrayOf(0, 500, 300, 500, 300, 500, 300, 500, 300)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1)) // -1 = no repeat

        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer.start()
    }

}

