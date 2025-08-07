package appdev.studybuddy.models

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.atan2
import kotlin.math.sqrt

class SensorRepository(
    private val context: Context
): SensorEventListener  {

    lateinit var sensorManager: SensorManager

    var brightnessSensor: Sensor? = null
    var soundSensor: Sensor? = null
    var vibrationSensor: Sensor? = null

    private val _lightLevel = MutableStateFlow(0.0)
    val lightLevel: StateFlow<Double> = _lightLevel

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        brightnessSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    fun register() {
        brightnessSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {

            event.values
            Log.d("SENSOR","${event.values}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}