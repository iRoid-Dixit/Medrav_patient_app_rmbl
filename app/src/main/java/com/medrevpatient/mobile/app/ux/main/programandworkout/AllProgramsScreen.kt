package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.utils.alias.string
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramItemComponent
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramsItem


@Preview
@Composable
private fun AllProgramScreenPreview() {
    AllProgramScreen(
        viewModel = hiltViewModel(),
        modifier = Modifier
    )
}

@Composable
fun AllProgramScreen(
    viewModel: ProgramsAndWorkOutViewModel,
    modifier: Modifier = Modifier,
) {

    val programs = viewModel.programs.collectAsLazyPagingItems()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "All Programs",
                onBackPress = {
                    if (viewModel.isFromNotification) {
                        //redirect to main screen
                        val intent = Intent(context, MainActivity::class.java)
                        viewModel.navigate(NavigationAction.NavigateIntent(intent, finishCurrentActivity = true))
                    } else {
                        viewModel.popBackStack()
                    }

                }
            )
        }
    ) { innerPadding ->
        AllProgramContent(
            pagingItems = programs,
            event = viewModel::event,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        )
    }
}

@Composable
fun AllProgramContent(
    pagingItems: LazyPagingItems<Program>,
    modifier: Modifier = Modifier,
    event: ProgramAndWorkOutEvent
) {

    PagingResultHandler(pagingItems) { pagingState ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(pagingState.itemCount) {

                val program = pagingItems[it] ?: return@items

                ProgramItemComponent(
                    programItem = ProgramsItem(
                        url = program.image,
                        title = program.name,
                        time = stringResource(string.append_days, program.days),
                        calories = stringResource(string.append_kcal, program.kcal)
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    event(
                        ProgramsAndWorkOutUiEvent.NavigateTo(
                            com.medrevpatient.mobile.app.navigation.RouteMaker.ViewProgram.createRoute(
                                program.id
                            )
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun ProgramItemPreview(modifier: Modifier = Modifier) {
    ProgramItemComponent(
        programItem = ProgramsItem(
            url = "",
            title = "Step It Up Fitness Boot Camp",
            time = "60 day",
            calories = "530 KCAL"
        ),
        modifier = modifier
    )
}



