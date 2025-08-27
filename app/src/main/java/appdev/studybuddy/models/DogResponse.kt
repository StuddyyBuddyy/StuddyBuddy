package appdev.studybuddy.models

import kotlinx.serialization.Serializable

/**
 * the type of onject returned by the Dog API
 */
@Serializable
data class DogResponse(
    val message: String,
    val status: String
)
