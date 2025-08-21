package com.medrevpatient.mobile.app.ux.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.theme.black25


@Preview(showBackground = true)
@Composable
private fun IconBackgroundMakerPreview() {
    IconBackgroundMaker(
        icon = drawable.edit,
        modifier = Modifier.padding(8.dp)
    ) { }
}


@Composable
fun IconBackgroundMaker(
    @DrawableRes icon: Int,
    tint: Color = black25,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    IconButton(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(25))
                .background(
                    color = tint.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(25)
                ).size(32.dp)
        )
    ) {

        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.fillMaxSize().padding(8.dp)
        )
    }
}
