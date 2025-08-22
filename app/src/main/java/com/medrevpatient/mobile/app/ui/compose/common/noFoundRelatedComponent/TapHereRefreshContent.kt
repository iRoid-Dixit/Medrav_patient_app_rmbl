package com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@Composable
fun TapHereRefreshContent(onClick: () -> Unit = {}) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.something_went_wrong),
                fontSize = 16.sp,
                maxLines = 1,
                color = White,
                fontFamily = WorkSans,

            )
            Text(
                modifier = Modifier.clickable {
                    onClick()
                },
                text = stringResource(id = R.string.tap_here_to_refresh_it),
                fontSize = 16.sp,
                maxLines = 1,
                color = White,
                fontFamily = WorkSans,
            )
        }
    }


}