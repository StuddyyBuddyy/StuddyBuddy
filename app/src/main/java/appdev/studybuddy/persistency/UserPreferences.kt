package appdev.studybuddy.persistency

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import appdev.studybuddy.models.SessionProperties
import appdev.studybuddy.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

/**
 * PreferenceDataStore to store last user login data or last session properties
 */
class UserPreferences (
    private val context: Context
){

    companion object{
        val BASE_EMAIL = stringPreferencesKey("base_email")
        val BASE_PASSWORD = stringPreferencesKey("base_password")
        val BASE_USERNAME = stringPreferencesKey("base_username")

        val LAST_SESSION_PROPERTIES = stringPreferencesKey("last_session_properties")

    }

    val baseEmail: Flow<String> = context.dataStore.data.map { preferences -> preferences[BASE_EMAIL] ?: ""}
    val basePassword: Flow<String> = context.dataStore.data.map { preferences -> preferences[BASE_PASSWORD] ?: ""}
    val baseUsername: Flow<String> = context.dataStore.data.map { preferences -> preferences[BASE_USERNAME] ?: ""}

    val lastSessionProperties: Flow<SessionProperties> = context.dataStore.data.map { preferences ->
        val json = preferences[LAST_SESSION_PROPERTIES]
        if (json != null) {
            Json.decodeFromString<SessionProperties>(SessionProperties.serializer(),json)
        } else {
            SessionProperties()
        }
    }

    suspend fun saveBaseEmail(email: String){ context.dataStore.edit { preferences -> preferences[BASE_EMAIL] = email } }
    suspend fun saveBasePassword(password: String){ context.dataStore.edit { preferences -> preferences[BASE_PASSWORD] = password } }
    suspend fun saveBaseUsername(username: String){ context.dataStore.edit { preferences -> preferences[BASE_USERNAME] = username } }

    suspend fun saveSessionProperties(properties: SessionProperties) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SESSION_PROPERTIES] = Json.encodeToString<SessionProperties>(SessionProperties.serializer(),properties)
        }
    }

    suspend fun clearLastUser(){
        saveBaseEmail("")
        saveBaseUsername("")
        saveBasePassword("")
    }

    suspend fun saveLastUser(user: User){
        saveBaseEmail(user.email)
        saveBaseUsername(user.username)
        saveBasePassword(user.password)
    }
}
