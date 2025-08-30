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
     * Berechnet die Gesamtpunkte aller User und gibt sie absteigend sortiert zurück.
     *
     * Die Methode lädt alle Benutzer aus der Datenbank, summiert deren Sitzungs-Punkte
     * (über [addSessionPoints]), speichert sie in [userPoints] und gibt schließlich eine
     * nach Punkten absteigend sortierte Map zurück.
     * @return Eine Map, die die Benutzernamen als Schlüssel und die Gesamtpunkte dieses Users
     *         als Wert enthält, absteigend nach Punkten sortiert.
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
     * Berechnet die Gesamtpunkte eines bestimmten Users.
     *
     * Die Methode summiert alle Punkte aus den Sitzungen des Users,
     * die aus der Datenbank über [dao.getUserSessions] geladen werden.
     *
     * @param user Der User, dessen Sitzungs-Punkte summiert werden sollen.
     * @return Die Summe aller Punkte des Users.
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
     * Sortiert die Sitzungen eines Users nach deren Punkten.
     *
     * Die Methode lädt alle Sitzungen eines Users aus der Datenbank und
     * gibt sie sortiert zurück, sodass die Sitzung mit den meisten Punkten zuerst erscheint.
     *
     * @param user Der User, dessen Sitzungen sortiert werden sollen.
     * @return Eine Liste der Sitzungen des Users, absteigend nach Punkten sortiert.
     */
    suspend fun sortSessionsByPoints(user: User): List<Session> {
        val sessions = dao.getUserSessions(user.email)
        return sessions.sortedByDescending { it.points }
    }
}