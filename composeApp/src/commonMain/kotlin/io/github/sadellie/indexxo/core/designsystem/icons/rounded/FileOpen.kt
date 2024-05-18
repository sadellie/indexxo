package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.FileOpen: ImageVector
    get() {
        if (_FileOpen != null) {
            return _FileOpen!!
        }
        _FileOpen = ImageVector.Builder(
            name = "FileOpen",
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
                horizontalLineToRelative(287f)
                quadToRelative(16f, 0f, 30.5f, 6f)
                reflectiveQuadToRelative(25.5f, 17f)
                lineToRelative(194f, 194f)
                quadToRelative(11f, 11f, 17f, 25.5f)
                reflectiveQuadToRelative(6f, 30.5f)
                verticalLineToRelative(167f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(760f, 560f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(720f, 520f)
                verticalLineToRelative(-160f)
                lineTo(560f, 360f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(520f, 320f)
                verticalLineToRelative(-160f)
                lineTo(240f, 160f)
                verticalLineToRelative(640f)
                horizontalLineToRelative(320f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(600f, 840f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(560f, 880f)
                lineTo(240f, 880f)
                close()
                moveTo(760f, 777f)
                verticalLineToRelative(49f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(720f, 866f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(680f, 826f)
                verticalLineToRelative(-146f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(720f, 640f)
                horizontalLineToRelative(146f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(906f, 680f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(866f, 720f)
                horizontalLineToRelative(-50f)
                lineToRelative(90f, 90f)
                quadToRelative(11f, 11f, 11f, 27.5f)
                reflectiveQuadTo(906f, 866f)
                quadToRelative(-12f, 12f, -28.5f, 12f)
                reflectiveQuadTo(849f, 866f)
                lineToRelative(-89f, -89f)
                close()
                moveTo(240f, 800f)
                verticalLineToRelative(-640f)
                verticalLineToRelative(640f)
                close()
            }
        }.build()

        return _FileOpen!!
    }

@Suppress("ObjectPropertyName")
private var _FileOpen: ImageVector? = null
