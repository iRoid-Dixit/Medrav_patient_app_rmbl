package com.medrevpatient.mobile.app.ux.container.about

import android.annotation.SuppressLint
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.R
@ExperimentalMaterial3Api
@Composable
fun AboutScreen(
    navController: NavController,
    viewModel: AboutViewModel = hiltViewModel(),
    screenName: String,
    url: String
) {
    val uiState = viewModel.uiState
    val aboutOtpUiState by uiState.aboutUsDataFlow.collectAsStateWithLifecycle()

    val titleName = when (screenName) {
        Constants.AppScreen.ABOUT_US -> {
            stringResource(R.string.about_us)
        }

        Constants.AppScreen.TERM_AND_CONDITION_SCREEN -> {
            stringResource(R.string.terms_and_conditions)
        }

        Constants.AppScreen.LEGACY_REFLECTION_SCREEN -> {
            "Legacy Reflection"
        }

        else -> {
            stringResource(R.string.privacy_policy)
        }
    }
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = titleName,
                isLineVisible = true,
                isBackVisible = true,
                onClick = {
                    uiState.event(AboutUiEvent.BackClick)
                },

                )
        },
        navBarData = null
    ) {
        AboutScreenContent(url = url)
    }
    if (aboutOtpUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AboutScreenContent(url: String) {
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
        ) {
            if (url.isEmpty()) {
                // Show a message when URL is not available
                Text(
                    text = "Loading content...",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            } else if (isLoading) {
                CustomLoader() // Show loading spinner
            }

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        clearCache(true)

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                isLoading = false// Stop loader even if an error occurs
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                view?.loadUrl(request?.url.toString())
                                return true
                            }
                        }
                        if (url.isNotEmpty()) {
                            loadUrl(url)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/*
@Preview
@Composable
fun AboutScreenContentPreview() {
    AboutScreenContent("")

}
*/






