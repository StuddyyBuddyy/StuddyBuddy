package appdev.studybuddy.viewModels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.User
import kotlinx.coroutines.runBlocking

class UserVM : ViewModel() {
    val dao = DAO()
    var currentUser by mutableStateOf<User?>(null)

    fun login(email: String, password : String) : Boolean{
        val userNullable : User?
        runBlocking {
            userNullable = dao.getUserByEmail(email)
        }
        if(userNullable == null){
            return false
        }
        val user : User = userNullable

        if(user.password == password) {
            currentUser = user
            return true
        }else{
            return false
        }
    }

    fun register(email: String, password: String, username : String): Boolean {
        val user = User(username, email, password)
        var success : Boolean
        runBlocking {
            success = dao.insertUser(user)
        }

        if(success){
            currentUser = user
        }

        return success
    }

    fun logout(){
        currentUser = null
    }
}