package pdqhashing

import pdqhashing.hasher.PDQHasher
import pdqhashing.utils.MatrixUtil.allocateMatrix
import pdqhashing.utils.MatrixUtil.allocateMatrixAsRowMajorArray
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals

class PDQHasherTest {
  @Test
  fun testFromBufferedImage() {
    val bi = ImageIO.read(File("./testDirs/pdq/bridge-1-original.jpg"))

    var numRows = bi.height
    var numCols = bi.width
    var buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    var buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    var buffer64x64: Array<FloatArray> = allocateMatrix(64, 64)
    var buffer16x64: Array<FloatArray> = allocateMatrix(16, 64)
    var buffer16x16: Array<FloatArray> = allocateMatrix(16, 16)

    val hasher = PDQHasher()
    val hashAndQuality =
      hasher.fromBufferedImage(bi, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16).hash

    assertEquals(
      "f8f8f0cee0f4a84f06370a22038f63f0b36e2ed596621e1d33e6b39c4e9c9b22",
      hashAndQuality.toString(),
    )
    assertEquals(0, hashAndQuality.hammingDistance(hashAndQuality))

    val bi2 = ImageIO.read(File("./testDirs/pdq/bridge-2-rotate-90.jpg"))
    numRows = bi2.height
    numCols = bi2.width
    buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer64x64 = allocateMatrix(64, 64)
    buffer16x64 = allocateMatrix(16, 64)
    buffer16x16 = allocateMatrix(16, 16)

    val hashAndQuality2 =
      hasher.fromBufferedImage(bi2, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16).hash

    assertEquals(
      "30a10efd71cc3d429013d48d0ffffc52e34e0e17ada952a9d29685211ea9e5af",
      hashAndQuality2.toString(),
    )

    assertEquals(120, hashAndQuality.hammingDistance(hashAndQuality2))

    val bi3 = ImageIO.read(File("./testDirs/pdq/pen-and-coaster.png"))
    numRows = bi3.height
    numCols = bi3.width
    buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer64x64 = allocateMatrix(64, 64)
    buffer16x64 = allocateMatrix(16, 64)
    buffer16x16 = allocateMatrix(16, 16)

    val hashAndQuality3 =
      hasher.fromBufferedImage(bi3, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16)
    assertEquals(138, hashAndQuality.hammingDistance(hashAndQuality3.hash))

    // try with gray-scaled image
    val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
    val op = ColorConvertOp(cs, null)
    val biGs = op.filter(bi, null)

    numRows = biGs.height
    numCols = biGs.width
    buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer64x64 = allocateMatrix(64, 64)
    buffer16x64 = allocateMatrix(16, 64)
    buffer16x16 = allocateMatrix(16, 16)

    val hashAndQuality4 =
      hasher.fromBufferedImage(biGs, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16)
    assertEquals(0, hashAndQuality.hammingDistance(hashAndQuality4.hash))

    // try with lower resolution image
    numRows = bi.height
    numCols = bi.width
    buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    buffer64x64 = allocateMatrix(64, 64)
    buffer16x64 = allocateMatrix(16, 64)
    buffer16x16 = allocateMatrix(16, 16)
    val biLowres = BufferedImage(numCols / 2, numRows / 2, bi.type)

    // scales the input image to the output image
    val g2d = biLowres.createGraphics()
    g2d.drawImage(bi, 0, 0, numCols / 2, numRows / 2, null)
    g2d.dispose()

    val hashAndQuality5 =
      hasher.fromBufferedImage(biLowres, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16)
    assertEquals(4, hashAndQuality.hammingDistance(hashAndQuality5.hash))
  }
}
