package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.TableChart: ImageVector
    get() {
        if (_TableChart != null) {
            return _TableChart!!
        }
        _TableChart = ImageVector.Builder(
            name = "TableChart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(760f, 840f)
                lineTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(560f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(560f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveTo(200f, 320f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-120f)
                lineTo(200f, 200f)
                verticalLineToRelative(120f)
                close()
                moveTo(300f, 400f)
                lineTo(200f, 400f)
                verticalLineToRelative(360f)
                horizontalLineToRelative(100f)
                verticalLineToRelative(-360f)
                close()
                moveTo(660f, 400f)
                verticalLineToRelative(360f)
                horizontalLineToRelative(100f)
                verticalLineToRelative(-360f)
                lineTo(660f, 400f)
                close()
                moveTo(580f, 400f)
                lineTo(380f, 400f)
                verticalLineToRelative(360f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-360f)
                close()
            }
        }.build()

        return _TableChart!!
    }

@Suppress("ObjectPropertyName")
private var _TableChart: ImageVector? = null
