package com.medrevpatient.mobile.app.ux.startup.auth.bmi

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun BmiScreen(
    navController: NavController,
    viewModel: BmiViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check",

                )
        },
        navBarData = null
    ) {
        uiState.event(BmiUiEvent.GetContext(context))
        BmiScreenContent(uiState = uiState, event = uiState.event)
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun BmiScreenContent(uiState: BmiUiState, event: (BmiUiEvent) -> Unit) {
    val bmiUiState by uiState.bmiDataFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable{
                keyboardController?.hide()
            }
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueChalk, shape = RoundedCornerShape(20.dp))
                .clip(shape = RoundedCornerShape(20.dp))
                .padding(vertical = 30.dp, horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.ic_bmi), contentDescription = null)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Calculate Your BMI",
                color = SteelGray,
                fontSize = 20.sp,
                fontFamily = nunito_sans_700,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your height and weight to\ncheck eligibility",
                color = Gray80,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontFamily = nunito_sans_400,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            HeightWeightInput(
                label = "Height (cm)",
                value = bmiUiState?.heightInput ?: "",
                onValueChange = { event(BmiUiEvent.HeightValueChange(it)) },
                placeholder = "E.g., 175",
                errorMessage = bmiUiState?.heightErrorFlow
            )
            Spacer(modifier = Modifier.height(20.dp))
            HeightWeightInput(
                label = "Weight (kg)",
                value = bmiUiState?.weightInput ?: "",
                onValueChange = { event(BmiUiEvent.WeightValueChange(it)) },
                placeholder = "E.g., 80",
                errorMessage = bmiUiState?.weightErrorFlow
            )
            Spacer(modifier = Modifier.height(32.dp))
            AppButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                text = "Calculate BMI",
                onClick = {
                    uiState.event(BmiUiEvent.CalculateBmi)
                },

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeightWeightInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Gray20, shape = RoundedCornerShape(5.dp))
                .clip(shape = RoundedCornerShape(5.dp))
                .background(White)
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = SteelGray,
                fontFamily = nunito_sans_600,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = Gray20,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clip(RoundedCornerShape(5.dp))
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = nunito_sans_400,
                        fontSize = 14.sp,
                        color = SteelGray,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth(),
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                ) {
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = it,
                        singleLine = true,
                        enabled = true,
                        visualTransformation = VisualTransformation.None,
                        placeholder = {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontFamily = nunito_sans_400,
                                    fontSize = 16.sp,
                                    color = Gray40,
                                )
                            }
                        },
                        interactionSource = interactionSource,
                        contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                            top = 10.dp, bottom = 10.dp, end = 15.dp, start = 15.dp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Transparent,
                            unfocusedIndicatorColor = Transparent,
                            focusedLabelColor = AppThemeColor,
                            unfocusedLabelColor = GrayC0,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color(0xFF7A42F4),
                            errorIndicatorColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            disabledTextColor = Gray40,
                            disabledIndicatorColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                    )
                }
            }
        }
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = nunito_sans_600,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .padding(start = 15.dp, top = 6.dp)
                    .align(Alignment.Start)
            )
        }
    }
}


@Preview
@Composable
private fun BmiScreenPreview() {
    val uiState = BmiUiState()
    Surface {
        BmiScreenContent(uiState = uiState, event = {})
    }
}
