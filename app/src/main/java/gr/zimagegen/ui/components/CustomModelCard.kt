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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.R
import gr.zimagegen.model.CustomImageModel

@Composable
fun CustomModelCard(
    model: CustomImageModel,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(24.dp)

    val backgroundColor =
        if (selected) {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f)
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
        }

    val borderColor =
        if (selected) {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f)
        }

    Box(
        modifier = Modifier
            .height(280.dp)
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = model.name,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.85f
                    ),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (onDelete != null) {
                    CustomModelMenu(onDelete = onDelete)
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(R.string.custom_api),
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.55f
                ),
                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = model.apiUrl,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.55f
                ),
                fontSize = 12.sp,
                maxLines = 2
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                model.pricePerRequest?.let { price ->
                    Text(
                        text = stringResource(R.string.price_per_request_format, price),
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.55f
                        ),
                        fontSize = 13.sp
                    )
                } ?: Spacer(modifier = Modifier.size(1.dp))

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 1.5.dp,
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                                },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    Color.Transparent
                                },
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomModelMenu(
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = "⋮",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            fontSize = 24.sp,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 4.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.delete))
                },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}