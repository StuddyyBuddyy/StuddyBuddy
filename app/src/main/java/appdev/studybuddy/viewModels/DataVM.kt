package appdev.studybuddy.viewModels

import androidx.lifecycle.ViewModel
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.Session
import appdev.studybuddy.models.User

class DataVM : ViewModel() {
    val dao = DAO()
    val userPoints = mutableMapOf<String, Int>()

    suspend fun getAllUsers(): List<User> = dao.getAllUsers()
    suspend fun getUserSessions(email: String) : List<Session> = dao.getUserSessions(email)

    suspend fun sortUsersByPoints(): Map<String, Int>{
        for (user in getAllUsers()){
            var points = addSessionPoints(user)
            userPoints[user.username] = points
        }
        return userPoints.toList().sortedByDescending { (_, value) -> value }.toMap()
    }

    suspend fun addSessionPoints(user: User): Int{
        var totalPoints = 0
        val sessions = getUserSessions(user.email)
        for (session in sessions){
            totalPoints += session.points
        }
        return totalPoints
    }


}