package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack

@Composable
fun AllOnDemandClassesScreen(
    viewModel: MoveNowViewModel,
    modifier: Modifier = Modifier
) {

    val onDemandClasses = viewModel.demandClassesPagingSource.collectAsLazyPagingItems()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "On Demand Classes",
                onBackPress = { viewModel.popBackStack() }
            )
        },
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->
        MoveNowComponent(
            isOnDemandClasses = true,
            pagingItems = onDemandClasses,
            event = viewModel::event,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}