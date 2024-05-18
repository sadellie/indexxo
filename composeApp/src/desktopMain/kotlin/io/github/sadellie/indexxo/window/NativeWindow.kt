package io.github.sadellie.indexxo.window

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.awt.ComposeWindow
import com.mayakapps.compose.windowstyler.findSkiaLayer
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinDef.LRESULT
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinDef.UINT
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.WM_DESTROY
import com.sun.jna.platform.win32.WinUser.WM_SIZE
import com.sun.jna.platform.win32.WinUser.WS_CAPTION
import com.sun.jna.platform.win32.WinUser.WS_SYSMENU
import com.sun.jna.platform.win32.WinUser.WindowProc
import com.sun.jna.win32.W32APIOptions
import io.github.sadellie.indexxo.window.WinUserConst.HTBOTTOM
import io.github.sadellie.indexxo.window.WinUserConst.HTBOTTOMLEFT
import io.github.sadellie.indexxo.window.WinUserConst.HTBOTTOMRIGHT
import io.github.sadellie.indexxo.window.WinUserConst.HTCLIENT
import io.github.sadellie.indexxo.window.WinUserConst.HTLEFT
import io.github.sadellie.indexxo.window.WinUserConst.HTMAXBUTTON
import io.github.sadellie.indexxo.window.WinUserConst.HTRIGHT
import io.github.sadellie.indexxo.window.WinUserConst.HTTOP
import io.github.sadellie.indexxo.window.WinUserConst.HTTOPLEFT
import io.github.sadellie.indexxo.window.WinUserConst.HTTOPRIGHT
import io.github.sadellie.indexxo.window.WinUserConst.HTTRANSPANRENT
import io.github.sadellie.indexxo.window.WinUserConst.WM_LBUTTONDOWN
import io.github.sadellie.indexxo.window.WinUserConst.WM_LBUTTONUP
import io.github.sadellie.indexxo.window.WinUserConst.WM_MOUSEMOVE
import io.github.sadellie.indexxo.window.WinUserConst.WM_NCCALCSIZE
import io.github.sadellie.indexxo.window.WinUserConst.WM_NCHITTEST
import io.github.sadellie.indexxo.window.WinUserConst.WM_NCLBUTTONDOWN
import io.github.sadellie.indexxo.window.WinUserConst.WM_NCLBUTTONUP
import io.github.sadellie.indexxo.window.WinUserConst.WM_NCMOUSEMOVE
import org.jetbrains.skiko.SkiaLayer
import java.awt.Window

internal val windowsBuildNumber by lazy {
  val buildNumber = Kernel32.INSTANCE.GetVersion().high.toInt()
  buildNumber
}

internal fun isWindows11OrLater() = windowsBuildNumber >= 22000

internal object WinUserConst {
  // calculate non client area size message
  const val WM_NCCALCSIZE = 0x0083

  // non client area hit test message
  const val WM_NCHITTEST = 0x0084

  // mouse move message
  const val WM_MOUSEMOVE = 0x0200

  // left mouse button down message
  const val WM_LBUTTONDOWN = 0x0201

  // left mouse button up message
  const val WM_LBUTTONUP = 0x0202

  // non client area mouse move message
  const val WM_NCMOUSEMOVE = 0x00A0

  // non client area left mouse down message
  const val WM_NCLBUTTONDOWN = 0x00A1

  // non client area left mouse up message
  const val WM_NCLBUTTONUP = 0x00A2

  /** [WM_NCHITTEST] Mouse Position Codes */
  // pass the hit test to parent window
  internal const val HTTRANSPANRENT = -1

  // no hit test
  // internal val HTNOWHERE = 0
  // client area
  internal const val HTCLIENT = 1

  // title bar
  internal const val HTCAPTION = 2

  // max button
  internal const val HTMAXBUTTON = 9

  // window edges
  internal const val HTLEFT = 10
  internal const val HTRIGHT = 11
  internal const val HTTOP = 12
  internal const val HTTOPLEFT = 13
  internal const val HTTOPRIGHT = 14
  internal const val HTBOTTOM = 15
  internal const val HTBOTTOMLEFT = 16
  internal const val HTBOTTOMRIGHT = 17
}

// See https://stackoverflow.com/q/62240901
@Structure.FieldOrder(
  "leftBorderWidth",
  "rightBorderWidth",
  "topBorderHeight",
  "bottomBorderHeight",
)
internal data class WindowMargins(
  @JvmField var leftBorderWidth: Int,
  @JvmField var rightBorderWidth: Int,
  @JvmField var topBorderHeight: Int,
  @JvmField var bottomBorderHeight: Int,
) : Structure(), Structure.ByReference

