package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.GridView: ImageVector
    get() {
        if (_GridView != null) {
            return _GridView!!
        }
        _GridView = ImageVector.Builder(
            name = "GridView",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveTo(120f, 440f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                lineTo(120f, 440f)
                close()
                moveTo(120f, 840f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                lineTo(120f, 840f)
                close()
                moveTo(520f, 440f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                lineTo(520f, 440f)
                close()
                moveTo(520f, 840f)
                verticalLineToRelative(-320f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(320f)
                lineTo(520f, 840f)
                close()
                moveTo(200f, 360f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                lineTo(200f, 200f)
                verticalLineToRelative(160f)
                close()
                moveTo(600f, 360f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                lineTo(600f, 200f)
                verticalLineToRelative(160f)
                close()
                moveTo(600f, 760f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                lineTo(600f, 600f)
                verticalLineToRelative(160f)
                close()
                moveTo(200f, 760f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(-160f)
                lineTo(200f, 600f)
                verticalLineToRelative(160f)
                close()
                moveTo(600f, 360f)
                close()
                moveTo(600f, 600f)
                close()
                moveTo(360f, 600f)
                close()
                moveTo(360f, 360f)
                close()
            }
        }.build()

        return _GridView!!
    }

@Suppress("ObjectPropertyName")
private var _GridView: ImageVector? = null
