package gr.zimagegen.model

import androidx.annotation.StringRes
import gr.zimagegen.R

enum class ImageResolution(
    val label: String,
    @get:StringRes val labelRes: Int,
    val width: Int,
    val height: Int
) {
    SQUARE(
        label = "Square",
        labelRes = R.string.resolution_square,
        width = 1024,
        height = 1024
    ),

    LANDSCAPE(
        label = "Landscape",
        labelRes = R.string.resolution_landscape,
        width = 1536,
        height = 1024
    ),

    PORTRAIT(
        label = "Portrait",
        labelRes = R.string.resolution_portrait,
        width = 1024,
        height = 1536
    )
}