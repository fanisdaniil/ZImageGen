package gr.zimagegen.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.zimagegen.R
import gr.zimagegen.media.ClipboardImage
import gr.zimagegen.media.ImageSaver
import kotlinx.coroutines.launch

@Composable
fun ImageActions(
    bitmap: android.graphics.Bitmap,
    onShowMessage: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val savedSuccessMsg = stringResource(R.string.image_saved_success)
    val saveFailedMsg = stringResource(R.string.image_save_failed)
    val copiedSuccessMsg = stringResource(R.string.image_copied_success)
    val copyFailedMsg = stringResource(R.string.image_copy_failed)

    fun notifyUser(message: String) {
        if (onShowMessage != null) {
            onShowMessage(message)
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Box {
        Button(
            onClick = {
                menuExpanded = true
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(
                text = stringResource(R.string.save) + " ▾",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.save_to_gallery))
                },
                onClick = {
                    menuExpanded = false
                    coroutineScope.launch {
                        val success = ImageSaver.saveBitmapToGallery(
                            context = context,
                            bitmap = bitmap
                        )
                        notifyUser(if (success) savedSuccessMsg else saveFailedMsg)
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.copy_to_clipboard))
                },
                onClick = {
                    menuExpanded = false
                    coroutineScope.launch {
                        val success = ClipboardImage.copyBitmap(
                            context = context,
                            bitmap = bitmap
                        )
                        notifyUser(if (success) copiedSuccessMsg else copyFailedMsg)
                    }
                }
            )
        }
    }
}