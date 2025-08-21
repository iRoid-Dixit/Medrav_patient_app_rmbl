package com.medrevpatient.mobile.app.ux.main.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.GoalLogs
import com.medrevpatient.mobile.app.domain.response.PopularItem
import com.medrevpatient.mobile.app.navigation.NavigationBar
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChart
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black75
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.whiteA4
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.component.HeaderContentWrapper
import com.medrevpatient.mobile.app.ux.main.component.RoundedImageWithRowDescription
import com.medrevpatient.mobile.app.ux.main.component.RoundedRectangularProgressComponent

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val event = viewModel::event
    var showNotificationDialog by remember { mutableStateOf(!areNotificationsEnabled(context)) }
    Scaffold(
        modifier = modifier.statusBarsPadding(),
        containerColor = Color.White,
        topBar = { ProfileTopBar(uiState = uiState, event = viewModel::event) },
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding()),
            uiState = uiState,
            event = viewModel::event,
            viewModel = viewModel
        )
    }
    if (!uiState.isCancelClickForNotificationPermission) {
        NotificationPermissionDialog(showDialog = showNotificationDialog, onDismiss = {
            showNotificationDialog = false
            event(HomeUIEvent.PerformNotificationPermissionCancelClick(true))
        })
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    event: (HomeUIEvent) -> Unit = {},
    viewModel: HomeViewModel
) {

    VStack(
        spaceBy = 20.dp,
        modifier
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(Modifier.padding(top = 8.dp))
        if (uiState.homeData.pinData != null) {
            HeaderBanner(
                type = uiState.homeData.pinData.type ?: 1,
                time = uiState.homeData.pinData.days.plus(" days"),
                calories = uiState.homeData.pinData.kcal.plus(" KCAL"),
                bannerUrl = uiState.homeData.pinData.image ?: "",
                title = uiState.homeData.pinData.name ?: "",
                onPinClick = {},
                onPlayClick = {},
                modifier = Modifier.padding(horizontal = 18.dp),
                event, uiState
            )
        }
        MyProgress(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .height(198.dp)
                .fillMaxWidth(),
            uiState = uiState
        )

        PopularWorkoutsAndRecipes(
            modifier = Modifier, uiState = uiState, event = event
        )
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.getUserData()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.callHomeApi()
    }
}


