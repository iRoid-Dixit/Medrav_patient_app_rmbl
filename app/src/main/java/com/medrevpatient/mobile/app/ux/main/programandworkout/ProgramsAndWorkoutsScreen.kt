package com.medrevpatient.mobile.app.ux.main.programandworkout


import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.TempDataSource
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.FullSizeCenterBox
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black25A25
import com.medrevpatient.mobile.app.ui.theme.greyE9
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.transformToLazyPagingItems
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.ux.main.component.HeaderContentWrapper
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramItemComponentCarousalEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun ProgramsAndWorkOutScreen(
    viewModel: ProgramsAndWorkOutViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val programsPager = viewModel.programs.collectAsLazyPagingItems()
    val ctx = LocalContext.current

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        containerColor = white,
        topBar = { TitleHeader() },
    ) { innerPadding ->
        ProgramsAndWorkOutScreenContent(
            uiState = uiState,
            event = viewModel::event,
            programs = programsPager,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }

    uiState.message?.let { msg ->
        AppUtils.Toast(context = ctx, message = msg).show()
        viewModel.event(ProgramsAndWorkOutUiEvent.ResetState)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgramsAndWorkOutScreenContent(
    uiState: ProgramAndWorkOutUiState,
    event: ProgramAndWorkOutEvent,
    modifier: Modifier = Modifier,
    programs: LazyPagingItems<Program>
) {

    var isStrengthLog by remember { mutableStateOf(false) }

    VStack(
        spaceBy = 18.dp,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {

        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            placeholder = {
                Text(
                    text = "Search Programs, Workouts, Recipes",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(painter = painterResource(drawable.search), contentDescription = "Search")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp)
                .clip(RoundedCornerShape(25))
                .clickable { event(ProgramsAndWorkOutUiEvent.NavigateTo(RouteMaker.Search.createRoute())) },
            shape = RoundedCornerShape(25),
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = black25,
                focusedIndicatorColor = black25,
                disabledIndicatorColor = black25,
                errorIndicatorColor = black25,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                disabledLeadingIconColor = black25,
                disabledPlaceholderColor = black25,
            )
        )

        PopularPrograms(
            programs = programs,
            event = event
        )

        HeaderContentWrapper(
            title = "Categories",
            wrapperPadding = 18.dp,
            spaceBy = 18.dp
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                maxItemsInEachRow = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                repeat(categories.size) {
                    val data = categories[it]
                    CategoriesItem(
                        data = data,
                        onCategoryClick = { id ->
                            when (id) {
                                0 -> isStrengthLog = true
                                1 -> event(ProgramsAndWorkOutUiEvent.NavigateTo(RouteMaker.MoveNow.createRoute()))
                                2 -> event(
                                    ProgramsAndWorkOutUiEvent.NavigateTo(
                                        RouteMaker.Recipes.createRoute()
                                    )
                                ) //Passing 1 for sample we can remove it
                                3 -> event(ProgramsAndWorkOutUiEvent.NavigateTo(RouteMaker.ForMe.createRoute()))
                            }
                        },
                        modifier = Modifier
                            .width(0.dp) // Ensures equal width distribution
                            .weight(1f) // Takes half of the parent width
                            .height(108.dp)
                    )
                }
            }
        }
    }

    StrengthLogSheet(
        uiState = uiState,
        event = event,
        shouldShowSheet = isStrengthLog,
        onDismissRequest = { isStrengthLog = false }
    )

}


@Composable
private fun PopularPrograms(
    modifier: Modifier = Modifier,
    programs: LazyPagingItems<Program>,
    event: ProgramAndWorkOutEvent
) {

    val programList = programs.itemSnapshotList.items
    val pagerState = rememberPagerState(pageCount = { programList.size })

    HeaderContentWrapper(
        title = "PROGRAMS",
        trailingText = "All",
        wrapperPadding = 18.dp,
        spaceBy = 18.dp,
        onAllClick = {
            event(ProgramsAndWorkOutUiEvent.NavigateTo(RouteMaker.AllPrograms.createRoute()))
        },
        modifier = modifier
    ) {

        Crossfade(
            targetState = programs.loadState.refresh,
            label = "Program Carousal CrossFade",
            modifier = Modifier.height(248.dp),
        ) { state ->
            when (state) {
                is LoadState.Error -> {
                    FullSizeCenterBox {
                        VStack(8.dp) {
                            Text(
                                text = state.error.message ?: "Something went wrong",
                                style = MaterialTheme.typography.labelLarge
                            )
                            TextButton(
                                onClick = {
                                    programs.retry()
                                }
                            ) {
                                Text(
                                    text = "Retry",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                LoadState.Loading -> {
                    FullSizeCircularLoader()
                }

                is LoadState.NotLoading -> {
                    CarousalEffect(
                        pagerState = pagerState,
                        autoScrollDuration = 2_000L,
                        programs = programList
                    ) { programId ->
                        event(
                            ProgramsAndWorkOutUiEvent.NavigateTo(
                                RouteMaker.ViewProgram.createRoute(
                                    programId
                                )
                            )
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CarousalEffect(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    autoScrollDuration: Long = 0L,
    programs: List<Program>,
    onClick: (String) -> Unit
) {

    if (programs.isEmpty()) return

    VStack(spaceBy = 0.dp, modifier = modifier) {

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 70.dp),
            beyondViewportPageCount = 5,
            modifier = Modifier
                .weight(.9f),
        ) { page ->
            Card(
                shape = RoundedCornerShape(30.dp),
                onClick = { onClick(programs[page].id) },
                colors = cardColors(containerColor = greyE9),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // Scale both X and Y
                        val scale = lerp(
                            start = 0.8f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                ProgramItemComponentCarousalEffect(
                    programItem = programs[page].toProgramsItem(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        HorizontalPagerIndicator(
            pageCount = pagerState.pageCount,
            currentPage = pagerState.currentPage,
            targetPage = pagerState.targetPage,
            currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
            modifier = Modifier.weight(.1f)
        )
    }

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    if (isDragged.not() && autoScrollDuration > 0L) {
        with(pagerState) {
            var currentPageKey by remember { mutableIntStateOf(0) }
            LaunchedEffect(key1 = currentPageKey) {
                launch {
                    delay(timeMillis = autoScrollDuration)
                    val nextPage = (currentPage + 1).mod(pageCount)
                    animateScrollToPage(page = nextPage)
                    currentPageKey = nextPage
                }
            }
        }
    }


}


@Composable
private fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    targetPage: Int,
    currentPageOffsetFraction: Float,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.DarkGray,
    unselectedIndicatorSize: Dp = 10.dp,
    selectedIndicatorSize: Dp = 18.dp,
    indicatorCornerRadius: Dp = selectedIndicatorSize / 2,
    indicatorPadding: Dp = 2.dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentSize()
            .height(selectedIndicatorSize + indicatorPadding * 2)
    ) {

        // draw an indicator for each page
        repeat(pageCount) { page ->
            // calculate color and size of the indicator
            val (color, size) =
                if (currentPage == page || targetPage == page) {
                    // calculate page offset
                    val pageOffset =
                        ((currentPage - page) + currentPageOffsetFraction).absoluteValue
                    // calculate offset percentage between 0.0 and 1.0
                    val offsetPercentage = 1f - pageOffset.coerceIn(0f, 1f)

                    val size =
                        unselectedIndicatorSize + ((selectedIndicatorSize - unselectedIndicatorSize) * offsetPercentage)

                    indicatorColor.copy(
                        alpha = offsetPercentage
                    ) to size
                } else {
                    indicatorColor.copy(alpha = 0.1f) to unselectedIndicatorSize
                }

            // draw indicator
            Box(
                modifier = Modifier
                    .padding(
                        // apply horizontal padding, so that each indicator is same width
                        horizontal = ((selectedIndicatorSize + indicatorPadding * 2) - size) / 2,
                        vertical = size / 4
                    )
                    .clip(RoundedCornerShape(indicatorCornerRadius))
                    .background(color)
                    .width(if (currentPage == page) size * 2 else size)
                    .height(size / 2)
            )
        }
    }
}


@Composable
private fun CategoriesItem(
    modifier: Modifier = Modifier,
    data: CategoryItem,
    onCategoryClick: (Int) -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(25),
        onClick = { onCategoryClick(data.id) }
    ) {

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(data.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            /* todo Remove:
            * placeholder = painterResource(drawable.img_landscape_placeholder),
                error = painterResource(drawable.img_landscape_placeholder),*/


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(black25A25)
            )

            Text(
                text = data.categoryName,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = outFit,
                    fontSize = 16.sp,
                )
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun TitleHeader(
    modifier: Modifier = Modifier,
    tint: Color = black25,
    onBackPress: (() -> Unit)? = null,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        HStack(4.dp) {
            onBackPress?.apply {
                IconButton(
                    onClick = onBackPress,
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .weight(.1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = tint.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = drawable.back),
                            contentDescription = "Back",
                            tint = tint
                        )
                    }
                }
            }

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    ) {
                        append("SKAI")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp
                        )
                    ) {
                        append(" FITNESS")
                    }

                },
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(.8f)
            )
            onBackPress?.apply {
                Spacer(
                    modifier = Modifier
                        .width(48.dp)
                        .weight(.1f)
                )
            }
        }

    }
}


val categories = listOf(
    CategoryItem(
        0,
        image = R.drawable.img_strength,
        categoryName = "Strength Log"
    ),
    CategoryItem(
        1,
        image = R.drawable.img_move_now,
        categoryName = "Move Now"
    ),
    CategoryItem(
        2,
        image = R.drawable.img_recipe,
        categoryName = "Recipes"
    ),
    CategoryItem(
        3,
        image = R.drawable.img_for_me,
        categoryName = "For Me"
    )
)

data class CategoryItem(
    val id: Int,
    val image: Int,
    val categoryName: String
)


@Preview(showBackground = true)
@Composable
fun ProgramsAndWorkOutScreenPreview(modifier: Modifier = Modifier) {
    /**
     * White background issue when bottom sheet is open in preview mode, It will resolve when we run in emulator or real device
     * */

    MedrevPatientTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.White
        ) {
            Scaffold(
                modifier = modifier,
                containerColor = white,
                topBar = { TitleHeader() },
            ) { innerPadding ->
                ProgramsAndWorkOutScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    event = {},
                    uiState = ProgramAndWorkOutUiState(searchKeyword = ""),
                    programs = TempDataSource.samplePrograms.transformToLazyPagingItems()
                )
            }
        }
    }
}

