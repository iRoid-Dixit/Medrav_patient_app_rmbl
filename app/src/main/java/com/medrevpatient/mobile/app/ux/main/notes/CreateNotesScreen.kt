package com.medrevpatient.mobile.app.ux.main.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.azureishWhite
import com.medrevpatient.mobile.app.ui.theme.black
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeEvent
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeUiEvent
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeUiState
import com.medrevpatient.mobile.app.ux.main.programandworkout.ForMeViewModel

@Composable
fun CreateNotesScreen(
    viewModel: ForMeViewModel,
    modifier: Modifier = Modifier
) {


    val uiState by viewModel.forMe.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "Notes",
                onBackPress = { viewModel.popBackStack() },
                onTrailingPress = {
                    viewModel.event(ForMeUiEvent.Notes.Delete)
                },
                trailingIcon = drawable.ic_delete_with_bg
            )
        },
        containerColor = Color.White,
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->

        CreateNoteContent(
            uiState = uiState,
            event = viewModel::event,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        )

    }
}


@Composable
private fun CreateNoteContent(
    uiState: ForMeUiState,
    event: ForMeEvent,
    modifier: Modifier = Modifier,
) {

    VStack(
        spaceBy = 12.dp,
        modifier = modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp)
    ) {
        VStack(
            spaceBy = 0.dp, modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12))
                .background(
                    brush = Brush.radialGradient(
                        radius = 1000f,
                        center = Offset.Zero,
                        colors = listOf(
                            white, azureishWhite
                        )
                    )
                )
                .padding(vertical = 18.dp)
        ) {
            HStack(
                spaceBy = 0.dp,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(drawable.ic_notes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                TextField(
                    value = uiState.title,
                    onValueChange = {
                        if (it.length <= 80) event(
                            ForMeUiEvent.Notes.EditNote(
                                it,
                                uiState.body
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    placeholder = {
                        Text(
                            text = "Title (max 80 char)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = black25.copy(
                                    alpha = .5f
                                )
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = black25,
                        cursorColor = black,
                        disabledIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        textSelectionColors = TextSelectionColors(
                            handleColor = black25,
                            backgroundColor = black25.copy(alpha = .1f)
                        )
                    )
                )
            }

            TextField(
                value = uiState.body,
                onValueChange = {
                    if (it.length <= 500) event(ForMeUiEvent.Notes.EditNote(uiState.title, it))
                },
                placeholder = {
                    Text(
                        text = "Write Notes (max 500 char)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = black25.copy(alpha = .5f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = black25,
                    cursorColor = black,
                    disabledIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    textSelectionColors = TextSelectionColors(
                        handleColor = black25,
                        backgroundColor = black25.copy(alpha = .1f)
                    )
                )
            )

            Text(
                text = uiState.note.formattedDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleSmall.copy(color = grey94)
            )
        }

        Spacer(Modifier.padding(9.dp))

        HStack(
            spaceBy = 18.dp,
        ) {
            SkaiButton(
                text = "cancel",
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = {
                    event(ForMeUiEvent.Notes.Cancel)
                }
            )

            SkaiButton(
                text = "save",
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                isLoading = uiState.isLoading,
                enable = uiState.isEnable,
                modifier = Modifier.weight(1f),
                onClick = {
                    event(ForMeUiEvent.Notes.Save)
                }
            )
        }

        Spacer(Modifier.padding(9.dp))

    }
}
