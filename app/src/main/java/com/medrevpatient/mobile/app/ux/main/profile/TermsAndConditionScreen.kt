package com.medrevpatient.mobile.app.ux.main.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack

@Composable
fun TermsAndConditionScreen(
    modifier: Modifier = Modifier,
    viewModel: TermsAndConditionViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = stringResource(id = R.string.terms_condition),
                onBackPress = {
                    viewModel.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        TermsAndConditionContent(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}

@Composable
fun TermsAndConditionContent(modifier: Modifier = Modifier) {

}