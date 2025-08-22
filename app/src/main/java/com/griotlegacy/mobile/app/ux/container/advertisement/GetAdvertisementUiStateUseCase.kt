package com.griotlegacy.mobile.app.ux.container.advertisement

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.navigation.NavigationAction.*
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.ux.container.addAdvertisement.AddAdvertisementRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetAdvertisementUiStateUseCase
@Inject constructor(

    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val advertisementDataFlow = MutableStateFlow(AdvertisementDataState())
    private var advertisementsList =
        MutableStateFlow<PagingData<AdvertisementResponse>>(PagingData.empty())

    operator fun invoke(
        coroutineScope: CoroutineScope,

        navigate: (NavigationAction) -> Unit,
    ): AdvertisementUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        getAdvertisements(coroutineScope = coroutineScope)
        return AdvertisementUiState(
            advertisementDataFlow = advertisementDataFlow,
            advertisementList = advertisementsList,
            event = { aboutUsEvent ->
                blockListUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,

                    )
            }
        )
    }

    private fun blockListUiEvent(
        event: AdvertisementUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            AdvertisementUiEvent.BackClick -> {
                navigate(PopIntent)
            }
            is AdvertisementUiEvent.GetContext -> {
                this.context = event.context
            }
            AdvertisementUiEvent.NavigateAddAdvertisement -> {
                // navigate(NavigationAction.Navigate(AdvertisementSubscriptionRoute.createRoute()))
                val advertisementData = Gson().toJson(AdvertisementResponse())
                navigate(Navigate(AddAdvertisementRoute.createRoute(screen = Constants.AppScreen.ADD_ADVERTISEMENT_SCREEN, advertisementData = advertisementData)))
            }
            AdvertisementUiEvent.AdvertisementAPICall -> {
                getAdvertisements(coroutineScope = coroutineScope)
            }

            is AdvertisementUiEvent.EditAdvertisement -> {
                val advertisementData = Gson().toJson(event.advertisement)
                navigate(Navigate(AddAdvertisementRoute.createRoute(screen = Constants.AppScreen.EDIT_ADVERTISEMENT_SCREEN, advertisementData = advertisementData)))
            }
        }
    }
    private fun getAdvertisements(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getAdvertisements().cachedIn(this).collect { pagingData ->
                advertisementsList.value = pagingData
            }
        }
    }


}


