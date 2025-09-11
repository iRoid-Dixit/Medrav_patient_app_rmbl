package com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.*
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.sideEffect.SideEffectQuestion
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader


@ExperimentalMaterial3Api
@Composable
fun SideEffectScreen(
    navController: NavController,
    viewModel: SideEffectQuestionViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val sideEffectData by uiState.sideEffectQuestionDataFlow.collectAsState()
    uiState.event(SideEffectQuestionUiEvent.GetContext(context))
    val questionsList by uiState.questionList.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                onClick = { uiState.event(SideEffectQuestionUiEvent.BackClick) },
                isBackVisible = true,
                titleText = "Side Effect Check-In",
            )
        },
        navBarData = null
    ) {

        SideEffectScreenContent(
            selectedAnswers = sideEffectData?.selectedAnswers ?: emptyList(),
            onOptionSelected = { questionIndex, answerIndex ->
                uiState.event(SideEffectQuestionUiEvent.UpdateAnswer(questionIndex, answerIndex))
            },
            onSubmit = {
                uiState.event(SideEffectQuestionUiEvent.SubmitAssessment)
            },
            questionsList = questionsList,
            isLoading = sideEffectData?.showLoader == true
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SideEffectScreenContent(
    selectedAnswers: List<Int>,
    onOptionSelected: (Int, Int) -> Unit,
    onSubmit: () -> Unit,
    questionsList: List<SideEffectQuestion>,
    isLoading: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    LazyColumn(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { keyboardController?.hide() }
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(
            top = 25.dp,
            bottom = 30.dp
        )
    ) {
        itemsIndexed(questionsList) { index, question ->
            QuestionCard(
                selectedOption = if (index < selectedAnswers.size) selectedAnswers[index] else -1,
                onOptionSelected = { onOptionSelected(index, it) },
                question
            )
        }
        item {
            AppButtonComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                text = "Submit Assessment",
                isEnabled = selectedAnswers.all { it != -1 },
                isLoading = isLoading ,

                onClick = onSubmit

            )
        }
    }
}
@Composable
private fun QuestionCard(
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    question: SideEffectQuestion
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 15.dp)) {
            Text(
                text = "${question.id}. ${question.question}",
                fontSize = 16.sp,
                fontFamily = nunito_sans_600,
                color = Ebony,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            question.options.forEachIndexed { index, option ->
                OptionRow(
                    text = option.option,
                    isSelected = selectedOption == index,
                    onClick = { onOptionSelected(index) }
                )

            }
        }
    }

}

@Composable
private fun OptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (isSelected)Magnolia else White)
            .border(width = 1.dp, color = if (isSelected) AppThemeColor else SteelGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 15.dp, horizontal = 20.dp)
    ) {

        Image(painter = if (isSelected) painterResource(id = R.drawable.ic_check_mark) else painterResource(id = R.drawable.ic_uncheck), contentDescription = null)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = nunito_sans_600,
            color = SteelGray.copy(alpha = 0.8f),
        )
    }
}



/*@Preview
@Composable
private fun DietChallengeScreenPreview() {
    Surface {
        SideEffectScreenContent(
            questions = emptyList(),
            selectedAnswers = List(5) { -1 },
            onOptionSelected = { _, _ -> },
            onSubmit = {}
        )
    }
}*/
