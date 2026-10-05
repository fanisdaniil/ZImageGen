package gr.zimagegen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.R

@Composable
fun AddModelButton(
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val addDesc = stringResource(R.string.add_model)

    Box(
        modifier = Modifier
            .padding(end = 8.dp, top = 8.dp)
            .size(48.dp)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                shape = shape
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics {
                contentDescription = addDesc
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            fontSize = 30.sp
        )
    }
}