package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.FrameInspect: ImageVector
    get() {
        if (_FrameInspect != null) {
            return _FrameInspect!!
        }
        _FrameInspect = ImageVector.Builder(
            name = "FrameInspect",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveTo(450f, 540f)
                quadToRelative(38f, 0f, 64f, -26f)
                reflectiveQuadToRelative(26f, -64f)
                quadToRelative(0f, -38f, -26f, -64f)
                reflectiveQuadToRelative(-64f, -26f)
                quadToRelative(-38f, 0f, -64f, 26f)
                reflectiveQuadToRelative(-26f, 64f)
                quadToRelative(0f, 38f, 26f, 64f)
                reflectiveQuadToRelative(64f, 26f)
                close()
                moveTo(643f, 700f)
                lineTo(538f, 595f)
                quadToRelative(-20f, 13f, -42.5f, 19f)
                reflectiveQuadToRelative(-45.5f, 6f)
                quadToRelative(-71f, 0f, -120.5f, -49.5f)
                reflectiveQuadTo(280f, 450f)
                quadToRelative(0f, -71f, 49.5f, -120.5f)
                reflectiveQuadTo(450f, 280f)
                quadToRelative(71f, 0f, 120.5f, 49.5f)
                reflectiveQuadTo(620f, 450f)
                quadToRelative(0f, 23f, -6.5f, 45.5f)
                reflectiveQuadTo(594f, 538f)
                lineToRelative(106f, 106f)
                lineToRelative(-57f, 56f)
                close()
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                lineTo(200f, 840f)
                close()
                moveTo(600f, 840f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                lineTo(600f, 840f)
                close()
                moveTo(120f, 360f)
                verticalLineToRelative(-160f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                lineTo(200f, 200f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(760f, 360f)
                verticalLineToRelative(-160f)
                lineTo(600f, 200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
            }
        }.build()

        return _FrameInspect!!
    }

@Suppress("ObjectPropertyName")
private var _FrameInspect: ImageVector? = null
