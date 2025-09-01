package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun DropdownField(
    list: List<String>,
    expanded: Boolean,
    selectedRole: String,
    onRoleDropDownExpanded: (Boolean) -> Unit = {},
    onUserRoleValue: (String) -> Unit = {},
    errorMessage: String? = null,
    placeholder: String? = "Select Gender"
) {
    val dropdownIcon = if (expanded) R.drawable.ic_app_icon else R.drawable.ic_app_icon
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        isFocused -> White
        else -> Gray2F
    }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(color = Gray2F, shape = RoundedCornerShape(10))
                    .fillMaxWidth()
                    .clickable {
                        onRoleDropDownExpanded(!expanded)
                    }
                    .heightIn(56.dp)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(10)
                    )
                    .clip(RoundedCornerShape(10))
            ) {
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = selectedRole.ifBlank { placeholder ?: "" },
                    fontSize = 14.sp,
                    fontWeight = W500,
                    fontFamily = WorkSans,
                    color = if (selectedRole.isNotEmpty()) White else White50,
                    modifier = Modifier.weight(1f)
                )
               /* Image(
                    painter = painterResource(id = dropdownIcon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (selectedRole.isNotEmpty()) White else White50),
                )*/
                Spacer(modifier = Modifier.width(15.dp))
            }
            if (errorMessage?.isNotEmpty() == true) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp)
                )
            }
            if (expanded){
                Spacer(modifier = Modifier.height(8.dp))
            }
            AnimatedVisibility(visible = expanded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, White, RoundedCornerShape(8.dp)),
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        list.forEachIndexed { index, item ->
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                fontWeight = W500,
                                fontFamily = WorkSans,
                                color = Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUserRoleValue(item)
                                        onRoleDropDownExpanded(false)
                                    }
                                    .padding(
                                        top = if (index == 0) 20.dp else 10.dp,
                                        bottom = if (index == list.lastIndex) 20.dp else 10.dp,
                                        start = 22.dp,
                                        end = 20.dp
                                    )
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
        selectedRole = "",
        onRoleDropDownExpanded = {
            expanded = it
        },
        onUserRoleValue = {
            expanded = true
        },
        )
}
