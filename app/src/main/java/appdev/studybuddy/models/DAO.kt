package appdev.studybuddy.models
import appdev.studybuddy.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * the DAO for the Database and the Dog Image APi
 */
class DAO {

    //-------------DB-------------

    //the Http Client for accessing the Database
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    val url = BuildConfig.SERVER_URL

    /**
     * GETs all Users in the DB
     */
    suspend fun getAllUsers(): List<User> {
        return try {
            client.get("$url/users").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * GETs User with the given email
     */
    suspend fun getUserByEmail(email: String): User? {
        val user =
            try {
            client.get("$url/userByEmail") {
                url {
                    parameters.append("email", email)
                }
            }.body<User>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return user
    }

    /**
     * GETs all Sessions associated with the User identified by email
     */
    suspend fun getUserSessions(email: String): List<Session> {
        return try {
            client.get("$url/sessions") {
                url {
                    parameters.append("email", email)
                }
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    /**
     * sends POST for new inserting given USer
     */
    suspend fun insertUser(user: User): Boolean {
        return try {
            val response = client.post("$url/users") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * sends POST for inserting given Session
     */
    suspend fun insertSession(session: Session): Boolean {
        return try {
            val response = client.post("$url/sessions") {
                contentType(ContentType.Application.Json)
                setBody(session)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ----------- Dog API --------------

    //the Http Client for accessing the Database
    val dogClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

}