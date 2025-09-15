package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray5
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import kotlin.Boolean

@Composable
fun DropdownField(
    list: List<String>,
    expanded: Boolean,
    selectedCategory: String,
    onRoleDropDownExpanded: (Boolean) -> Unit = {},
    onUserRoleValue: (String) -> Unit = {},
    errorMessage: String? = null,
    placeholder: String? = "Select Gender",
    isTitleVisible: Boolean = false,
    title: String = "",
    backGroundColor: Color = Gray5,
    valueTextColor: Color = Black,
    borderColors: Color = Color.Transparent,
) {
    val dropdownIcon = if (expanded) R.drawable.ic_drop_up else R.drawable.ic_drow_down
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        isFocused -> White
        else -> Gray2F
    }

    Column {
        if (isTitleVisible) {
            Text(
                text = title, fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = backGroundColor, shape = RoundedCornerShape(15))
                .fillMaxWidth()
                .clickable {
                    onRoleDropDownExpanded(!expanded)
                }
                .heightIn(50.dp)
                .border(width = 1.dp, color = borderColors, shape = RoundedCornerShape(15))

                .clip(RoundedCornerShape(15))
        ) {
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = selectedCategory.ifBlank { placeholder ?: "" },
                fontSize = 16.sp,
                fontFamily = nunito_sans_600,
                color = if (selectedCategory.isNotBlank()) valueTextColor else Gray50,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = dropdownIcon),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(15.dp))
        }
        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = nunito_sans_600,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(10.dp))
            ) {
                list.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(White)
                            .border(width = 1.dp, color = SteelGray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp))
                            .clickable {
                                onUserRoleValue(item)
                                onRoleDropDownExpanded(false)
                            }
                            .padding(vertical = 14.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = item,
                            fontSize = 16.sp,
                            fontWeight = W500,
                            fontFamily = WorkSans,
                            color = Black
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RoleDropdownFieldPreview() {
    var expanded by remember { mutableStateOf(false) }
    DropdownField(
        list = listOf(),
        expanded = expanded,
        selectedCategory = "",
        onRoleDropDownExpanded = {
            expanded = it
        },
        onUserRoleValue = {
            expanded = true
        },
    )
}
