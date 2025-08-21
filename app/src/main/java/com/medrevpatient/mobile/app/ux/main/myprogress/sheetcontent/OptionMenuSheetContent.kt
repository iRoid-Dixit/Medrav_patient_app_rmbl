package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent

@Preview(showBackground = true)
@Composable
private fun OptionMenuSheetContentPreview() {
    OptionMenuSheetContent(event = {}, edit = {}, delete = {})
}

@Composable
fun OptionMenuSheetContent(
    event: SheetEvent,
    edit: () -> Unit,
    delete: () -> Unit,
    modifier: Modifier = Modifier
) {
    VStack(
        18.dp,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .height(108.dp)
    ) {

        val textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)

        IconTextHStack(
            icon = drawable.edit,
            tint = black25,
            style = textStyle,
            text = "Edit Goal",
            iconModifier = Modifier.size(24.dp),
            modifier = Modifier
                .clickable(
                    onClick = { edit() },
                    indication = null,
                    interactionSource = null
                )
        )

        IconTextHStack(
            icon = drawable.delete,
            tint = black25,
            style = textStyle,
            text = "Delete Goal",
            iconModifier = Modifier.size(24.dp),
            modifier = Modifier
                .clickable(
                    onClick = { delete() },
                    indication = null,
                    interactionSource = null
                )
        )

    }
}
