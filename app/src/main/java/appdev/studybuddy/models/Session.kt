package appdev.studybuddy.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: Int? = null,
    val userEmail: String,
    val date: String, // store date as ISO String (e.g. "2025-07-11")
    val duration: Int,
    val points: Int,
    val description: String? = null
)