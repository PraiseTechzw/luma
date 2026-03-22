package app.lumalabs.luma.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import app.lumalabs.luma.ui.theme.DarkBackground
import app.lumalabs.luma.ui.theme.PrimaryAccent

@Composable
fun LumaBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PrimaryAccent.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.2f),
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.2f, size.height * 0.2f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00C853).copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.6f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.8f, size.height * 0.6f)
            )
        }
        content()
    }
}