internal class ComposeWindowProcedure(
  private val window: Window,
  private val hitTest: (x: Float, y: Float) -> Int,
  private val onWindowInsetUpdate: (WindowInsets) -> Unit,
) : WindowProc {
  private val windowPointer =
    (this.window as? ComposeWindow)?.windowHandle?.let(::Pointer)
      ?: Native.getWindowPointer(this.window)

  val windowHandle = HWND(windowPointer)

  private var hitResult = 1

  private val margins =
    WindowMargins(
      leftBorderWidth = 0,
      topBorderHeight = 0,
      rightBorderWidth = -1,
      bottomBorderHeight = -1,
    )

  // The default window procedure to call its methods when the default method behaviour is
  // desired/sufficient
  private var defaultWindowProcedure =
    User32Extend.instance?.setWindowLong(windowHandle, WinUser.GWL_WNDPROC, this) ?: LONG_PTR(-1)

  private var dpi = UINT(0)
  private var width = 0
  private var height = 0
  private var frameX = 0
  private var frameY = 0
  private var edgeX = 0
  private var edgeY = 0
  private var padding = 0
  private var isMaximized = User32Extend.instance?.isWindowInMaximized(windowHandle) ?: false

  @Suppress("UNUSED")
  private val skiaLayerProcedure =
    (window as? ComposeWindow)?.findSkiaLayer()?.let { skiaLayer ->
      SkiaLayerWindowProcedure(
        skiaLayer = skiaLayer,
        hitTest = { x, y ->
          val horizontalPadding = frameX
          val verticalPadding = frameY
          // Hit test for resizer border
          hitResult =
            when {
              // skip resizer border hit test if window is maximized
              isMaximized -> hitTest(x, y)
              x <= horizontalPadding && y > verticalPadding && y < height - verticalPadding ->
                HTLEFT

              x <= horizontalPadding && y <= verticalPadding -> HTTOPLEFT
              x <= horizontalPadding -> HTBOTTOMLEFT
              y <= verticalPadding && x > horizontalPadding && x < width - horizontalPadding ->
                HTTOP

              y <= verticalPadding && x <= horizontalPadding -> HTTOPLEFT
              y <= verticalPadding -> HTTOPRIGHT
              x >= width - horizontalPadding &&
                y > verticalPadding &&
                y < height - verticalPadding -> HTRIGHT

              x >= width - horizontalPadding && y <= verticalPadding -> HTTOPRIGHT
              x >= width - horizontalPadding -> HTBOTTOMRIGHT
              y >= height - verticalPadding &&
                x > horizontalPadding &&
                x < width - horizontalPadding -> HTBOTTOM

              y >= height - verticalPadding && x <= horizontalPadding -> HTBOTTOMLEFT
              y >= height - verticalPadding -> HTBOTTOMRIGHT
              // else hit test by user
              else -> hitTest(x, y)
            }
          hitResult
        },
      )
    }

  init {
    enableResizability()
    enableBorderAndShadow()
  }

  override fun callback(hWnd: HWND, uMsg: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
    return when (uMsg) {
      // Returns 0 to make the window not draw the non-client area (title bar and border)
      // thus effectively making all the window our client area
      WM_NCCALCSIZE -> {
        if (wParam.toInt() == 0) {
          User32Extend.instance?.CallWindowProc(defaultWindowProcedure, hWnd, uMsg, wParam, lParam)
            ?: LRESULT(0)
        } else {
          val user32 = User32Extend.instance ?: return LRESULT(0)
          dpi = user32.GetDpiForWindow(hWnd)
          frameX = user32.GetSystemMetricsForDpi(WinUser.SM_CXFRAME, dpi)
          frameY = user32.GetSystemMetricsForDpi(WinUser.SM_CYFRAME, dpi)
          // Returns 2 and causes weird padding when not minimized
          // edgeX = user32.GetSystemMetricsForDpi(WinUser.SM_CXEDGE, dpi)
          // edgeY = user32.GetSystemMetricsForDpi(WinUser.SM_CYEDGE, dpi)
          padding = user32.GetSystemMetricsForDpi(WinUser.SM_CXPADDEDBORDER, dpi)
          isMaximized = user32.isWindowInMaximized(hWnd)
          // Edge inset padding for non-client area
          val horizontalInsets = if (isMaximized) frameX + padding else edgeX
          val verticalInsets = if (isMaximized) frameY + padding else edgeY
          onWindowInsetUpdate(
            WindowInsets(
              left = horizontalInsets,
              right = horizontalInsets,
              top = verticalInsets,
              bottom = verticalInsets,
            ),
          )
          LRESULT(0)
        }
      }

      WM_NCHITTEST -> {
        // Hit test result return
        return LRESULT(hitResult.toLong())
      }

      WM_DESTROY -> {
        User32Extend.instance?.CallWindowProc(defaultWindowProcedure, hWnd, uMsg, wParam, lParam)
          ?: LRESULT(0)
      }

      WM_SIZE -> {
        width = lParam.toInt() and 0xFFFF
        height = (lParam.toInt() shr 16) and 0xFFFF
        User32Extend.instance?.CallWindowProc(defaultWindowProcedure, hWnd, uMsg, wParam, lParam)
          ?: LRESULT(0)
      }

      else -> {
        User32Extend.instance?.CallWindowProc(defaultWindowProcedure, hWnd, uMsg, wParam, lParam)
          ?: LRESULT(0)
      }
    }
  }

  /** For this to take effect, also set `resizable` argument of Compose Window to `true`. */
  private fun enableResizability() {
    // Enable window resizing and remove standard caption bar
    User32Extend.instance?.updateWindowStyle(windowHandle) { oldStyle ->
      (oldStyle or WS_CAPTION) and WS_SYSMENU.inv()
    }
  }

  /**
   * To disable window border and shadow, pass (0, 0, 0, 0) as window margins (or, simply, don't
   * call this function).
   */
  private fun enableBorderAndShadow() {
    val dwmApi =
      "dwmapi"
        .runCatching(NativeLibrary::getInstance)
        .onFailure { println("Could not load dwmapi library") }
        .getOrNull()
    dwmApi
      ?.runCatching { getFunction("DwmExtendFrameIntoClientArea") }
      ?.onFailure {
        println("Could not enable window native decorations (border/shadow/rounded corners)")
      }
      ?.getOrNull()
      ?.invoke(arrayOf(windowHandle, margins))
  }
}

