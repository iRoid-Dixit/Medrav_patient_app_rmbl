package com.griotlegacy.mobile.app.ux.container.about
import android.util.Log
import com.griotlegacy.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GetAboutUiStateUseCase
@Inject constructor(
) {
    private val aboutUsDataFlow = MutableStateFlow(AboutUSDataState())
    operator fun invoke(
        url: String,
        navigate: (NavigationAction) -> Unit,
    ): AboutUiState {
        Log.d("TAG", "url: $url")
        aboutUsDataFlow.update { state ->
            state.copy(
                showLoader = aboutUsDataFlow.value.showLoader,
            )
        }
        return AboutUiState(
            aboutUsDataFlow = aboutUsDataFlow,
            event = { aboutUsEvent ->
                aboutUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                )
            }
        )
    }
    private fun aboutUiEvent(
        event: AboutUiEvent,
        navigate: (NavigationAction) -> Unit,
    ) {
        when (event) {
            AboutUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }


        }
    }
}


