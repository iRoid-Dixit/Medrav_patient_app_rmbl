package com.medrevpatient.mobile.app.navigation.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * This scaffold will be updated based on requirement
 * */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable (() -> Unit)? = null,
    navBarData: AppNavBarData? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topAppBar ?: {},
        modifier = modifier,
        bottomBar = navBarData?.bottomBar() ?: {}
    ) { innerPadding ->
        AppScaffoldContentWrapper(innerPadding, navBarData, content)
    }
}

@Composable
private fun AppScaffoldContentWrapper(
    innerPadding: PaddingValues,
    navBarData: AppNavBarData? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(modifier = Modifier.padding(innerPadding)) {
        when (navBarData?.appNavBarType) {
            AppNavBarType.NAV_BAR -> {
                // Content with NavigationRail
                Row {
                    content(innerPadding)
                    navBarData.bottomBar().invoke()
                }
            }

            else -> {
                // Content
                content(innerPadding)
            }
        }
    }
}