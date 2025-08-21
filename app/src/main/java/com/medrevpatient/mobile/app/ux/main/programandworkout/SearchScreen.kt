package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.data.source.remote.dto.ProgramWorkoutAndRecipesSearch
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.greyE9
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.transformToLazyPagingItems

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    SearchScreenContent(
        event = {},
        searchResult = listOf(
            ProgramWorkoutAndRecipesSearch(
                "name",
                "description",
                "subtitle"
            )
        ).transformToLazyPagingItems()
    )
}

@Composable
fun SearchScreen(
    viewModel: ProgramsAndWorkOutViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchResult = uiState.searchResult.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        containerColor = white,
        topBar = {
            VStack(4.dp) {
                TitleHeader {
                    viewModel.popBackStack()
                }
                SearchProgram(
                    value = uiState.searchKeyword,
                    onValueChange = { viewModel.event(ProgramsAndWorkOutUiEvent.Search(it)) },
                    event = viewModel::event,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
    ) { innerPadding ->
        SearchScreenContent(
            searchResult = searchResult,
            event = viewModel::event,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        )
    }
}

@Composable
fun SearchScreenContent(
    searchResult: LazyPagingItems<ProgramWorkoutAndRecipesSearch>,
    event: ProgramAndWorkOutEvent,
    modifier: Modifier = Modifier
) {

    val textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    val textModifier = Modifier.fillMaxWidth()

    PagingResultHandler(lazyPagingState = searchResult, modifier) { onSuccess ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(count = onSuccess.itemCount) { index ->

                val item = searchResult[index] ?: return@items

                Card(
                    shape = RoundedCornerShape(25),
                    colors = CardDefaults.cardColors(containerColor = greyE9),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (item.isProgramType) {
                            event(
                                ProgramsAndWorkOutUiEvent.NavigateTo(
                                    RouteMaker.ViewProgram.createRoute(item.id)
                                )
                            )
                        } else {
                            event(
                                ProgramsAndWorkOutUiEvent.NavigateTo(
                                    RouteMaker.ViewRecipe.createRoute(item.id)
                                )
                            )
                        }
                    }
                ) {
                    VStack(
                        0.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = textStyle,
                            modifier = textModifier
                        )
                        Text(
                            text = item.description,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis,
                            style = textStyle.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = grey94
                            ),
                            modifier = textModifier
                        )

                        item.subtitle?.let {
                            Text(
                                text = "includes: $it",
                                style = textStyle.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                ),
                                modifier = textModifier
                            )
                        }
                    }
                }
            }

            when (val appendState = searchResult.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        FullSizeCircularLoader(
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                is LoadState.Error -> {
                    item {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = textStyle.toSpanStyle()) {
                                    append(
                                        appendState.error.localizedMessage
                                            ?: "Something went wrong!"
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
                                    searchResult.retry()
                                }
                        )
                    }
                }

                is LoadState.NotLoading -> {}
            }
        }
    }
}


@Composable
fun SearchProgram(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    event: ProgramAndWorkOutEvent
) {
    var dismissPop by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                dismissPop = false
            },
            placeholder = {
                Text(
                    text = "Search Programs, Workouts, Recipes",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(painter = painterResource(drawable.search), contentDescription = "Search")
            },
            trailingIcon = {
                if (value.isNotEmpty())
                    IconButton(
                        onClick = {
                            event(ProgramsAndWorkOutUiEvent.ClearSearch)
                        }
                    ) {
                        Icon(
                            painter = painterResource(drawable.close),
                            contentDescription = "Close"
                        )
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25),
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = black25,
                focusedIndicatorColor = black25,
                disabledIndicatorColor = black25,
                errorIndicatorColor = black25,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                textSelectionColors = TextSelectionColors(
                    handleColor = black25,
                    backgroundColor = black25.copy(alpha = .1f)
                )
            )
        )
    }
}