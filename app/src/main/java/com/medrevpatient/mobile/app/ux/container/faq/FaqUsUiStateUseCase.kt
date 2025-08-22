package com.medrevpatient.mobile.app.ux.container.faq
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.container.faqQuestion.FAQQuestionResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class FaqUsUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private val contactUsDataFlow = MutableStateFlow(ContactUsDataState())
    private val faqQuestionList =
        MutableStateFlow<PagingData<FAQQuestionResponse>>(PagingData.empty())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ContactUsUiState {
        //getFqaQuestionData(coroutineScope = coroutineScope)
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return ContactUsUiState(
            contactUsDataFlow = contactUsDataFlow,
            faqQuestionListFlow = faqQuestionList,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun contactUsUiEvent(
        event: ContactUsUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope

        ) {
        when (event) {
            ContactUsUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is ContactUsUiEvent.GetContext -> {


            }

            is ContactUsUiEvent.GetScreenName -> {
                if (event.screen == Constants.AppScreen.FQA_SCREEN) {
                    getFqaQuestionData(coroutineScope = coroutineScope)
                } else {
                    getReflectionAPI(coroutineScope = coroutineScope)
                }
            }
        }
    }

    private fun getFqaQuestionData(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getFaqQuestion().cachedIn(this).collect { pagingData ->
                faqQuestionList.value = pagingData
            }
        }
    }
    private fun getReflectionAPI(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getReflection().cachedIn(this).collect { pagingData ->
                faqQuestionList.value = pagingData
            }
        }
    }






}


