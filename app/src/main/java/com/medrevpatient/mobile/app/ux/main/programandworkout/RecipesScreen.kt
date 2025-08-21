package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipes
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.ux.main.component.RoundedImageWithRowDescription
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault.textStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel, modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBarCenterAlignTextAndBack("Recipes", onBackPress = { viewModel.popBackStack() })
        }, containerColor = white, modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->
        RecipesScreenContent(
            uiState = uiState,
            event = viewModel::event,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}

@Composable
fun RecipesScreenContent(
    uiState: RecipesUiState, event: RecipesEvents, modifier: Modifier = Modifier
) {
    val recipesLazyPagingState = uiState.recipes.collectAsLazyPagingItems()

    VStack(
        spaceBy = 0.dp, modifier = modifier
    ) {

        SearchBar(
            value = uiState.searchKeyword,
            onValueChange = { event(RecipesUiEvent.SearchRecipes(it)) },
            event = event,
            filters = uiState.filterTags,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        RecipeList(
            event = event,
            recipesPagingItems = recipesLazyPagingState,
            modifier = Modifier.fillMaxHeight()
        )
    }
}


@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    filters: List<String>,
    event: RecipesEvents,
    modifier: Modifier = Modifier
) {
    var shouldShowFilterSheet by remember { mutableStateOf(false) }

    HStack(
        8.dp, modifier = modifier.then(
            Modifier
                .padding(2.dp)
                .height(56.dp)
        )
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            shape = RoundedCornerShape(25),
            textStyle = MaterialTheme.typography.labelLarge,
            placeholder = {
                Text(
                    text = "Search by Title, Ingredients",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,

                    )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    tint = black25
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) IconButton(onClick = {
                    event(RecipesUiEvent.ClearSearch)
                }) {
                    Icon(
                        painter = painterResource(drawable.close), contentDescription = "Close"
                    )
                }
            },

            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = black25,
                unfocusedIndicatorColor = black25,
                focusedTextColor = black25,
                cursorColor = Color.Black,
                textSelectionColors = TextSelectionColors(
                    handleColor = black25, backgroundColor = black25.copy(alpha = .1f)
                )
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                //TODO:Implementation pending
            }),
            modifier = Modifier.weight(1f)
        )


        IconButton(
            onClick = { shouldShowFilterSheet = !shouldShowFilterSheet },
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 56.dp)
                .background(
                    color = black25.copy(alpha = 0.1f), shape = RoundedCornerShape(25)
                )
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.filter), contentDescription = null
            )
        }
    }

    if (filters.isNotEmpty()) FilterSheet(
        shouldShowSheet = shouldShowFilterSheet, filters = filters, onDismiss = {
            shouldShowFilterSheet = !shouldShowFilterSheet
        }, event = event
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheet(
    shouldShowSheet: Boolean,
    filters: List<String>,
    onDismiss: () -> Unit,
    event: RecipesEvents,
    modifier: Modifier = Modifier,
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { newState ->
            (!shouldShowSheet || (newState != SheetValue.Hidden))
        })

    val scope = rememberCoroutineScope()

    val selectedTags = remember { mutableStateListOf<String>() }

    fun CoroutineScope.hideSheet() {
        this.launch {
            if (sheetState.isVisible) {
                sheetState.hide()
            }
        }.invokeOnCompletion {
            Timber.d("Dismiss")
            onDismiss()
        }
    }

    if (shouldShowSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            modifier = modifier,
            onDismissRequest = {
                scope.hideSheet()
            },
            containerColor = Color.White,
        ) {

            VStack(
                18.dp,
                modifier = Modifier
                    .heightIn(max = 350.dp)
                    .fillMaxSize()
                    .padding(bottom = 18.dp)
            ) {

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(filters) {
                        FilterCheckBoxItem(text = it,
                            isChecked = selectedTags.contains(it),
                            onClick = {
                                if (selectedTags.contains(it)) {
                                    selectedTags.remove(it)
                                } else {
                                    selectedTags.add(it)
                                }
                            })
                    }

                }

                HStack(
                    spaceBy = 8.dp, modifier = Modifier.padding(horizontal = 18.dp)
                ) {
                    SkaiButton(text = if (selectedTags.isNotEmpty()) "clear" else "cancel",
                        makeUpperCase = true,
                        color = white,
                        textStyle = textStyle.copy(black25),
                        borderStroke = BorderStroke(1.dp, black25),
                        modifier = Modifier.weight(1f),
                        innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                        onClick = {
                            if (selectedTags.isEmpty()) {
                                scope.hideSheet()
                            } else {
                                selectedTags.clear()
                                event(RecipesUiEvent.ApplyFilter(selectedTags.joinToString()))
                            }
                        })

                    SkaiButton(text = "Apply",
                        innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                        makeUpperCase = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            event(RecipesUiEvent.ApplyFilter(selectedTags.joinToString()))
                            scope.hideSheet()
                        })
                }
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
private fun FilterCheckBoxItem(
    text: String = "test",
    isChecked: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    HStack(spaceBy = 8.dp,
        modifier = modifier
            .clip(RoundedCornerShape(25))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = black25,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onClick
        ) {
            Image(
                painter = painterResource(if (isChecked) R.drawable.check_box_checked else R.drawable.check_box_unchecked),
                contentDescription = null
            )
        }
    }
}


@Composable
private fun RecipeList(
    recipesPagingItems: LazyPagingItems<Recipes.Data>,
    event: RecipesEvents,
    modifier: Modifier = Modifier
) {

    PagingResultHandler(lazyPagingState = recipesPagingItems) { pagingState ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = modifier
        ) {

            items(
                count = pagingState.itemCount,
                key = { index -> "${index}_${pagingState[index]?.id ?: ""}" }
            ) { index ->

                val recipe = pagingState[index] ?: return@items

                RoundedImageWithRowDescription(image = recipe.image,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18))
                        .background(color = black25)
                        .height(162.dp)
                        .clickable {
                            recipe.id.isNotEmpty().not().let {
                                event(
                                    RecipesUiEvent.NavigateTo(
                                        RouteMaker.ViewRecipe.createRoute(recipe.id)
                                    )
                                )
                            }
                        }) {
                    Text(
                        text = recipe.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 7.dp)
                    )
                }
            }

            when (val appendState = pagingState.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        FullSizeCircularLoader(modifier = Modifier.size(56.dp))
                    }
                }

                is LoadState.Error -> {
                    item {
                        Text(text = buildAnnotatedString {
                            withStyle(style = textStyle.toSpanStyle()) {
                                append(
                                    appendState.error.localizedMessage ?: "Something went wrong!"
                                )
                            }
                            withStyle(
                                style = textStyle.toSpanStyle()
                                    .copy(fontWeight = FontWeight.SemiBold)
                            ) {
                                append("\nRetry")
                            }
                        },
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pagingState.retry()
                                })
                    }
                }

                is LoadState.NotLoading -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipesScreenPreview(
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White, topBar = {
            TopBarCenterAlignTextAndBack("Recipes", onBackPress = {})
        }, modifier = modifier
    ) { innerPadding ->
        RecipesScreenContent(
            uiState = RecipesUiState(), event = {}, modifier = Modifier.padding(innerPadding)
        )
    }
}