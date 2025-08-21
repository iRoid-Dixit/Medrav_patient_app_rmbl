package com.medrevpatient.mobile.app.ux.main.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.azureishWhite
import com.medrevpatient.mobile.app.ui.theme.white


@Preview(showBackground = true)
@Composable
private fun NotePreview() {
    NotesItem(
        modifier = Modifier,
        title = "Recipe",
        body = "Description".repeat(90),
        onClick = {}
    )
}


@Composable
fun NotesItem(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    VStack(
        spaceBy = 12.dp,
        horizontalAlignment = Alignment.Start, modifier = modifier
            .clip(RoundedCornerShape(25))
            .background(
                brush = Brush.radialGradient(
                    center = Offset.Zero,
                    radius = 1000f,
                    colors = listOf(
                        white,
                        azureishWhite,
                    )
                )
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        //Title
        HStack(8.dp) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_notes),
                contentDescription = null
            )

            Text(
                title, style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = Bold,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = body,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = W300
            ),
            maxLines = 8,
            overflow = TextOverflow.Ellipsis
        )

    }
}