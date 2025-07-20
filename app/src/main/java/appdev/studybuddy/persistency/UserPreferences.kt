package appdev.studybuddy.persistency

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class UserPreferences(private val context: Context){

    companion object{
        val BASE_EMAIL = stringPreferencesKey("base_email")
        val BASE_PASSWORD = stringPreferencesKey("base_password")

        val LAST_SESSION_DURATION = intPreferencesKey("last_session_duration")
        val LAST_USE_VIBRATIONSENSOR = booleanPreferencesKey("last_use_vibrationsensor")
        val LAST_USE_MICROPHONESENSOR = booleanPreferencesKey("last_use_microphonesensor")
        val LAST_USE_BRIGHTNESSSENSOR = booleanPreferencesKey("last_use_brightnesssensor")
    }

    val baseEmail: Flow<String> = context.dataStore.data.map { preferences -> preferences[BASE_EMAIL] ?: ""}
    val basePassword: Flow<String> = context.dataStore.data.map { preferences -> preferences[BASE_PASSWORD] ?: ""}

    val lastSessionDuration: Flow<Int> = context.dataStore.data.map { preferences -> preferences[LAST_SESSION_DURATION] ?: 120}
    val lastUseVibrationSensor: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[LAST_USE_VIBRATIONSENSOR] ?: false}
    val lastUseMicrophoneSensor: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[LAST_USE_MICROPHONESENSOR] ?: false}
    val lastUseBrightnessSensor: Flow<Boolean> = context.dataStore.data.map { preferences -> preferences[LAST_USE_BRIGHTNESSSENSOR] ?: false}

    suspend fun saveBaseEmail(email: String){ context.dataStore.edit { preferences -> preferences[BASE_EMAIL] = email } }
    suspend fun saveBasePassword(password: String){ context.dataStore.edit { preferences -> preferences[BASE_PASSWORD] = password } }

    suspend fun saveLastSessionDuration(lastSessionDuration: Int){ context.dataStore.edit { preferences -> preferences[LAST_SESSION_DURATION] = lastSessionDuration } }

    suspend fun saveLastUseVibrationSensor(lastUseVibrationSensor: Boolean){
        context.dataStore.edit { preferences -> preferences[LAST_USE_VIBRATIONSENSOR] = lastUseVibrationSensor }
    }
    suspend fun saveLastUseMicrophoneSensor(lastUseMicrophoneSensor: Boolean){
        context.dataStore.edit { preferences -> preferences[LAST_USE_MICROPHONESENSOR] = lastUseMicrophoneSensor }
    }
    suspend fun saveLastUseBrightnessSensor(lastUseBrightnessSensor: Boolean){
        context.dataStore.edit { preferences -> preferences[LAST_USE_BRIGHTNESSSENSOR] = lastUseBrightnessSensor }
    }




}
