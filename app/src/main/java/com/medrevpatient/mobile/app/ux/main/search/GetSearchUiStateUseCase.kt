package com.medrevpatient.mobile.app.ux.main.search
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.main.griotLegacy.GriotLegacyRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSearchUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,

) {
    private val searchUiDataFlow = MutableStateFlow(SearchUiDataState())
    private val allPeopleListFlow =
        MutableStateFlow<PagingData<SearchPeopleResponse>>(PagingData.empty())
    private var searchJob: Job? = null
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SearchUiState {
        getAllSearchDataList(
            coroutineScope = coroutineScope,
            searchName = searchUiDataFlow.value.search
        )
        return SearchUiState(
            searchUiDataFlow = searchUiDataFlow,
            allPeopleListFlow = allPeopleListFlow,
            event = { archiveUiEvent ->
                archiveUiEvent(
                    context = context,
                    event = archiveUiEvent,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun archiveUiEvent(
        context: Context,
        event: SearchUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is SearchUiEvent.SearchValueChange -> {
                // Update the search value in the state
                searchUiDataFlow.value = searchUiDataFlow.value.copy(
                    search = event.search
                )
                // Cancel any ongoing job
                searchJob?.cancel()

                // Start a new job with a debounce delay
                searchJob = coroutineScope.launch {
                    delay(500)
                    // Trigger API call even if the search is empty
                    getAllSearchDataList(coroutineScope = coroutineScope, searchName = event.search)
                }
            }

            SearchUiEvent.NavigateToNotification -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screen = Constants.AppScreen.NOTIFICATION_SCREEN,
                    userId = ""
                )
            }

            is SearchUiEvent.NavigateToUserProfile -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screen = Constants.AppScreen.USER_PROFILE,
                    userId = event.userId
                )
            }

            SearchUiEvent.BackClick -> {
                navigate(NavigationAction.Navigate(GriotLegacyRoute.createRoute()))
            }
        }
    }

    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screen: String,
        userId: String

        ) {
        val bundle = Bundle()
        val intent = Intent(context, ContainerActivity::class.java)
        bundle.putString(Constants.BundleKey.USER_ID, userId)
        intent.putExtra(Constants.IS_FORM, bundle)
        intent.putExtra(Constants.IS_COME_FOR, screen)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun getAllSearchDataList(
        coroutineScope: CoroutineScope,
        searchName: String,

        ) {
        coroutineScope.launch {
            apiRepository.getAllPeopleList(searchName = searchName, tribeId = "")
                .cachedIn(this)
                .collect { pagingData ->
                    allPeopleListFlow.value = pagingData
                }
        }
    }


}