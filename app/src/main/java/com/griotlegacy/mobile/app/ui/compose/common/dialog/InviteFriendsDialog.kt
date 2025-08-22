package com.griotlegacy.mobile.app.ui.compose.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.ui.theme.Black
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans

@Composable
fun InviteFriendsDialog(
    onDismiss: () -> Unit,
    onSmsClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(50)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Invite Friends",
                    fontWeight = FontWeight.W400,
                    fontFamily = WorkSans,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedButton(
                        onClick = onSmsClick,
                        shape = buttonShape,
                        modifier = Modifier
                            .widthIn(min = 120.dp),

                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_icon),
                            contentDescription = "SMS",

                            )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SMS",
                            maxLines = 1,
                            fontFamily = WorkSans,
                            color = Black,
                            fontWeight = FontWeight.W500,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onEmailClick,
                        shape = buttonShape,
                        modifier = Modifier
                            .widthIn(min = 120.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_icon),
                            contentDescription = "SMS",

                            )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EMAIL",
                            maxLines = 1,
                            fontFamily = WorkSans,
                            color = White,
                            fontWeight = FontWeight.W500,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun InviteFriendsDialogPreview() {
    InviteFriendsDialog(
        onDismiss = { },
        onSmsClick = { /* handle SMS */ },
        onEmailClick = { /* handle Email */ }
    )

}