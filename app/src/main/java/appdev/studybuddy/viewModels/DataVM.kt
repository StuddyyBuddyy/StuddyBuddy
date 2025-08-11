package appdev.studybuddy.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.User

class DataVM : ViewModel() {
    val dao = DAO()
    val userPoints = mutableMapOf<String, Int>()
    val sessionPoints = mutableMapOf<String, Int>()

    suspend fun sortUsersByPoints(): Map<String, Int>{
            for (user in dao.getAllUsers()) {
                val points = addSessionPoints(user)
                userPoints[user.username] = points
        }
        Log.d("UserPoints", userPoints.toString())
        return userPoints.toList().sortedByDescending { (_, value) -> value }.toMap()
    }

    suspend fun addSessionPoints(user: User): Int{
        var totalPoints = 0
        val sessions = dao.getUserSessions(user.email)
        for (session in sessions){
            totalPoints += session.points
        }
        return totalPoints
    }

    suspend fun sortSessionPoints(user: User): Map<String, Int>{
        for (session in dao.getUserSessions(user.email)){
            sessionPoints[session.date] = session.points
        }
        Log.d("SessionPoints", sessionPoints.toString())
        return sessionPoints.toList().sortedByDescending { (_, value) -> value }.toMap()
    }

}