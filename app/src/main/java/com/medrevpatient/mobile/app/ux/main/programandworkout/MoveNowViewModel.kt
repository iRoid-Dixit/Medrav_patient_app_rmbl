package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.domain.usecases.DemandClassesUseCase
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.utils.ext.toJsonString
import com.medrevpatient.mobile.app.ux.main.player.PlayerActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoveNowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    demandClassesUseCase: DemandClassesUseCase
) : BaseViewModel() {

    private val isForME: Int = savedStateHandle[RouteMaker.Keys.IS_FOR_ME] ?: 1

    val demandClassesPagingSource = demandClassesUseCase(type = isForME).cachedIn(viewModelScope)

    fun event(event: MoveNowUiEvent) {
        when (event) {
            is MoveNowUiEvent.NavigateTo -> {
                navigate(NavigationAction.Navigate(event.navRoute))
            }

            is MoveNowUiEvent.StartPlayer -> {
                handlePlayerNavigation(demandClasses = event.onDemandClasses, context = event.ctx)
            }
        }
    }

    private fun handlePlayerNavigation(context: Context, demandClasses: OnDemandClasses) {
        Intent(context, PlayerActivity::class.java).apply {
            val gSon = demandClasses.toJsonString()
            this.putExtra(Constants.Intents.Player_ONE_KEY, gSon)
            context.startActivity(this)
        }
    }
}

sealed interface MoveNowUiEvent {
    data class NavigateTo(val navRoute: NavRoute) : MoveNowUiEvent
    data class StartPlayer(val onDemandClasses: OnDemandClasses, val ctx: Context) : MoveNowUiEvent

}