private class SkiaLayerWindowProcedure(
  skiaLayer: SkiaLayer,
  private val hitTest: (x: Float, y: Float) -> Int,
) : WindowProc {

  private val windowHandle = HWND(Pointer(skiaLayer.windowHandle))
  private val contentHandle = HWND(skiaLayer.canvas.let(Native::getComponentPointer))
  private val defaultWindowProcedure =
    User32Extend.instance?.setWindowLong(contentHandle, WinUser.GWL_WNDPROC, this)
      ?: LONG_PTR(-1)

  private var hitResult = 1

  override fun callback(
    hwnd: HWND,
    uMsg: Int,
    wParam: WPARAM,
    lParam: LPARAM,
  ): LRESULT {

    return when (uMsg) {
      WM_NCHITTEST -> {
        val x = lParam.toInt() and 0xFFFF
        val y = (lParam.toInt() shr 16) and 0xFFFF
        val point = POINT(x, y)
        User32Extend.instance?.ScreenToClient(windowHandle, point)
        hitResult = hitTest(point.x.toFloat(), point.y.toFloat())
        point.clear()
        when (hitResult) {
          HTCLIENT,
          HTMAXBUTTON -> LRESULT(hitResult.toLong())

          else -> LRESULT(HTTRANSPANRENT.toLong())
        }
      }

      WM_NCMOUSEMOVE -> {
        User32Extend.instance?.SendMessage(contentHandle, WM_MOUSEMOVE, wParam, lParam)
        LRESULT(0)
      }

      WM_NCLBUTTONDOWN -> {
        User32Extend.instance?.SendMessage(contentHandle, WM_LBUTTONDOWN, wParam, lParam)
        LRESULT(0)
      }

      WM_NCLBUTTONUP -> {
        User32Extend.instance?.SendMessage(contentHandle, WM_LBUTTONUP, wParam, lParam)
        return LRESULT(0)
      }

      else -> {
        User32Extend.instance?.CallWindowProc(defaultWindowProcedure, hwnd, uMsg, wParam, lParam)
          ?: LRESULT(0)
      }
    }
  }
}

@Suppress("FunctionName")
private interface User32Extend : User32 {

  fun SetWindowLong(hWnd: HWND, nIndex: Int, wndProc: WindowProc): LONG_PTR

  fun SetWindowLongPtr(hWnd: HWND, nIndex: Int, wndProc: WindowProc): LONG_PTR

  fun CallWindowProc(proc: LONG_PTR, hWnd: HWND, uMsg: Int, uParam: WPARAM, lParam: LPARAM): LRESULT

  fun GetSystemMetricsForDpi(nIndex: Int, dpi: UINT): Int

  fun GetDpiForWindow(hWnd: HWND): UINT

  fun ScreenToClient(hWnd: HWND, lpPoint: POINT): Boolean

  companion object {

    val instance by lazy {
      runCatching { Native.load("user32", User32Extend::class.java, W32APIOptions.DEFAULT_OPTIONS) }
        .onFailure { println("Could not load user32 library") }
        .getOrNull()
    }
  }
}

private fun User32Extend.setWindowLong(
  hWnd: HWND, nIndex: Int, procedure: WindowProc
): LONG_PTR {
  return if (Platform.is64Bit()) {
    SetWindowLongPtr(hWnd, nIndex, procedure)
  } else {
    SetWindowLong(hWnd, nIndex, procedure)
  }
}

private fun User32.isWindowInMaximized(hWnd: HWND): Boolean {
  val placement = WinUser.WINDOWPLACEMENT()
  val result =
    GetWindowPlacement(hWnd, placement).booleanValue() &&
      placement.showCmd == WinUser.SW_SHOWMAXIMIZED
  placement.clear()
  return result
}

private fun User32.updateWindowStyle(hWnd: HWND, styleBlock: (oldStyle: Int) -> Int) {
  val oldStyle = GetWindowLong(hWnd, WinUser.GWL_STYLE)
  SetWindowLong(hWnd, WinUser.GWL_STYLE, styleBlock(oldStyle))
}
