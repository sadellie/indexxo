package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.FindInPage: ImageVector
    get() {
        if (_FindInPage != null) {
            return _FindInPage!!
        }
        _FindInPage = ImageVector.Builder(
            name = "FindInPage",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 880f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(160f, 800f)
                verticalLineToRelative(-640f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(240f, 80f)
                horizontalLineToRelative(323f)
                quadToRelative(18f, 0f, 34f, 7.5f)
                reflectiveQuadToRelative(27f, 21.5f)
                lineToRelative(157f, 189f)
                quadToRelative(9f, 11f, 14f, 24f)
                reflectiveQuadToRelative(5f, 27f)
                verticalLineToRelative(451f)
                quadToRelative(0f, 27f, -24.5f, 37f)
                reflectiveQuadToRelative(-43.5f, -9f)
                lineTo(560f, 658f)
                quadToRelative(-17f, 11f, -37f, 16.5f)
                reflectiveQuadToRelative(-43f, 5.5f)
                quadToRelative(-66f, 0f, -113f, -47f)
                reflectiveQuadToRelative(-47f, -113f)
                quadToRelative(0f, -66f, 47f, -113f)
                reflectiveQuadToRelative(113f, -47f)
                quadToRelative(66f, 0f, 113f, 47f)
                reflectiveQuadToRelative(47f, 113f)
                quadToRelative(0f, 23f, -5.5f, 43f)
                reflectiveQuadTo(618f, 600f)
                lineToRelative(102f, 104f)
                verticalLineToRelative(-356f)
                lineTo(562f, 160f)
                lineTo(240f, 160f)
                verticalLineToRelative(640f)
                horizontalLineToRelative(333f)
                quadToRelative(20f, 0f, 30f, 12.5f)
                reflectiveQuadToRelative(10f, 27.5f)
                quadToRelative(0f, 15f, -10f, 27.5f)
                reflectiveQuadTo(573f, 880f)
                lineTo(240f, 880f)
                close()
                moveTo(480f, 600f)
                quadToRelative(33f, 0f, 56.5f, -23.5f)
                reflectiveQuadTo(560f, 520f)
                quadToRelative(0f, -33f, -23.5f, -56.5f)
                reflectiveQuadTo(480f, 440f)
                quadToRelative(-33f, 0f, -56.5f, 23.5f)
                reflectiveQuadTo(400f, 520f)
                quadToRelative(0f, 33f, 23.5f, 56.5f)
                reflectiveQuadTo(480f, 600f)
                close()
                moveTo(480f, 520f)
                close()
                moveTo(480f, 520f)
                close()
            }
        }.build()

        return _FindInPage!!
    }

@Suppress("ObjectPropertyName")
private var _FindInPage: ImageVector? = null
