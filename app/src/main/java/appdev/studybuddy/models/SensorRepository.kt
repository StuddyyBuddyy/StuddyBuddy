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

class SensorRepository(
    private val context: Context
): SensorEventListener  {

    private lateinit var sensorManager: SensorManager

    private var mediaRecorder: MediaRecorder? = null

    var brightnessSensor: Sensor? = null
    var soundSensor: Sensor? = null
    var vibrationSensor: Sensor? = null

    private val _lightLevel = MutableStateFlow(0.0)
    val lightLevel: StateFlow<Double> = _lightLevel

    private val _amplitude = MutableStateFlow<Int?>(0)
    val amplitude: StateFlow<Int?> = _amplitude

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        brightnessSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    suspend fun recordSound(){
        while (mediaRecorder!=null) {
            delay(1000)
            _amplitude.value = mediaRecorder?.maxAmplitude
            Log.d("SENSOR","Sound: ${_amplitude.value}")
        }
    }

    fun getAmplitude():Int?{
        return mediaRecorder?.maxAmplitude
    }

    fun registerBrightnessSensor() {
        brightnessSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun registerSoundSensor(){
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
        }
    }

    fun unregisterSoundSensor() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun unregisterBrightnessSensor() {
        sensorManager.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {

            Log.d("SENSOR","Light: ${event.values[0]}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}