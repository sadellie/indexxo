package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.Checklist: ImageVector
    get() {
        if (_Checklist != null) {
            return _Checklist!!
        }
        _Checklist = ImageVector.Builder(
            name = "Checklist",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(221f, 647f)
                lineToRelative(142f, -142f)
                quadToRelative(12f, -12f, 28f, -11.5f)
                reflectiveQuadToRelative(28f, 12.5f)
                quadToRelative(11f, 12f, 11f, 28f)
                reflectiveQuadToRelative(-11f, 28f)
                lineTo(250f, 732f)
                quadToRelative(-12f, 12f, -28f, 12f)
                reflectiveQuadToRelative(-28f, -12f)
                lineToRelative(-86f, -86f)
                quadToRelative(-11f, -11f, -11f, -28f)
                reflectiveQuadToRelative(11f, -28f)
                quadToRelative(11f, -11f, 28f, -11f)
                reflectiveQuadToRelative(28f, 11f)
                lineToRelative(57f, 57f)
                close()
                moveTo(221f, 327f)
                lineTo(363f, 185f)
                quadToRelative(12f, -12f, 28f, -11.5f)
                reflectiveQuadToRelative(28f, 12.5f)
                quadToRelative(11f, 12f, 11f, 28f)
                reflectiveQuadToRelative(-11f, 28f)
                lineTo(250f, 412f)
                quadToRelative(-12f, 12f, -28f, 12f)
                reflectiveQuadToRelative(-28f, -12f)
                lineToRelative(-86f, -86f)
                quadToRelative(-11f, -11f, -11f, -28f)
                reflectiveQuadToRelative(11f, -28f)
                quadToRelative(11f, -11f, 28f, -11f)
                reflectiveQuadToRelative(28f, 11f)
                lineToRelative(57f, 57f)
                close()
                moveTo(560f, 680f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(520f, 640f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(560f, 600f)
                horizontalLineToRelative(280f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(880f, 640f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(840f, 680f)
                lineTo(560f, 680f)
                close()
                moveTo(560f, 360f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(520f, 320f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(560f, 280f)
                horizontalLineToRelative(280f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(880f, 320f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(840f, 360f)
                lineTo(560f, 360f)
                close()
            }
        }.build()

        return _Checklist!!
    }

@Suppress("ObjectPropertyName")
private var _Checklist: ImageVector? = null
