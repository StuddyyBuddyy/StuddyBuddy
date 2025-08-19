package appdev.studybuddy.controller

import androidx.compose.foundation.layout.FlowRow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackBarEvent(
    val message: String,
    val action: SnackBarAction? = null
)

data class SnackBarAction(
    val name: String,
    val action: () -> Unit
)

object SnackBarController {

    private val _events = MutableSharedFlow<SnackBarEvent>()
    val events = _events.asSharedFlow()

    suspend fun sendEvent(event: SnackBarEvent){
        _events.emit(event)
    }
}