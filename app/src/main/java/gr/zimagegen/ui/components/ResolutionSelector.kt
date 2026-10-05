package gr.zimagegen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gr.zimagegen.model.ImageResolution

/**
 * Three visually consistent resolution controls.
 */
@Composable
fun ResolutionSelector(
    selectedResolution: ImageResolution,
    onResolutionSelected: (ImageResolution) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            20.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.Top
    ) {

        ImageResolution.entries.forEach { resolution ->

            ResolutionButton(
                resolution = resolution,
                selected = selectedResolution == resolution,
                onClick = {
                    onResolutionSelected(resolution)
                }
            )
        }
    }
}