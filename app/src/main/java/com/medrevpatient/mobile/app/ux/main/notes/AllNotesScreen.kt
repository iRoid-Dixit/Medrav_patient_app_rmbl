package com.medrevpatient.mobile.app.ux.main.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.navigation.RouteMaker.CrudNote
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH_FOR_ME
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault.textStyle
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeEvent
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeUiEvent
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun AllNotesScreen(
    navController: NavHostController,
    viewModel: ForMeViewModel,
    modifier: Modifier = Modifier
) {

    var shouldRefreshForMeScreen by remember { mutableStateOf(false) }

    val notes = viewModel.notes.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "All Notes",
                onBackPress = {
                    viewModel.popBackStackWithResult(
                        listOf(
                            PopResultKeyValue(
                                key = REFRESH_FOR_ME,
                                value = shouldRefreshForMeScreen
                            )
                        )
                    )
                }
            )
        },
        containerColor = Color.White,
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->

        NotesContent(
            pagingItems = notes,
            event = viewModel::event,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        )
    }

    getFromBackStack<Boolean>(key = REFRESH, navController = navController)?.let {
        shouldRefreshForMeScreen = it
        Timber.d("AllNotes $it")
        scope.launch(Dispatchers.Main) {
            try {
                notes.refresh()
                Timber.d("Notes refreshed successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh notes")
            }
        }
    }

}

@Composable
private fun NotesContent(
    pagingItems: LazyPagingItems<Note>,
    event: ForMeEvent,
    modifier: Modifier = Modifier
) {

    PagingResultHandler(pagingItems, modifier) { onSucess ->

        Box(modifier = Modifier.fillMaxSize()) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                items(pagingItems.itemCount) { index ->
                    val note = pagingItems[index] ?: return@items
                    NotesItem(
                        title = note.title,
                        body = note.body,
                        modifier = Modifier.height(218.dp),
                        onClick = { event(ForMeUiEvent.Notes.NavigateTo(CrudNote.createRoute(note))) }
                    )
                }

                when (val appendState = pagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item(
                            span = {
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
                            FullSizeCircularLoader(modifier = Modifier.size(56.dp))
                        }
                    }

                    is LoadState.Error -> {
                        item(
                            span = {
                                GridItemSpan(maxLineSpan)
                            }
                        ) {
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
                                        pagingItems.retry()
                                    }
                            )
                        }
                    }

                    is LoadState.NotLoading -> {}
                }
            }

            SkaiButton(
                text = "create new",
                modifier = Modifier
                    .offset(y = -(22.dp))
                    .align(Alignment.BottomCenter)
            ) {
                event(ForMeUiEvent.Notes.NavigateTo(CrudNote.createRoute()))
            }
        }
    }
}


