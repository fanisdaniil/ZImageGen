package gr.zimagegen.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import gr.zimagegen.R
import gr.zimagegen.model.CreditsUiState

@Composable
fun CreditsLabel(
    creditsState: CreditsUiState,
    modifier: Modifier = Modifier
) {
    val text = when (creditsState) {
        CreditsUiState.NotSupported -> return
        CreditsUiState.NotFetched -> stringResource(R.string.credits_not_loaded)
        CreditsUiState.Loading -> stringResource(R.string.loading_credits)
        is CreditsUiState.Available -> "${creditsState.amount} ${creditsState.unit}"
        CreditsUiState.Unavailable -> stringResource(R.string.credits_unavailable)
    }

    val alpha = when (creditsState) {
        is CreditsUiState.Available -> 0.75f
        else -> 0.50f
    }

    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
        fontSize = 14.sp
    )
}