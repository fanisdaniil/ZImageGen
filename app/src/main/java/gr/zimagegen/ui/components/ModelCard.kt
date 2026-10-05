package gr.zimagegen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.model.CreditsUiState
import gr.zimagegen.model.ImageModel

@Composable
fun ModelCard(
    imageModel: ImageModel,
    selected: Boolean,
    creditsState: CreditsUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)

    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    }

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
    }

    Box(
        modifier = modifier
            .height(280.dp)
            .background(
                color = backgroundColor,
                shape = cardShape
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = cardShape
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = imageModel.brand,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-1.5).sp
            )

            if (imageModel.modelName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = imageModel.modelName,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.8).sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = imageModel.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CreditsLabel(
                    creditsState = creditsState
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 1.5.dp,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                androidx.compose.ui.graphics.Color.Transparent
                            },
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            color = MaterialTheme.colorScheme.surface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}