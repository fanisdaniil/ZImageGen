package gr.zimagegen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (isDark) {
        Color(0xFF080A18)
    } else {
        Color(0xFFE8EAF0)
    }

    val purple = if (isDark) {
        Color(0xFF4035FF)
    } else {
        Color(0xFF7C6FFF)
    }

    val cyan = if (isDark) {
        Color(0xFF00B8D9)
    } else {
        Color(0xFF5CCFE3)
    }

    val pink = if (isDark) {
        Color(0xFFD946EF)
    } else {
        Color(0xFFE78BEF)
    }

    Canvas(
        modifier = modifier
    ) {
        drawRect(
            color = backgroundColor
        )

        drawAuroraBlob(
            x = 0.20f,
            y = 0.20f,
            width = 0.85f,
            height = 1.20f,
            color = purple
        )

        drawAuroraBlob(
            x = 0.80f,
            y = 0.35f,
            width = 1.00f,
            height = 0.70f,
            color = cyan
        )

        drawAuroraBlob(
            x = 0.45f,
            y = 0.85f,
            width = 1.10f,
            height = 0.70f,
            color = pink
        )
    }
}

private fun DrawScope.drawAuroraBlob(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Color
) {
    drawOval(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to color.copy(alpha = 0.55f),
                0.45f to color.copy(alpha = 0.40f),
                0.75f to color.copy(alpha = 0.25f),
                0.92f to color.copy(alpha = 0.16f),
                1.0f to color.copy(alpha = 0.10f)
            )
        ),
        topLeft = Offset(
            x = size.width * x - size.width * width / 2,
            y = size.height * y - size.height * height / 2
        ),
        size = Size(
            width = size.width * width,
            height = size.height * height
        )
    )
}