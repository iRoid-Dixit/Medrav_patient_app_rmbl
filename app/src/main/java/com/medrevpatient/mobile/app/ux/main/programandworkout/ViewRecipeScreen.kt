package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipe
import com.medrevpatient.mobile.app.ui.FullSizeCenterBox
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black94
import com.medrevpatient.mobile.app.ui.theme.orangeYellow
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.alias.drawable
import kotlin.text.Typography.bullet

@Preview
@Composable
private fun ViewRecipeScreenPreview(
    modifier: Modifier = Modifier
) {
    ViewRecipeScreenContent(
        isLoading = false,
        recipe = Recipe(),
        isPin = false,
        event = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRecipeScreen(
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scaffoldState = rememberBottomSheetScaffoldState()

    NetworkResultHandler(
        networkResult = uiState.recipe,
        onError = { message ->
            FullSizeCenterBox {
                TopBarCenterAlignTextAndBack(
                    "View Recipe",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    onBackPress = { viewModel.popBackStack() }
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        onRetry = {
            viewModel.event(RecipesUiEvent.RefreshRecipe)
        }
    ) { response ->

        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            sheetPeekHeight = 400.dp,
            sheetTonalElevation = 12.dp,
            containerColor = Color.Transparent,
            sheetContainerColor = Color.White,
            sheetDragHandle = {},
            sheetSwipeEnabled = true,
            sheetShadowElevation = 12.dp,
            sheetContent = {
                ViewRecipeScreenContent(
                    isLoading = uiState.isLoading,
                    recipe = response.data ?: Recipe(),
                    event = viewModel::event,
                    isPin = uiState.isPin,
                    modifier = Modifier.background(Color.White)
                )
            }) { innerPadding ->
            RecipeScreenBackGroundContent(
                image = response.data?.image ?: "",
                modifier = Modifier
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding() - 30.dp
                    )
            ) {
                viewModel.popBackStack()
            }
        }
    }
}

@Composable
private fun ViewRecipeScreenContent(
    isLoading: Boolean,
    recipe: Recipe,
    isPin: Boolean,
    event: RecipesEvents,
    modifier: Modifier = Modifier
) {
    recipe.apply {

        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                HStack(
                    spaceBy = 8.dp,
                    verticalAlignment = Alignment.Top
                ) {
                    VStack(
                        spaceBy = 14.dp,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = name,
                            style = TextStyle(
                                color = black25,
                                fontSize = 18.sp,
                                fontFamily = outFit,
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = description,
                            style = TextStyle(
                                color = black25,
                                fontSize = 12.sp,
                                fontFamily = outFit,
                                fontWeight = FontWeight.W300
                            ),
                        )

                        Info(
                            recipe = recipe,
                            modifier = Modifier.fillMaxWidth()
                        )

                    }

                    val pinToggle =
                        if (isPin) Triple(
                            drawable.pin__filled_,
                            "Pinned",
                            orangeYellow
                        ) else Triple(drawable.pin__outline_, "Un-Pinned", black25)

                    IconButton(
                        onClick = {
                            if (isLoading)
                                return@IconButton

                            event(RecipesUiEvent.ViewRecipeUiEvent.TogglePinProgram(id))
                        },
                        modifier = Modifier
                            .padding(4.dp),
                    ) {

                        Icon(
                            imageVector = ImageVector.vectorResource(pinToggle.first),
                            contentDescription = pinToggle.second,
                            tint = pinToggle.third
                        )
                    }
                }

                Spacer(Modifier.padding(12.dp))

                Text(
                    "Ingredients",
                    style = TextStyle(
                        color = black25,
                        fontSize = 18.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }


            items(ingredients) { data ->
                val bulletTextStyle = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = outFit,
                    color = black94,
                    lineHeight = 2.sp
                )
                HStack(8.dp) {
                    Text(
                        text = "$bullet ${data.item}",
                        style = bulletTextStyle,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${data.quantity} ${data.unit}",
                        style = bulletTextStyle,
                    )
                }
            }
        }
    }
}


@Composable
private fun Info(
    recipe: Recipe,
    modifier: Modifier = Modifier
) {

    recipe.apply {

        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
        ) {
            HStack(2.dp) {

                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(9.dp, 12.dp)
                            .background(
                                color = if (index > difficultyLevel - 1) black25.copy(alpha = 0.2f) else black25,
                                shape = RoundedCornerShape(48)
                            )
                    )
                }

                Spacer(Modifier.padding(1.dp))

                Text(
                    text = difficultyLevelLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.padding(5.dp))

            HStack(4.dp) {

                Icon(
                    painter = painterResource(drawable.carbohydrate),
                    contentDescription = null
                )

                Text(
                    text = nutritionalCategory.joinToString { it },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }

        }
    }

}


@Composable
private fun RecipeScreenBackGroundContent(
    image: String,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit
) {

    Box(
        modifier = modifier

    ) {
        AsyncImage(
            model = Uri.parse(image),
            placeholder = painterResource(R.drawable.img_portrait_placeholder_transparent),
            error = painterResource(R.drawable.img_portrait_placeholder_transparent),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(200.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )


        TopBarCenterAlignTextAndBack(
            "",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding(),
            onBackPress = onBackPress
        )
    }
}