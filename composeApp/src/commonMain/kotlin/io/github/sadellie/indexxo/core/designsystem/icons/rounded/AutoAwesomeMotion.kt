package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.AutoAwesomeMotion: ImageVector
    get() {
        if (_AutoAwesomeMotion != null) {
            return _AutoAwesomeMotion!!
        }
        _AutoAwesomeMotion = ImageVector.Builder(
            name = "AutoAwesomeMotion",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveTo(480f, 880f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(400f, 800f)
                verticalLineToRelative(-320f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(480f, 400f)
                horizontalLineToRelative(320f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(880f, 480f)
                verticalLineToRelative(320f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(800f, 880f)
                lineTo(480f, 880f)
                close()
                moveTo(480f, 800f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(-320f)
                lineTo(480f, 480f)
                verticalLineToRelative(320f)
                close()
                moveTo(240f, 720f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(320f, 240f)
                horizontalLineToRelative(400f)
                verticalLineToRelative(80f)
                lineTo(320f, 320f)
                verticalLineToRelative(400f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(80f, 560f)
                verticalLineToRelative(-400f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(160f, 80f)
                horizontalLineToRelative(400f)
                verticalLineToRelative(80f)
                lineTo(160f, 160f)
                verticalLineToRelative(400f)
                lineTo(80f, 560f)
                close()
                moveTo(480f, 800f)
                verticalLineToRelative(-320f)
                verticalLineToRelative(320f)
                close()
            }
        }.build()

        return _AutoAwesomeMotion!!
    }

@Suppress("ObjectPropertyName")
private var _AutoAwesomeMotion: ImageVector? = null
