package appdev.studybuddy.controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlin.math.sqrt

class SensorRepository(
    private val context: Context
): SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var soundSensor: MediaRecorder? = null
    var brightnessSensor: Sensor? = null
    var movementSensor: Sensor? = null

    lateinit var soundTacking: File

    private val _lightLevel = MutableStateFlow<Float>(0f)
    val lightLevel: StateFlow<Float> = _lightLevel

    private val _soundAmplitude = MutableStateFlow<Int?>(0)
    val soundAmplitude: StateFlow<Int?> = _soundAmplitude

    private val _accelerationMagnitude = MutableStateFlow<Float>(0f)
    val accelerationMagnitude: StateFlow<Float> = _accelerationMagnitude

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        brightnessSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        movementSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun registerSoundSensor(){
        if (soundSensor == null) {
            soundTacking = File.createTempFile("tempSoundTracking", ".3gp", context.cacheDir)

            soundSensor = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(soundTacking.absolutePath)
            }

            try {
                soundSensor?.prepare()
                soundSensor?.start()
            } catch (e: Exception) {
                e.printStackTrace()
                soundTacking.delete()
            }
        }
    }

    fun registerBrightnessSensor() {
        brightnessSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun registerMovementSensor() {
        movementSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterSoundSensor() {
        soundSensor?.apply {
            stop()
            release()
        }
        soundTacking.delete()
        soundSensor = null
    }

    fun unregisterBrightnessSensor() {
        sensorManager.unregisterListener(this)
    }

    fun unregisterMovementSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) { //Brightness Sensor Event
            _lightLevel.value = event.values[0]
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) { //Movement Sensor Event
            var x = event.values[0]
            var y = event.values[1]
            var z = event.values[2]

            _accelerationMagnitude.value = sqrt(x * x + y * y + z * z)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used
    }

    suspend fun recordSound(){
        while (soundSensor!=null) {
            delay(1000)
            _soundAmplitude.value = soundSensor?.maxAmplitude
        }
    }

}