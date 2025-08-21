package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType.FOR_GENERAL
import com.medrevpatient.mobile.app.data.source.remote.dto.PinRequest
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipe
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipes
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.APIPagingCallBack
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import com.medrevpatient.mobile.app.utils.Debouncing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


//Shared ViewModel for Recipes and ViewRecipe Screen
@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val debouncing: Debouncing,
    private val repository: ApiRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val recipeId: String? = savedStateHandle[RouteMaker.Keys.ID]
    private val resultType: Int = savedStateHandle[RouteMaker.Keys.IS_FOR_ME] ?: FOR_GENERAL.value

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        Timber.d("RecipeId: $recipeId")
        recipeId?.let {
            fetchRecipe(it) //Fetch Recipe by Id for its Detail Screen
        } ?: getSearchedRecipes()
    }

    private fun getSearchedRecipes() {
        _uiState.update {
            it.copy(
                recipes = repository.getSearchedRecipes(
                    it.searchKeyword,
                    type = resultType,
                    apiPagingCallBack = object : APIPagingCallBack<List<String>>() {
                        override fun onSuccess(response: List<String>) {
                            _uiState.update { state ->
                                state.copy(filterTags = response)
                            }
                        }
                    }).cachedIn(viewModelScope)
            )
        }
    }

    fun event(event: RecipesUiEvent) {
        when (event) {
            is RecipesUiEvent.NavigateTo -> {
                navigate(event.navRoute)
            }

            is RecipesUiEvent.SearchRecipes -> {
                _uiState.update {
                    it.copy(searchKeyword = event.keyword)
                }
                debouncing.debounce(delay = 500) {
                    searchRecipes(event.keyword)
                }
            }

            RecipesUiEvent.ClearSearch -> {
                _uiState.update {
                    it.copy(
                        searchKeyword = "",
                        recipes = repository.getSearchedRecipes("", type = resultType,
                            apiPagingCallBack = object : APIPagingCallBack<List<String>>() {
                                override fun onSuccess(response: List<String>) {

                                }
                            })
                            .cachedIn(viewModelScope)
                    )
                }
            }

            is RecipesUiEvent.ViewRecipeUiEvent.TogglePinProgram -> pinRecipe(id = event.id)

            RecipesUiEvent.RefreshRecipes -> {
                debouncing.debounce(delay = 500) {
                    searchRecipes(uiState.value.searchKeyword)
                }
            }

            is RecipesUiEvent.ApplyFilter -> {
                _uiState.update {
                    it.copy(
                        selectedFilters = event.filter,
                        recipes = repository.getSearchedRecipes(
                            it.searchKeyword,
                            filterTag = event.filter,
                            type = resultType,
                            apiPagingCallBack = object : APIPagingCallBack<List<String>>() {
                                override fun onSuccess(response: List<String>) {

                                }

                            }
                        ).cachedIn(viewModelScope)
                    )
                }
            }

            RecipesUiEvent.RefreshRecipe -> recipeId?.let { fetchRecipe(it) }
        }
    }


    private fun searchRecipes(keyword: String) {
        _uiState.update {
            it.copy(
                recipes = repository.getSearchedRecipes(
                    keyword,
                    filterTag = uiState.value.selectedFilters,
                    type = resultType,
                    apiPagingCallBack = object : APIPagingCallBack<List<String>>() {
                        override fun onSuccess(response: List<String>) {

                        }
                    }
                ).cachedIn(viewModelScope))
        }
    }


    private fun fetchRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.getRecipe(recipeId).collect { network ->
                _uiState.update {
                    it.copy(
                        recipe = network,
                        isPin = network.data?.data?.isPin ?: false
                    )
                }
            }
        }
    }


    private fun pinRecipe(id: String) {
        viewModelScope.launch {
            repository.pin(
                pinRequest = PinRequest(
                    type = 2,
                    itemId = id
                )
            ).collect { network ->
                when (network) {
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                hasError = network.message
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                hasError = null
                            )
                        }
                    }

                    is NetworkResult.Success -> {

                        val previousState = uiState.value.isPin

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                hasError = null,
                                isPin = previousState.not()
                            )
                        }
                    }
                }
            }
        }
    }
}

typealias RecipesEvents = (RecipesUiEvent) -> Unit

sealed interface RecipesUiEvent {
    object ClearSearch : RecipesUiEvent
    data class NavigateTo(val navRoute: NavRoute) : RecipesUiEvent
    data class SearchRecipes(val keyword: String) : RecipesUiEvent
    data class ApplyFilter(val filter: String) : RecipesUiEvent
    object RefreshRecipes : RecipesUiEvent
    object RefreshRecipe : RecipesUiEvent
    object ViewRecipeUiEvent {
        data class TogglePinProgram(val id: String) : RecipesUiEvent
    }
}


data class RecipesUiState(
    val isLoading: Boolean = false,
    val hasError: String? = null,
    val searchKeyword: String = "",
    val isPin: Boolean = false,
    val selectedFilters: String = "",
    val filterTags: List<String> = emptyList(),
    val recipes: Flow<PagingData<Recipes.Data>> = MutableStateFlow(PagingData.empty()),
    val recipe: NetworkResult<ApiResponse<Recipe>> = NetworkResult.Loading(),
)

