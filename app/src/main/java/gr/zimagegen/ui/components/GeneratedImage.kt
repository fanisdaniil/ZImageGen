package gr.zimagegen.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import gr.zimagegen.R
import gr.zimagegen.media.ImageDownloader

@Composable
fun GeneratedImage(
    imageUrl: String,
    onBitmapLoaded: (android.graphics.Bitmap) -> Unit
) {
    var bitmap by remember(imageUrl) {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    LaunchedEffect(imageUrl) {
        val loaded = ImageDownloader.downloadBitmap(imageUrl)
        bitmap = loaded
        loaded?.let(onBitmapLoaded)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = stringResource(R.string.generated_image_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    } ?: Text(
        text = stringResource(R.string.loading_generated_image),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
        fontSize = 14.sp
    )
}