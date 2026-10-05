package gr.zimagegen.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background = Color(0xFF080A18),
    onBackground = Color.White,

    surface = Color(0xFF151827),
    onSurface = Color.White,

    primary = Color.White,
    onPrimary = Color.Black
)

private val LightColors = lightColorScheme(
    background = Color(0xFFE8EAF0),
    onBackground = Color(0xFF111318),

    surface = Color(0xFFF4F5F8),
    onSurface = Color(0xFF111318),

    primary = Color(0xFF111318),
    onPrimary = Color.White
)

@Composable
fun ZImageGenTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColors
        } else {
            LightColors
        },
        content = content
    )
}