package appdev.studybuddy.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import appdev.studybuddy.models.DAO
import appdev.studybuddy.models.Session
import appdev.studybuddy.models.User

class DataVM : ViewModel() {
    val dao = DAO()
    val userPoints = mutableMapOf<String, Int>()

    /**
     * Calculates the total points of all users and returns them in descending order.
     *
     * This method loads all users from the database, sums up their session points
     * (via [addSessionPoints]), stores them in [userPoints], and finally returns
     * a map sorted by points in descending order.
     *
     * @return A map where the keys are usernames and the values are their total points,
     *         sorted in descending order by points.
     */
    suspend fun sortUsersByPoints(): Map<String, Int>{
            for (user in dao.getAllUsers()) {
                val points = addSessionPoints(user)
                userPoints[user.username] = points
        }
        Log.d("UserPoints", userPoints.toString())
        return userPoints.toList().sortedByDescending { (_, value) -> value }.toMap()
    }

    /**
     * Calculates the total points of a specific user.
     *
     * This method sums up all points from the sessions of the given user,
     * which are loaded from the database via [dao.getUserSessions].
     *
     * @param user The user whose session points should be summed up.
     * @return The total sum of all points of the user.
     */
    suspend fun addSessionPoints(user: User): Int{
        var totalPoints = 0
        val sessions = dao.getUserSessions(user.email)
        for (session in sessions){
            totalPoints += session.points
        }
        return totalPoints
    }

    /**
     * Sorts the sessions of a user by their points.
     *
     * This method loads all sessions of a given user from the database
     * and returns them sorted so that the session with the highest points
     * appears first.
     *
     * @param user The user whose sessions should be sorted.
     * @return A list of the user's sessions, sorted in descending order by points.
     */
    suspend fun sortSessionsByPoints(user: User): List<Session> {
        val sessions = dao.getUserSessions(user.email)
        return sessions.sortedByDescending { it.points }
    }
}