package appdev.studybuddy.models

import kotlinx.serialization.Serializable

@Serializable
data class DogResponse(
    val message: String,
    val status: String
)
