package appdev.studybuddy.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.User
import appdev.studybuddy.persistency.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class UserVM @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    val dao = DAO()
    var currentUser by mutableStateOf<User?>(null)

    init {
        viewModelScope.launch {
            combine(
                userPreferences.baseEmail,
                userPreferences.baseUsername,
                userPreferences.basePassword
            ) { email, username, password ->
                if (email.isNotBlank() && password.isNotBlank() && username.isNotBlank()) {
                    User(username, email, password)
                } else null
            }.first().let { user ->
                currentUser = user
            }
        }
    }


    fun login(email: String, password: String): Boolean {
        val userNullable: User?
        runBlocking {
            userNullable = dao.getUserByEmail(email)
        }
        if (userNullable == null) {
            return false
        }
        val user: User = userNullable

        if (user.password == password) {
            currentUser = user
            Log.d("Login", "launch VMScope")
            viewModelScope.launch {
                userPreferences.saveLastUser(user)
            }
            Log.d("Login", "successful")
            return true
        } else {
            Log.d("Login", "${user.password} != $password")
            return false
        }
    }

    fun register(email: String, password: String, username: String): Boolean {
        val user = User(username, email, password)
        var success: Boolean
        runBlocking {
            success = dao.insertUser(user)
        }

        if (success) {
            success = login(user.email, user.password)
        }

        return success
    }

    fun autoLogin(): Boolean {
        return currentUser != null && login(currentUser!!.email, currentUser!!.password)
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearLastUser()
        }
        currentUser = null
    }
}