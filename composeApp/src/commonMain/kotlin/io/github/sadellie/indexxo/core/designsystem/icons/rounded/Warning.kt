package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.Warning: ImageVector
    get() {
        if (_Warning != null) {
            return _Warning!!
        }
        _Warning = ImageVector.Builder(
            name = "Warning",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(109f, 840f)
                quadToRelative(-11f, 0f, -20f, -5.5f)
                reflectiveQuadTo(75f, 820f)
                quadToRelative(-5f, -9f, -5.5f, -19.5f)
                reflectiveQuadTo(75f, 780f)
                lineToRelative(370f, -640f)
                quadToRelative(6f, -10f, 15.5f, -15f)
                reflectiveQuadToRelative(19.5f, -5f)
                quadToRelative(10f, 0f, 19.5f, 5f)
                reflectiveQuadToRelative(15.5f, 15f)
                lineToRelative(370f, 640f)
                quadToRelative(6f, 10f, 5.5f, 20.5f)
                reflectiveQuadTo(885f, 820f)
                quadToRelative(-5f, 9f, -14f, 14.5f)
                reflectiveQuadToRelative(-20f, 5.5f)
                lineTo(109f, 840f)
                close()
                moveTo(178f, 760f)
                horizontalLineToRelative(604f)
                lineTo(480f, 240f)
                lineTo(178f, 760f)
                close()
                moveTo(480f, 720f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(520f, 680f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(480f, 640f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(440f, 680f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(480f, 720f)
                close()
                moveTo(480f, 600f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(520f, 560f)
                verticalLineToRelative(-120f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(480f, 400f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(440f, 440f)
                verticalLineToRelative(120f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(480f, 600f)
                close()
                moveTo(480f, 500f)
                close()
            }
        }.build()

        return _Warning!!
    }

@Suppress("ObjectPropertyName")
private var _Warning: ImageVector? = null
