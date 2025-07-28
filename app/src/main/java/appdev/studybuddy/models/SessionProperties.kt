package appdev.studybuddy.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionProperties(
    var useVibrationSensor: Boolean = false,
    var useMicrophoneSensor: Boolean = false,
    var useBrightnessSensor: Boolean = false,
    var duration: Int  = 3600, //duration in seconds
    var numBreaks: Int = 1,
    var durationBreak: Int = 300, //duration of a break in seconds
)
