package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.Compare: ImageVector
    get() {
        if (_Compare != null) {
            return _Compare!!
        }
        _Compare = ImageVector.Builder(
            name = "Compare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(400f, 840f)
                lineTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-40f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(440f, 40f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(480f, 80f)
                verticalLineToRelative(800f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(440f, 920f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(400f, 880f)
                verticalLineToRelative(-40f)
                close()
                moveTo(200f, 720f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-240f)
                lineTo(200f, 720f)
                close()
                moveTo(560f, 840f)
                verticalLineToRelative(-360f)
                lineToRelative(200f, 240f)
                verticalLineToRelative(-520f)
                lineTo(560f, 200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(560f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                lineTo(560f, 840f)
                close()
            }
        }.build()

        return _Compare!!
    }

@Suppress("ObjectPropertyName")
private var _Compare: ImageVector? = null
