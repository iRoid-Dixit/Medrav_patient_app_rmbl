package com.medrevpatient.mobile.app.ux.startup.onboarding

import android.content.Context
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GetOnboardingUiStateUseCase
@Inject constructor(
    private val localManager: LocalManager
) {

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): OnboardingUiState {
        return OnboardingUiState(
            onStartClick = {
                navigateToAgeBoardingScreen(navigate)
            }
        )
    }

    private fun navigateToAgeBoardingScreen(navigate: (NavigationAction) -> Unit) {
        navigate(NavigationAction.Navigate(com.medrevpatient.mobile.app.navigation.RouteMaker.OnBoardDataRoute.createRoute()))
    }

}