package com.medrevpatient.mobile.app.ux.main.bottombar
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Composable
fun <T : Enum<T>> RowScope.AppBottomNavigationItem(
    navBarItem: T,
    unselectedIconDrawableId: Int,
    selectedIconDrawableId: Int,
    selectedBarItem: Boolean,
    @StringRes textResId: Int? = null,
    onNavItemClicked: (T) -> Unit,
) {
    NavigationBarItem(
        icon = {
            Box(contentAlignment = Alignment.TopCenter) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (selectedBarItem) {
                        Image(
                            painter = painterResource(id = selectedIconDrawableId),
                            contentDescription = null
                        )
                    } else {
                        Image(
                            painter = painterResource(id = unselectedIconDrawableId),
                            contentDescription = null
                        )
                    }
                }
                if (selectedBarItem) {
                    Image(
                        painterResource(id = R.drawable.ic_desh),
                        contentDescription = null,
                        modifier = Modifier.offset(y = (-14).dp)
                    )
                }
            }
        },
        label = {
            if (textResId != null) {
                val transition = updateTransition(
                    targetState = selectedBarItem,
                    label = "BottomBarLabelTransition"
                )
                val scale by transition.animateFloat(label = "Scale") { selected ->
                    if (selected) 1f else 0f
                }
                val alpha by transition.animateFloat(label = "Alpha") { selected ->
                    if (selected) 1f else 0f
                }
                Text(
                    text = stringResource(textResId),
                    fontSize = 10.sp,
                    color = AppThemeColor,
                    fontFamily = nunito_sans_600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier

                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                )
            }
        },
        selected = false,
        onClick = { onNavItemClicked(navBarItem) },
        alwaysShowLabel = true
    )
}
