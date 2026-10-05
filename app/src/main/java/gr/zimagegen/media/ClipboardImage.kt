package gr.zimagegen.media

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ClipboardImage {

    suspend fun copyBitmap(
        context: Context,
        bitmap: Bitmap
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(
                context.cacheDir,
                "clipboard_image.png"
            )

            file.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
                )
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            withContext(Dispatchers.Main) {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE)
                            as? ClipboardManager ?: return@withContext false

                val clip = ClipData.newUri(
                    context.contentResolver,
                    "Generated image",
                    uri
                )

                clipboard.setPrimaryClip(clip)
                true
            }
        } catch (_: Exception) {
            false
        }
    }
}