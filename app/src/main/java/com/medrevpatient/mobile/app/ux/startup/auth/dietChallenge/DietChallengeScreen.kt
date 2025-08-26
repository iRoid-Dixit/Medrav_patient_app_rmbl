package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                titleText = stringResource(R.string.daily_diet_challenge),
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
    
    var selectedCategory by remember { mutableStateOf<FoodCategory?>(null) }
    
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Progress Card
        ProgressCard(
            correctAnswers = dietChallengeData?.correctAnswers ?: 0,
            incorrectAnswers = dietChallengeData?.incorrectAnswers ?: 0
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Food Item Card
        FoodItemCard(
            foodItem = dietChallengeData?.currentFoodItem ?: FoodItem()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Category Selection
        Text(
            text = stringResource(R.string.choose_correct_category),
            fontFamily = nunito_sans_600,
            fontSize = 18.sp,
            color = Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Category List using LazyColumn
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(foodCategories) { category ->
                CategoryButton(
                    category = category,
                    isSelected = selectedCategory == category,
                    onCategorySelected = { 
                        selectedCategory = category
                        event(DietChallengeUiEvent.SelectCategory(category.name))
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Continue Button
        Button(
            onClick = { 
                event(DietChallengeUiEvent.ContinueChallenge)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppThemeColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = nunito_sans_600,
                fontSize = 16.sp,
                color = White
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$correctAnswers ${stringResource(R.string.correct)}",
                fontFamily = nunito_sans_600,
                fontSize = 16.sp,
                color = Black
            )
            Text(
                text = "$incorrectAnswers ${stringResource(R.string.incorrect)}",
                fontFamily = nunito_sans_600,
                fontSize = 16.sp,
                color = colorError
            )
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Gray5,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍞",
                    fontSize = 48.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = foodItem.name,
                fontFamily = nunito_sans_600,
                fontSize = 20.sp,
                color = Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.select_category_below),
                fontFamily = nunito_sans_400,
                fontSize = 14.sp,
                color = Gray94
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategorySelected() }
            .background(
                color = if (isSelected) AppThemeColor else White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else GrayC0,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = category.icon),
            contentDescription = category.name,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) White else AppThemeColor
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = category.name,
            fontFamily = nunito_sans_500,
            fontSize = 16.sp,
            color = if (isSelected) White else Black
        )
    }
}

// Data classes and sample data
data class FoodCategory(
    val name: String,
    val icon: Int
)

private val foodCategories = listOf(
    FoodCategory("Fiber with Low Absorption rate", R.drawable.ic_leaf),
    FoodCategory("Starchy with High Absorption rate", R.drawable.ic_bread),
    FoodCategory("Starchy with Low Absorption rate", R.drawable.ic_leaf),
    FoodCategory("Meat with High Fat", R.drawable.ic_chicken),
    FoodCategory("Meat with Low Fat", R.drawable.ic_fish),
    FoodCategory("Drinks with High Potassium", R.drawable.ic_drink_straw),
    FoodCategory("Drinks without Potassium", R.drawable.ic_drink)
)

@Preview
@Composable
private fun DietChallengeScreenPreview() {
    val uiState = DietChallengeUiState()
    DietChallengeContent(uiState = uiState, event = {})
}


