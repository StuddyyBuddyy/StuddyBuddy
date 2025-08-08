package appdev.studybuddy.models

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class SensorRepository(
    private val context: Context
): SensorEventListener  {

    private lateinit var sensorManager: SensorManager

    private var microphoneSensor: MediaRecorder? = null
    var brightnessSensor: Sensor? = null
    var vibrationSensor: Sensor? = null

    lateinit var soundTacking: File

    private val _lightLevel = MutableStateFlow(0.0)
    val lightLevel: StateFlow<Double> = _lightLevel

    private val _soundAmplitude = MutableStateFlow<Int?>(0)
    val soundAmplitude: StateFlow<Int?> = _soundAmplitude

    private val _moving = MutableStateFlow<Int?>(0)
    val moving: StateFlow<Int?> = _moving

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        brightnessSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    suspend fun recordSound(){
        while (microphoneSensor!=null) {
            delay(1000)
            _soundAmplitude.value = microphoneSensor?.maxAmplitude
        }
    }

    fun registerSoundSensor(){
        if (microphoneSensor == null) {
            soundTacking = File.createTempFile("tempSoundTracking", ".3gp", context.cacheDir)

            microphoneSensor = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(soundTacking.absolutePath)
            }

            try {
                microphoneSensor?.prepare()
                microphoneSensor?.start()
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

    fun unregisterSoundSensor() {
        microphoneSensor?.apply {
            stop()
            release()
        }
        soundTacking.delete()
        microphoneSensor = null
    }

    fun unregisterBrightnessSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) { //Brightness Sensor Event

            Log.d("SENSOR","Light: ${event.values[0]}")
        }

        //todo VIbration sensor events.
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Not used
    }

}