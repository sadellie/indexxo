/*
 * Indexxo is file management software.
 * Copyright (c) 2024 Elshan Agaev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package androidx.compose.material3.windowsizeclass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import kotlin.jvm.JvmInline

// Modified androidx.compose.material3.windowsizeclass
fun calculateWindowSizeClass(size: DpSize): WindowSizeClass =
  WindowSizeClass.calculateFromSize(size)

/**
 * Window size classes are a set of opinionated viewport breakpoints to design, develop, and test
 * responsive application layouts against. For more details check <a
 * href="https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes"
 * class="external" target="_blank">Support different screen sizes</a> documentation.
 *
 * WindowSizeClass contains a [WindowWidthSizeClass] and [WindowHeightSizeClass], representing the
 * window size classes for this window's width and height respectively.
 *
 * See [calculateWindowSizeClass] to calculate the WindowSizeClass for an Activity's current window
 *
 * @property widthSizeClass width-based window size class ([WindowWidthSizeClass])
 * @property heightSizeClass height-based window size class ([WindowHeightSizeClass])
 */
@Immutable
class WindowSizeClass(
  val widthSizeClass: WindowWidthSizeClass,
  val heightSizeClass: WindowHeightSizeClass,
) {
  companion object {
    /**
     * Calculates [WindowSizeClass] for a given [size]. Should be used for testing purposes only
     * - to calculate a [WindowSizeClass] for the Activity's current window see
     *   [calculateWindowSizeClass].
     *
     * @param size of the window
     * @return [WindowSizeClass] corresponding to the given width and height
     */
    fun calculateFromSize(size: DpSize): WindowSizeClass {
      val windowWidthSizeClass = WindowWidthSizeClass.fromWidth(size.width)
      val windowHeightSizeClass = WindowHeightSizeClass.fromHeight(size.height)
      return WindowSizeClass(windowWidthSizeClass, windowHeightSizeClass)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as WindowSizeClass

    if (widthSizeClass != other.widthSizeClass) return false
    if (heightSizeClass != other.heightSizeClass) return false

    return true
  }

  override fun hashCode(): Int {
    var result = widthSizeClass.hashCode()
    result = 31 * result + heightSizeClass.hashCode()
    return result
  }

  override fun toString() = "WindowSizeClass($widthSizeClass, $heightSizeClass)"
}

/**
 * Width-based window size class.
 *
 * A window size class represents a breakpoint that can be used to build responsive layouts. Each
 * window size class breakpoint represents a majority of cases for typical device scenarios so your
 * layouts will work well on most devices and configurations.
 *
 * For more details see <a
 * href="https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes"
 * class="external" target="_blank">Window size classes documentation</a>.
 */
@Immutable
@JvmInline
value class WindowWidthSizeClass private constructor(private val value: Int) :
  Comparable<WindowWidthSizeClass> {

  override operator fun compareTo(other: WindowWidthSizeClass) =
    breakpoint().compareTo(other.breakpoint())

  override fun toString(): String {
    return "WindowWidthSizeClass." +
      when (this) {
        Compact -> "Compact"
        Medium -> "Medium"
        Expanded -> "Expanded"
        else -> ""
      }
  }

  companion object {
    /** Represents the majority of phones in portrait. */
    val Compact = WindowWidthSizeClass(0)

    /**
     * Represents the majority of tablets in portrait and large unfolded inner displays in portrait.
     */
    val Medium = WindowWidthSizeClass(1)

    /**
     * Represents the majority of tablets in landscape and large unfolded inner displays in
     * landscape.
     */
    val Expanded = WindowWidthSizeClass(2)

    /**
     * The default set of size classes that includes [Compact], [Medium], and [Expanded] size
     * classes. Should never expand to ensure behavioral consistency.
     */
    val DefaultSizeClasses = setOf(Compact, Medium, Expanded)

    private fun WindowWidthSizeClass.breakpoint(): Dp {
      return when {
        this == Expanded -> 840.dp
        this == Medium -> 600.dp
        else -> 0.dp
      }
    }

    /** Calculates the [WindowWidthSizeClass] for a given [width] */
    internal fun fromWidth(width: Dp): WindowWidthSizeClass {
      return fromWidth(with(defaultDensity) { width.toPx() }, defaultDensity, DefaultSizeClasses)
    }

    /**
     * Calculates the best matched [WindowWidthSizeClass] for a given [width] in Pixels and a given
     * [Density] from [supportedSizeClasses].
     */
    internal fun fromWidth(
      width: Float,
      density: Density,
      supportedSizeClasses: Set<WindowWidthSizeClass>,
    ): WindowWidthSizeClass {
      require(width >= 0) { "Width must not be negative" }
      require(supportedSizeClasses.isNotEmpty()) { "Must support at least one size class" }
      val sortedSizeClasses = supportedSizeClasses.sortedDescending()
      // Find the largest supported size class that matches the width
      sortedSizeClasses.fastForEach {
        if (width >= with(density) { it.breakpoint().toPx() }) {
          return it
        }
      }
      // If none of the size classes matches, return the smallest one.
      return sortedSizeClasses.last()
    }
  }
}

/**
 * Height-based window size class.
 *
 * A window size class represents a breakpoint that can be used to build responsive layouts. Each
 * window size class breakpoint represents a majority of cases for typical device scenarios so your
 * layouts will work well on most devices and configurations.
 *
 * For more details see <a
 * href="https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes"
 * class="external" target="_blank">Window size classes documentation</a>.
 */
@Immutable
@JvmInline
value class WindowHeightSizeClass private constructor(private val value: Int) :
  Comparable<WindowHeightSizeClass> {

  override operator fun compareTo(other: WindowHeightSizeClass) =
    breakpoint().compareTo(other.breakpoint())

  override fun toString(): String {
    return "WindowHeightSizeClass." +
      when (this) {
        Compact -> "Compact"
        Medium -> "Medium"
        Expanded -> "Expanded"
        else -> ""
      }
  }

  companion object {
    /** Represents the majority of phones in landscape */
    val Compact = WindowHeightSizeClass(0)

    /** Represents the majority of tablets in landscape and majority of phones in portrait */
    val Medium = WindowHeightSizeClass(1)

    /** Represents the majority of tablets in portrait */
    val Expanded = WindowHeightSizeClass(2)

    /**
     * The default set of size classes that includes [Compact], [Medium], and [Expanded] size
     * classes. Should never expand to ensure behavioral consistency.
     */
    val DefaultSizeClasses = setOf(Compact, Medium, Expanded)

    private fun WindowHeightSizeClass.breakpoint(): Dp {
      return when {
        this == Expanded -> 900.dp
        this == Medium -> 480.dp
        else -> 0.dp
      }
    }

    /** Calculates the [WindowHeightSizeClass] for a given [height] */
    internal fun fromHeight(height: Dp): WindowHeightSizeClass {
      return fromHeight(with(defaultDensity) { height.toPx() }, defaultDensity, DefaultSizeClasses)
    }

    /**
     * Calculates the best matched [WindowHeightSizeClass] for a given [height] in Pixels and a
     * given [Density] from [supportedSizeClasses].
     */
    internal fun fromHeight(
      height: Float,
      density: Density,
      supportedSizeClasses: Set<WindowHeightSizeClass>,
    ): WindowHeightSizeClass {
      require(height >= 0) { "Width must not be negative" }
      require(supportedSizeClasses.isNotEmpty()) { "Must support at least one size class" }
      val sortedSizeClasses = supportedSizeClasses.sortedDescending()
      // Find the largest supported size class that matches the width
      sortedSizeClasses.fastForEach {
        if (height >= with(density) { it.breakpoint().toPx() }) {
          return it
        }
      }
      // If none of the size classes matches, return the smallest one.
      return sortedSizeClasses.last()
    }
  }
}

private val defaultDensity = Density(1F, 1F)
