package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun DietChallengeScreen(
    navController: NavController,
    viewModel: DietChallengeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "Daily Diet Challenge",
            )
        },
        navBarData = null
    ) {
        uiState.event(DietChallengeUiEvent.GetContext(context))
        DietChallengeContent(uiState = uiState, event = uiState.event)
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun DietChallengeContent(uiState: DietChallengeUiState, event: (DietChallengeUiEvent) -> Unit) {
    val dietChallengeData by uiState.dietChallengeDataFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }
    LazyColumn(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .noRippleClickable {
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 50.dp)
    ) {
        item {
            ProgressCard(
                correctAnswers = dietChallengeData?.correctAnswers ?: 0,
                incorrectAnswers = dietChallengeData?.incorrectAnswers ?: 0
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            FoodItemCard(
                foodItem = dietChallengeData?.currentFoodItem ?: FoodItem()
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            // Category Selection
            Text(
                text = "Choose the correct category:",
                fontFamily = nunito_sans_600,
                fontSize = 18.sp,
                color = SteelGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(foodCategories) { category ->
            CategoryButton(
                category = category,
                isSelected = selectedCategory == category,
                onCategorySelected = {
                    selectedCategory = category
                    event(DietChallengeUiEvent.SelectCategory(category.name))
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
        item {
            AppButtonComponent(
                onClick = {
                    event(DietChallengeUiEvent.ContinueChallenge)
                },
                modifier = Modifier.fillMaxWidth(),
                text = "Continue",
                isEnabled = selectedCategory != null
            )
        }
    }
}

@Composable
private fun ProgressCard(
    correctAnswers: Int,
    incorrectAnswers: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, // distribute evenly
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = correctAnswers.toString(),
                    fontFamily = nunito_sans_700,
                    color = AppThemeColor,
                    fontSize = 24.sp
                )
                Text(
                    text = "Correct",
                    fontFamily = nunito_sans_400,
                    color = Gray80,
                    fontSize = 12.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally // ✅ Center items
            ) {
                Text(
                    text = incorrectAnswers.toString(),
                    fontFamily = nunito_sans_700,
                    color = RedF7,
                    fontSize = 24.sp
                )
                Text(
                    text = "Incorrect",
                    fontFamily = nunito_sans_400,
                    color = Gray80,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
private fun FoodItemCard(
    foodItem: FoodItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Food Image Placeholder (you can replace with actual image)
            Image(painterResource(id = R.drawable.ic_bread), contentDescription = null)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = foodItem.name,
                fontFamily = nunito_sans_700,
                fontSize = 24.sp,
                color = SteelGray
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Choose the correct category:",
                fontFamily = nunito_sans_400,
                fontSize = 14.sp,
                color = Gray60
            )
        }
    }
}

@Composable
private fun CategoryButton(
    category: FoodCategory,
    isSelected: Boolean,
    onCategorySelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation =  if (isSelected)0.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCategorySelected() }
                .background(
                    color = if (isSelected) Magnolia else Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = if (isSelected) 0.dp else 2.dp,
                    color = if (isSelected) AppThemeColor else Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = category.icon),
                contentDescription = category.name,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(AppThemeColor)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontFamily = nunito_sans_400,
                fontSize = 14.sp,
                color = if (isSelected) AppThemeColor else PortGore
            )
        }
    }

}
data class FoodCategory(
    val name: String,
    val icon: Int
)
private val foodCategories = listOf(
    FoodCategory("Fiber with Low Absorption rate", R.drawable.ic_leaf),
    FoodCategory("Starchy with High Absorption rate", R.drawable.ic_leaf),
    FoodCategory("Starchy with Low Absorption rate", R.drawable.ic_leaf),
    FoodCategory("Meat with High Fat", R.drawable.ic_leaf),
    FoodCategory("Meat with Low Fat", R.drawable.ic_leaf),
    FoodCategory("Drinks with High Potassium", R.drawable.ic_leaf),
    FoodCategory("Drinks without Potassium", R.drawable.ic_leaf)
)

@Preview
@Composable
private fun DietChallengeScreenPreview() {
    val uiState = DietChallengeUiState()
    DietChallengeContent(uiState = uiState, event = {})
}