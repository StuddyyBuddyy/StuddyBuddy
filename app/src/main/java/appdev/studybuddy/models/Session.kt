package appdev.studybuddy.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: Int? = null,
    val userEmail: String,
    val date: String, // "yyyy-MM-dd"
    val duration: Int,
    val points: Int,
    val description: String? = null
)