//@Preview(showBackground = true)
@Composable
private fun ProfileTopBar(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    event: (HomeUIEvent) -> Unit = {}
) {
    HStack(
        spaceBy = 8.dp,
        modifier = modifier
            .padding(top = 15.dp)
            .padding(horizontal = 18.dp)
            .height(56.dp)
            .fillMaxWidth()
            .noRippleClickable {
                event(HomeUIEvent.PerformProfileClick)
            }
    ) {

        AsyncImage(
            model = uiState.userData.profileImage,
            error = painterResource(drawable.ic_dummy_profile),
            placeholder = painterResource(drawable.ic_dummy_profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
        )

        Text(
            text = "Hey ${uiState.userData.firstName},\nLet’s begin your workout.",
            fontSize = 18.sp,
            fontFamily = outFit,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

//@Preview
@Composable
private fun MyProgress(
    modifier: Modifier = Modifier,
    uiState: HomeUiState
) {

    HeaderContentWrapper(
        "My Progress",
        wrapperPadding = 18.dp,
        spaceBy = 8.dp,
    ) {
        HStack(
            spaceBy = 18.dp,
            modifier = modifier.padding(top = 5.dp)
        ) {
            ProgressBars(modifier = Modifier.weight(1f), goalLogs = uiState.homeData.goalLogs)
            VStack(14.dp, modifier = Modifier.weight(1f)) {
                when {
                    uiState.homeData.specificGoals.isEmpty() -> {
                        RoundedRectangularProgressComponent(
                            progress = 0f,
                            modifier = Modifier.weight(1f),
                            description = stringResource(R.string.steps),
                            icon = drawable.steps
                        )
                        RoundedRectangularProgressComponent(
                            progress = 0f,
                            modifier = Modifier.weight(1f),
                            description = stringResource(R.string.workouts),
                            icon = drawable.exercise
                        )
                    }

                    uiState.homeData.specificGoals.size == 1 -> {
                        RoundedRectangularProgressComponent(
                            current = if (uiState.homeData.specificGoals[0].goalType == 3 || uiState.homeData.specificGoals[0].goalType == 6) AppUtils.convertSecondsToHoursMin(
                                uiState.homeData.specificGoals[0].totalLogValue ?: 0
                            ) else uiState.homeData.specificGoals[0].totalLogValue.toString(),
                            total = if (uiState.homeData.specificGoals[0].goalType == 3 || uiState.homeData.specificGoals[0].goalType == 6) AppUtils.convertSecondsToOnlyHours(
                                uiState.homeData.specificGoals[0].value ?: 0
                            ) else uiState.homeData.specificGoals[0].value.toString(),
                            progress = if (uiState.homeData.specificGoals[0].totalLogValue == 0L) 0f else (uiState.homeData.specificGoals[0].value?.toFloat()
                                ?.let {
                                    uiState.homeData.specificGoals[0].totalLogValue?.toFloat()
                                        ?.div(it)
                                } ?: 0f),
                            icon = Constants.Goals.getIconByType(
                                uiState.homeData.specificGoals[0].goalType ?: 0
                            ),
                            description = Constants.Goals.getNameByType(
                                uiState.homeData.specificGoals[0].goalType ?: 0
                            ),
                            modifier = Modifier.weight(1f),
                            textSize = if (uiState.homeData.specificGoals[0].goalType == 3 || uiState.homeData.specificGoals[0].goalType == 6) 10 else 14,
                            showTotalValue = !(uiState.homeData.specificGoals[0].goalType == 3 || uiState.homeData.specificGoals[0].goalType == 6)
                        )
                        RoundedRectangularProgressComponent(
                            progress = 0f,
                            modifier = Modifier.weight(1f),
                            description = stringResource(R.string.workouts),
                            icon = drawable.exercise
                        )
                    }

                    else -> {
                        uiState.homeData.specificGoals.forEach {
                            RoundedRectangularProgressComponent(
                                current = if (it.goalType == 3 || it.goalType == 6) AppUtils.convertSecondsToHoursMin(
                                    it.totalLogValue ?: 0
                                ) else it.totalLogValue.toString(),
                                total = if (it.goalType == 3 || it.goalType == 6) AppUtils.convertSecondsToOnlyHours(
                                    it.value ?: 0
                                ) else it.value.toString(),
                                progress = it.value?.toFloat()
                                    ?.let { it1 -> it.totalLogValue?.toFloat()?.div(it1) } ?: 0f,
                                icon = Constants.Goals.getIconByType(it.goalType ?: 0),
                                description = Constants.Goals.getNameByType(it.goalType ?: 0),
                                modifier = Modifier.weight(1f),
                                textSize = if (it.goalType == 3 || it.goalType == 6) 10 else 14,
                                showTotalValue = !(it.goalType == 3 || it.goalType == 6)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun ProgressBars(
    modifier: Modifier = Modifier,
    goalLogs: GoalLogs? = null
) {
    VStack(
        spaceBy = 0.dp,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .size(168.dp, 198.dp)
            .background(color = black25, shape = RoundedCornerShape(25.dp))
            .padding(12.dp)
    ) {
        val style = SpanStyle(
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = outFit,
            fontWeight = FontWeight.SemiBold,
        )

        if (goalLogs == null || goalLogs.chartData.all { it.value == 0 }) {
            Spacer(Modifier.weight(1f))
            NoProgress()
        } else {

            val chartData = goalLogs.toProgressBarChart()

            Text(
                text = chartData.item.map { it.month }.toSet().joinToString("-"),
                maxLines = 1,
                overflow = Ellipsis,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = whiteA4, shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )

            ProgressBarChart(
                progressBarChart = chartData.copy(monthStyle = TextStyle(fontSize = 0.sp)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }


        Spacer(Modifier.padding(vertical = 10.dp))

        HStack(8.dp, modifier = Modifier) {

            HStack(0.dp, Modifier.weight(1f)) {
                Image(
                    painter = painterResource(drawable.calorie),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null
                )
                Text(
                    text = goalLogs?.footer?.metric ?: stringResource(R.string.calories),
                    fontSize = 12.sp,
                    fontFamily = outFit,
                    color = Color.White,
                    maxLines = 1,
                    overflow = Ellipsis,
                )
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = style) {
                        append(if (goalLogs?.footer?.total == null) "0" else goalLogs.footer.total.toString())
                    }
                    withStyle(style = style.copy(fontSize = 6.sp)) {
                        append(if (goalLogs?.footer?.unit == null) "KCAL" else goalLogs.footer.unit.toString())
                    }
                },
                maxLines = 1,
                overflow = Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = whiteA4, shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .weight(1f)
            )
        }
    }
}


@Composable
private fun HeaderBanner(
    type: Int,// 1 : program, 2: Recipe
    time: String,
    calories: String,
    bannerUrl: String,
    title: String,
    onPinClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    event: (HomeUIEvent) -> Unit,
    uiState: HomeUiState
) {

    Box(modifier = modifier.noRippleClickable {
        //redirect to program or recipe screen
        event(HomeUIEvent.PerformPinDataClick(uiState.homeData.pinData?.id ?: ""))

    }) {
        AsyncImage(
            model = bannerUrl,
            error = painterResource(drawable.img_landscape_placeholder),
            placeholder = painterResource(drawable.img_landscape_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(25))
                .fillMaxWidth()
                .height(164.dp)
        )

        Box(
            modifier = Modifier
                .padding(18.dp)
                .matchParentSize()
        ) {
            Image(
                painter = painterResource(drawable.ic_pin_orange),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopStart)
                    .clickable { onPinClick() },
            )

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(25),
                modifier = Modifier
                    .size(172.dp, if (type == 1) 72.dp else 60.dp)
                    .align(Alignment.BottomStart)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),
                ) {

                    HStack(spaceBy = 8.dp) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontFamily = outFit,
                            color = black25,
                            maxLines = if (type == 2) 2 else 1,
                            overflow = Ellipsis,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )

                        if (type == 1) {
                            Image(
                                painter = painterResource(drawable.filled_play),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable(onClick = onPlayClick)
                            )
                        }
                    }
                    if (type == 1) {
                        HStack(0.dp, modifier = Modifier.weight(1f)) {

                            HStack(4.dp, modifier = Modifier.weight(0.5f)) {

                                Image(
                                    painter = painterResource(drawable.filled_time),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(16.dp)
                                        .weight(0.2f)
                                )

                                Text(
                                    text = time,
                                    fontSize = 14.sp,
                                    fontFamily = outFit,
                                    color = black25,
                                    maxLines = 1,
                                    overflow = Ellipsis,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(0.8f)
                                )
                            }

                            HStack(4.dp, modifier = Modifier.weight(0.5f)) {

                                Image(
                                    painter = painterResource(drawable.calorie),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(16.dp)
                                        .weight(0.2f)
                                )

                                Text(
                                    text = calories,
                                    fontSize = 12.sp,
                                    fontFamily = outFit,
                                    color = black25,
                                    maxLines = 1,
                                    overflow = Ellipsis,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


//@Preview
@Composable
private fun PopularWorkoutsAndRecipesItem(
    data: PopularItem,
    modifier: Modifier = Modifier,
    event: (HomeUIEvent) -> Unit
) {
    RoundedImageWithRowDescription(
        image = data.image ?: "",
        modifier = modifier
            .clip(RoundedCornerShape(18))
            .background(color = black25)
            .width(175.dp)
            .height(162.dp)
            .noRippleClickable {
                event(
                    HomeUIEvent.PerformPopularClick(
                        if (data.type == 1) data.programId ?: "" else data.id ?: "", data.type ?: 1
                    )
                )
            }
    ) {
        Text(
            text = data.name ?: "",
            maxLines = 2,
            minLines = 2,
            overflow = Ellipsis,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 7.dp)
                .align(Alignment.Center)
        )
    }
}


//@Preview
@Composable
private fun PopularWorkoutsAndRecipes(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    event: (HomeUIEvent) -> Unit
) {
    HeaderContentWrapper(
        stringResource(R.string.popular_workouts_and_recipes),
        wrapperPadding = 18.dp,
        modifier = modifier
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(
                horizontal = 18.dp,
                vertical = 8.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        ) {
            items(uiState.homeData.popularItems) { item ->
                PopularWorkoutsAndRecipesItem(data = item, event = event)
            }
        }
    }
}


//@Preview
@Composable
private fun NoProgress(
    title: String = "Let’s begin workout!",
    description: String = "No stats have been generated yet.",
    @DrawableRes icon: Int = drawable.grey_progress,
    modifier: Modifier = Modifier
) {

    VStack(
        spaceBy = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.W200,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = Ellipsis,
            color = Color.White
        )
    }
}

private fun areNotificationsEnabled(context: Context): Boolean {
    return when {
        !NotificationManagerCompat.from(context).areNotificationsEnabled() -> false
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            NotificationManagerCompat.from(context).notificationChannels.none { channel ->
                channel.importance == android.app.NotificationManager.IMPORTANCE_NONE
            }
        }

        else -> true
    }
}

fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
    }
    context.startActivity(intent)
}

@Composable
fun NotificationPermissionDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.enable_notifications),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    fontFamily = outFit
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.enable_notifications_desc),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    fontFamily = outFit,
                    color = black75
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    openNotificationSettings(context)
                    onDismiss()
                }) {
                    Text(
                        text = stringResource(R.string.open_setting),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = outFit
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = outFit
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 350)
@Composable
fun HomeScreenPreview(modifier: Modifier = Modifier) {
    MedrevPatientTheme {
        Surface(
            color = Color.White
        ) {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = Color.White,
                topBar = {
                    ProfileTopBar()
                },
                bottomBar = {
                    NavigationBar(rememberNavController(), onNavItemClicked = {})
                }
            ) { innerPadding ->
                HomeScreenContent(
                    modifier = modifier
                        .padding(innerPadding), viewModel = hiltViewModel()
                )
            }
        }
    }
}
