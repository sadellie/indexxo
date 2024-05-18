package io.github.sadellie.indexxo.core.designsystem.icons.rounded

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.Suppress

val SymbolsRounded.HdrAuto: ImageVector
  get() {
    if (_HdrAuto != null) {
      return _HdrAuto!!
    }
    _HdrAuto = ImageVector.Builder(
      name = "HdrAuto",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 960f,
      viewportHeight = 960f,
    ).apply {
      path(fill = SolidColor(Color(0xFFE8EAED))) {
        moveTo(276f, 680f)
        horizontalLineToRelative(76f)
        lineToRelative(40f, -112f)
        horizontalLineToRelative(176f)
        lineToRelative(40f, 112f)
        horizontalLineToRelative(76f)
        lineTo(520f, 240f)
        horizontalLineToRelative(-80f)
        lineTo(276f, 680f)
        close()
        moveTo(414f, 504f)
        lineTo(478f, 322f)
        horizontalLineToRelative(4f)
        lineToRelative(64f, 182f)
        lineTo(414f, 504f)
        close()
        moveTo(480f, 880f)
        quadToRelative(-83f, 0f, -156f, -31.5f)
        reflectiveQuadTo(197f, 763f)
        quadToRelative(-54f, -54f, -85.5f, -127f)
        reflectiveQuadTo(80f, 480f)
        quadToRelative(0f, -83f, 31.5f, -156f)
        reflectiveQuadTo(197f, 197f)
        quadToRelative(54f, -54f, 127f, -85.5f)
        reflectiveQuadTo(480f, 80f)
        quadToRelative(83f, 0f, 156f, 31.5f)
        reflectiveQuadTo(763f, 197f)
        quadToRelative(54f, 54f, 85.5f, 127f)
        reflectiveQuadTo(880f, 480f)
        quadToRelative(0f, 83f, -31.5f, 156f)
        reflectiveQuadTo(763f, 763f)
        quadToRelative(-54f, 54f, -127f, 85.5f)
        reflectiveQuadTo(480f, 880f)
        close()
        moveTo(480f, 480f)
        close()
        moveTo(480f, 800f)
        quadToRelative(133f, 0f, 226.5f, -93.5f)
        reflectiveQuadTo(800f, 480f)
        quadToRelative(0f, -133f, -93.5f, -226.5f)
        reflectiveQuadTo(480f, 160f)
        quadToRelative(-133f, 0f, -226.5f, 93.5f)
        reflectiveQuadTo(160f, 480f)
        quadToRelative(0f, 133f, 93.5f, 226.5f)
        reflectiveQuadTo(480f, 800f)
        close()
      }
    }.build()

    return _HdrAuto!!
  }

@Suppress("ObjectPropertyName")
private var _HdrAuto: ImageVector? = null
