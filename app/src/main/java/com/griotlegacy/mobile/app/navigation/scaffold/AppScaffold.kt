@file:OptIn(ExperimentalMaterial3Api::class)

package com.griotlegacy.mobile.app.navigation.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger

/**
 * This scaffold will be updated based on requirement
 * */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topAppBar: @Composable (() -> Unit)? = null,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.background,
    navBarData: AppNavBarData? = null,
    content: @Composable (PaddingValues) -> Unit,
) {

    Scaffold(
        topBar = topAppBar ?: {},
        modifier = modifier,
        containerColor = containerColor,
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
                Logger.e("AppScaffoldContentWrapper - Nav bar items")
                Row {
                    content(innerPadding)
                    navBarData.bottomBar().invoke()
                }
            }

            else -> {
                // Content
                Logger.e("AppScaffoldContentWrapper - Else called")
                content(innerPadding)
            }
        }
    }
}