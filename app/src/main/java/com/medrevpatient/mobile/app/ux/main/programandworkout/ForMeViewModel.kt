package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.medrevpatient.mobile.app.data.source.remote.dto.ForMe
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.DATA_ONE
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH_FOR_ME
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.ext.fromJsonString
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ForMeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: ApiRepository,
    saveStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _forMe: MutableStateFlow<ForMeUiState> = MutableStateFlow(ForMeUiState())
    val forMe: StateFlow<ForMeUiState> = _forMe.asStateFlow()

    val notes = repository.getNotes().cachedIn(viewModelScope) //All Notes Screen

    private val noteStateHandle: String? = saveStateHandle[DATA_ONE]

    private var oldNoteContent = ""

    init {
        noteStateHandle?.fromJsonString<Note>()?.apply {
            oldNoteContent = title + body
            _forMe.update { it.copy(note = this, title = title, body = body) }
        } ?: fetchForMe()  // Initial fetch
    }

    fun event(event: ForMeUiEvent) {
        when (event) {
            is ForMeUiEvent.NavigateTo -> navigate(event.navRoute)
            is ForMeUiEvent.Notes.NavigateTo -> {
                navigate(event.navRoute)
            }

            ForMeUiEvent.Notes.Delete -> {
                deleteNote(forMe.value.note.id)
            }

            is ForMeUiEvent.Notes.EditNote -> {
                _forMe.update {
                    it.copy(
                        title = event.title,
                        body = event.body,
                    )
                }
                _forMe.update {
                    it.copy(isEnable = shouldEnable())
                }
            }

            ForMeUiEvent.Notes.Save -> {
                oldNoteContent.isNotEmpty().ifTrue {
                    updateNote(
                        noteId = forMe.value.note.id,
                        title = forMe.value.title,
                        body = forMe.value.body
                    )
                    return
                }
                createNote(
                    title = forMe.value.title,
                    body = forMe.value.body
                )
            }

            ForMeUiEvent.Notes.Cancel -> {
                (forMe.value.isLoading.not()).ifTrue {
                    popBackStack()
                }
            }
        }
    }

    fun fetchForMe() {
        viewModelScope.launch {
            repository.getForMe()
                .collect { result ->
                    _forMe.update { it.copy(getForMe = result) }
                }
        }
    }

    private fun createNote(
        title: String,
        body: String
    ) {

        val note = JsonObject().apply {
            addProperty("title", title)
            addProperty("body", body)
        }

        viewModelScope.launch {
            repository.createNote(note).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "something went wrong!")
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                        }

                        is NetworkResult.Loading -> {
                            _forMe.update {
                                it.copy(isLoading = true, isEnable = false)
                            }
                        }

                        is NetworkResult.Success -> {
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                            popBackStackWithResult(
                                resultValues = listOf(
                                    PopResultKeyValue(
                                        key = REFRESH,
                                        value = true
                                    ),
                                    PopResultKeyValue(
                                        key = REFRESH_FOR_ME,
                                        value = true
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }


    private fun updateNote(
        noteId: String,
        title: String,
        body: String
    ) {

        val note = JsonObject().apply {
            addProperty("title", title)
            addProperty("body", body)
        }

        viewModelScope.launch {
            repository.updateNote(noteId, note).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "something went wrong!")
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                        }

                        is NetworkResult.Loading -> {
                            _forMe.update {
                                it.copy(isLoading = true, isEnable = false)
                            }
                        }

                        is NetworkResult.Success -> {
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                            popBackStackWithResult(
                                resultValues = listOf(
                                    PopResultKeyValue(
                                        key = REFRESH,
                                        value = true
                                    ),
                                    PopResultKeyValue(
                                        key = REFRESH_FOR_ME,
                                        value = true
                                    ),
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteNote(
        noteId: String
    ) {
        viewModelScope.launch {
            repository.deleteNote(noteId).collect { networkResult ->
                withContext(Dispatchers.Main) {
                    when (networkResult) {
                        is NetworkResult.Error -> {
                            Toast(context, networkResult.message ?: "something went wrong!")
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                        }

                        is NetworkResult.Loading -> {
                            _forMe.update {
                                it.copy(isLoading = true, isEnable = false)
                            }
                        }

                        is NetworkResult.Success -> {
                            _forMe.update {
                                it.copy(isLoading = false, isEnable = true)
                            }
                            popBackStackWithResult(
                                resultValues = listOf(
                                    PopResultKeyValue(
                                        key = REFRESH,
                                        value = true
                                    ),
                                    PopResultKeyValue(
                                        key = REFRESH_FOR_ME,
                                        value = true
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun shouldEnable(): Boolean {
        val areContentSame = forMe.value.title + forMe.value.body == oldNoteContent
        val areValuesNotEmpty = forMe.value.title.isNotEmpty() || forMe.value.body.isNotEmpty()
        return !areContentSame && areValuesNotEmpty
    }
}

typealias ForMeEvent = (ForMeUiEvent) -> Unit

sealed interface ForMeUiEvent {
    data class NavigateTo(val navRoute: NavRoute) : ForMeUiEvent
    object Notes {
        data class NavigateTo(val navRoute: NavRoute) : ForMeUiEvent
        data class EditNote(val title: String, val body: String) : ForMeUiEvent
        object Save : ForMeUiEvent
        object Delete : ForMeUiEvent
        object Cancel : ForMeUiEvent
    }
}


//It's combine state of ForMe Screen and Notes Flow
data class ForMeUiState(
    val isLoading: Boolean = false,
    val isEnable: Boolean = false,
    val getForMe: NetworkResult<ApiResponse<ForMe>> = NetworkResult.Loading(),
    val notes: PagingData<Note> = PagingData.empty(),
    val note: Note = Note(),
    val title: String = "",
    val body: String = "", //we can use merge this and title in note data class.
)


