package gr.zimagegen.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import gr.zimagegen.R

@Composable
fun PromptInput(
    value: String,
    onValueChange: (String) -> Unit,
    showError: Boolean,
    onInteraction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) {
                    onInteraction()
                }
            },
        label = {
            Text(
                text = stringResource(R.string.describe_image)
            )
        },
        placeholder = {
            Text(
                text = "A woman walking through Paris at golden hour..."
            )
        },
        minLines = 3,
        maxLines = 4,
        isError = showError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor =
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (showError) 0.05f else 0.08f
                ),

            unfocusedContainerColor =
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (showError) 0.05f else 0.08f
                ),

            focusedTextColor =
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.90f),

            unfocusedTextColor =
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),

            focusedBorderColor =
                if (showError) {
                    Color(0xFFFF6B6B)
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                },

            unfocusedBorderColor =
                if (showError) {
                    Color(0xFFFF6B6B)
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                },

            errorBorderColor = Color(0xFFFF6B6B),

            cursorColor =
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),

            focusedLabelColor =
                if (showError) {
                    Color(0xFFFF8585)
                } else {
                    MaterialTheme.colorScheme.onBackground
                },

            unfocusedLabelColor =
                if (showError) {
                    Color(0xFFFF8585)
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
                },

            errorLabelColor = Color(0xFFFF8585),

            focusedPlaceholderColor =
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),

            unfocusedPlaceholderColor =
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
        )
    )
}