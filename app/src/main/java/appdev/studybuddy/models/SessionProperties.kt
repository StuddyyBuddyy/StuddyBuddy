package appdev.studybuddy.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionProperties(
    var useMovementSensor: Boolean = false,
    var useSoundSensor: Boolean = false,
    var useBrightnessSensor: Boolean = false,
    var duration: Int  = 300, //duration in seconds
    var numBreaks: Int = 1,
    var durationBreak: Int = 60, //duration of a break in seconds
)
