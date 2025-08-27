package appdev.studybuddy.models

import kotlinx.serialization.Serializable

/**
 * the object representing the User in the DB
 */
@Serializable
data class User(
    val username: String,
    val email: String,
    val password: String
)