package com.medrevpatient.mobile.app.ux.startup.onboarding.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSplashUiStateUseCase
@Inject constructor(
    private val localManager: LocalManager
) {
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SplashUiState {
        navigateToNextScreen(
            context = context,
            navigate = navigate,
            coroutineScope = coroutineScope
        )
        return SplashUiState(
            notificationKey = {
                if (!it.isEmpty) {
                    managePushIntent(it, context, navigate)
                }
            }
        )
    }

    private fun navigateToNextScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            delay(3000)
            if (localManager.retrieveUserData() != null) {
                val intent = Intent(context, MainActivity::class.java)
                navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
            } else {
                navigate(NavigationAction.PopAndNavigate(RouteMaker.SignInRoute.createRoute()))
            }
        }
    }

    private fun managePushIntent(
        bundle: Bundle,
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ): Intent {
        if (bundle.getString("type").toString().isNotEmpty()) {
            when (bundle.getString("type")) {
                Constants.NotificationConstants.PUSH_TYPE_PROGRAM -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(Constants.IntentKeys.START_DESTINATION_FOR_MAIN, Constants.Keywords.ALL_PROGRAMS)
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }

                Constants.NotificationConstants.PUSH_TYPE_GOAL -> {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(Constants.IntentKeys.START_DESTINATION_FOR_MAIN, Constants.Keywords.VIEW_GOALS)
                    intent.putExtra(Constants.IntentKeys.GOAL_ID, bundle.getString("goalId"))
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = false
                        )
                    )
                    return intent
                }

                else -> {
                    return MainActivity.newIntent(context)
                }
            }
        } else {
            return Intent(context, MainActivity::class.java)
        }
    }
}