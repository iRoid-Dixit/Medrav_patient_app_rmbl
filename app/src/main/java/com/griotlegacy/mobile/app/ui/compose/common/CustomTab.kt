package com.griotlegacy.mobile.app.ui.compose.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.White50
import com.griotlegacy.mobile.app.ui.theme.WorkSans
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabSliderBar(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0,
    content: @Composable (Int) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val pagerState = rememberPagerState(initialPage = initialTabIndex, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                if (tabIndex < tabPositions.size) {
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        height = 2.dp,
                        color = White
                    )

                }
            },
            containerColor = Black,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            title,
                            fontSize = 13.sp,
                            fontFamily = WorkSans,
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.padding()
                        )
                    },
                    selectedContentColor = White,
                    unselectedContentColor = White50,
                    selected = tabIndex == index,
                    onClick = {
                        if (tabIndex != index) {
                            tabIndex = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
                )
            }
        }
        HorizontalPager(
            // count = tabs.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            content(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (tabIndex != page) {
                tabIndex = page
            }
        }
    }
}

@Preview
@Composable
fun TowyTabSliderBarPreview() {
    val tabs = listOf("InnerCircle", "Tribe")
    TabSliderBar(
        tabs = tabs,
        initialTabIndex = 0,
        modifier = Modifier
    ) { page ->
        when (page) {
            0 -> {}
            1 -> {

            }
        }
    }


}
