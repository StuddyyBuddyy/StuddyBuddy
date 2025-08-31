package appdev.studybuddy.controller

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @property message text to be displayed
 * @property action optional SnackBarAction for Buttons in Snackbar
 */
data class SnackBarEvent(
    val message: String,
    val action: SnackBarAction? = null
)

/**
 * @property name text of Action/Button
 * @property action actual action to be executed
 */
data class SnackBarAction(
    val name: String,
    val action: () -> Unit
)

/**
 * Snack bar controller object which handles a Flow of SnackbarEvents()
 *
 * @constructor Create empty Snack bar controller
 */
object SnackBarController {

    private val _events = MutableSharedFlow<SnackBarEvent>()
    val events = _events.asSharedFlow()

    suspend fun sendEvent(event: SnackBarEvent){
        _events.emit(event)
    }
}