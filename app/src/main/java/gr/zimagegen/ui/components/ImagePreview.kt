package gr.zimagegen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.R
import gr.zimagegen.ui.screens.ImageGenerationStatus

@Composable
fun ImagePreview(
    height: Dp,
    status: ImageGenerationStatus,
    imageUrl: String?,
    onBitmapLoaded: (android.graphics.Bitmap) -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            ImageGenerationStatus.PROMPTING -> {
                Text(
                    text = stringResource(R.string.generated_image_here),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            ImageGenerationStatus.GENERATING -> {
                SkeletonLoader()
            }

            ImageGenerationStatus.GENERATED -> {
                if (imageUrl != null) {
                    GeneratedImage(
                        imageUrl = imageUrl,
                        onBitmapLoaded = onBitmapLoaded
                    )
                } else {
                    Text(
                        text = stringResource(R.string.image_unavailable),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}