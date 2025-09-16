package com.medrevpatient.mobile.app.ux

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import com.medrevpatient.mobile.app.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medrevpatient.mobile.app.data.source.local.UserData
import com.medrevpatient.mobile.app.data.source.local.UserData.medications
import com.medrevpatient.mobile.app.data.source.local.UserData.summary
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.Martinique50
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.ghostWhite
import com.medrevpatient.mobile.app.ui.theme.lavender
import com.medrevpatient.mobile.app.ui.theme.spaceCadetA60
import com.medrevpatient.mobile.app.ui.theme.violetsAreBlue
import com.medrevpatient.mobile.app.ui.theme.whiteBrush
import com.medrevpatient.mobile.app.ux.main.medication.MedicationViewModel

@ExperimentalMaterial3Api
@Composable
fun MedicationRemindersScreen(
    navController: NavController,
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                isBackVisible = true,
                onClick = {

                },
                titleText = "Medication Reminders",

                )
        },
        navBarData = null
    ) {
        MedicationRemindersContent()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun MedicationRemindersContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MedicationSummaryCard(summary = summary)
        }
        items(medications) { medication ->
            MedicationCard(medication = medication)
        }
        item {
            HelpfulTipCard()
        }
    }
}
@Preview
@Composable
fun MedicationSummaryCard(
    modifier: Modifier = Modifier,
    summary: UserData.MedicationSummary = UserData.summary,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ghostWhite),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Today, July 15",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${summary.totalMedications} medications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = spaceCadetA60
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserData.statusList.forEach { item ->
                    StatusIndicator(
                        item = item
                    )
                }
            }
        }
    }
}


@Composable
fun StatusIndicator(
    item: UserData.StatusIndicatorData,
    modifier: Modifier = Modifier
) {
    item.apply {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.horizontalGradient(listOf(color, color.copy(0.7f)))),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                )
            }
            Text(
                text = "$count $label",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = color
            )
        }
    }
}


@Preview
@Composable
fun MedicationCard(
    medication: UserData.Medication = medications[0],
    modifier: Modifier = Modifier
) {

    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, medication.status.textColor),
        onClick = { isExpanded = !isExpanded }
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left side - Icon and details
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Medication icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(medication.status.textColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(medication.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Medication details
                    Column(
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = medication.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${medication.dosage}, ${medication.frequency}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Right side - Status and time
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Status tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(medication.status.textColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = medication.status.displayText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = medication.status.textColor
                        )
                    }

                    // Time
                    Text(
                        text = medication.time,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = medication.status.textColor
                    )
                }
            }

            // Next dose info (if available)
            medication.nextDoseInfo?.let { nextDose ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                    )
                    Text(
                        text = nextDose,
                        style = MaterialTheme.typography.bodySmall,
                        color = Martinique50,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Action buttons (only for non-taken medications)
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    AppButtonComponent(
                        text = "Skip Dose",
                        textColor = spaceCadetA60,
                        contentPadding = PaddingValues(),
                        borderColors = LightGray,
                        backgroundBrush = whiteBrush,
                        modifier = Modifier
                            .weight(1f),
                        onClick = {

                        },
                    )

                    // Action button (Mark as Taken or Take Now)
                    AppButtonComponent(
                        text = "Due Soon",
                        contentPadding = PaddingValues(),
                        textColor = White,
                        borderColors = violetsAreBlue,
                        modifier = Modifier
                            .weight(1f),
                        onClick = {

                        },
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun HelpfulTipCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = lavender)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.ic_tip),
                contentDescription = null
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Helpful Tip",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SteelGray
                )
                Text(
                    text = "Tap when you take your medication to track your adherence and stay on top of your health goals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Martinique50
                )
            }
        }
    }
}

