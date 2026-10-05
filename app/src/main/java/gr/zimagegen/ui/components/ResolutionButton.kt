package gr.zimagegen.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.model.ImageResolution

@Composable
fun ResolutionButton(
    resolution: ImageResolution,
    selected: Boolean,
    onClick: () -> Unit
) {
    val aspectRatio =
        resolution.width.toFloat() /
                resolution.height.toFloat()

    val maxIconWidth = 52.dp
    val maxIconHeight = 34.dp

    val iconWidth: Dp
    val iconHeight: Dp

    if (aspectRatio >= 1f) {
        iconWidth = maxIconWidth
        iconHeight = maxIconWidth / aspectRatio
    } else {
        iconHeight = maxIconHeight
        iconWidth = maxIconHeight * aspectRatio
    }

    val shape = RoundedCornerShape(12.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(48.dp)
                .clip(shape)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.90f)
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                        }
                    ),
                    shape = shape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(iconWidth)
                    .height(iconHeight)
                    .background(
                        color = if (selected) {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                        } else {
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                        },
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(resolution.labelRes),
            color = if (selected) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            },
            fontSize = 12.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )
    }
}