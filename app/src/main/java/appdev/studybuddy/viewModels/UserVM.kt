package appdev.studybuddy.viewModels

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
import org.mindrot.jbcrypt.BCrypt

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


    fun login(email: String, password: String, bypassPassword: Boolean = false): Boolean {
        val userNullable: User?
        runBlocking {
            userNullable = dao.getUserByEmail(email)
        }
        if (userNullable == null) {
            return false
        }
        val user: User = userNullable

        if (bypassPassword || verifyPassword(password, user.password)) {
            currentUser = user
            viewModelScope.launch {
                userPreferences.saveLastUser(user)
            }
            return true
        } else {
            return false
        }
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun register(email: String, password: String, username: String): Boolean {
        val user = User(username, email, hashPassword(password))
        var success: Boolean
        runBlocking {
            success = dao.insertUser(user)
        }

        if (success) {
            success = login(email, password)
        }

        return success
    }

    fun autoLogin(): Boolean {
        return currentUser != null && login(currentUser!!.email, "", bypassPassword = true)
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearLastUser()
        }
        currentUser = null
    }
}