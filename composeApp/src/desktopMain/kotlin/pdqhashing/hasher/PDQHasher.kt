// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.hasher

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt
import pdqhashing.types.Hash256
import pdqhashing.types.HashAndQuality
import pdqhashing.types.HashesAndQuality
import pdqhashing.utils.MatrixUtil.allocateMatrix
import pdqhashing.utils.MatrixUtil.allocateMatrixAsRowMajorArray
import pdqhashing.utils.MatrixUtil.torben

/**
 * The only class state is the DCT matrix, so this class may either be instantiated once per image,
 * or instantiated once and used for all images; the latter will be slightly faster as the DCT
 * matrix will not need to be recomputed once per image. Methods are threadsafe.
 */
class PDQHasher {
  // ================================================================

  // Christoph Zauner 'Implementation and Benchmarking of Perceptual
  // Image Hash Functions' 2010
  //
  // See also comments on dct64To16. Input is (0..63)x(0..63); output is
  // (1..16)x(1..16) with the latter indexed as (0..15)x(0..15).
  // Returns 16x64 matrix.

  private val DCT_matrix = allocateMatrix(16, 64)

  init {
    for (i in 0..15) {
      for (j in 0..63) {
        DCT_matrix[i][j] =
          (DCT_MATRIX_SCALE_FACTOR * cos((Math.PI / 2 / 64.0) * (i + 1) * (2 * j + 1))).toFloat()
      }
    }
  }

  // ================================================================
  // Supporting info returned by the hashing operation
  class HashingMetadata {
    var readSeconds: Float = (-1.0).toFloat()
    var hashSeconds: Float = (-1.0).toFloat()
    var imageHeightTimesWidth: Int = -1
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class)
  fun fromFile(filename: String, hashingMetadata: HashingMetadata): HashAndQuality {
    var t1 = System.nanoTime()
    val img: BufferedImage?
    try {
      img = ImageIO.read(File(filename))
    } catch (e: IOException) {
      throw e
    }
    var t2 = System.nanoTime()
    hashingMetadata.readSeconds = ((t2 - t1) / 1e9).toFloat()

    val numRows = img.height
    val numCols = img.width
    hashingMetadata.imageHeightTimesWidth = numRows * numCols

    val buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    val buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    val buffer64x64 = allocateMatrix(64, 64)
    val buffer16x64 = allocateMatrix(16, 64)
    val buffer16x16 = allocateMatrix(16, 16)

    t1 = System.nanoTime()
    val rv = fromBufferedImage(img, buffer1, buffer2, buffer64x64, buffer16x64, buffer16x16)
    t2 = System.nanoTime()
    hashingMetadata.hashSeconds = ((t2 - t1) / 1e9).toFloat()

    return rv
  }

  // ----------------------------------------------------------------
  // The buffers could be allocated within this method rather than being passed in.
  // This is coded with an eye to the future wherein we could hash video frames,
  // all of which would be the same dimension, and the buffers could be reused.
  fun fromBufferedImage(
    img: BufferedImage,
    buffer1: FloatArray =
      allocateMatrixAsRowMajorArray(
        img.height,
        img.width,
      ), // image numRows x numCols as row-major array
    buffer2: FloatArray =
      allocateMatrixAsRowMajorArray(
        img.height,
        img.width,
      ), // image numRows x numCols as row-major array
    buffer64x64: Array<FloatArray> = allocateMatrix(64, 64),
    buffer16x64: Array<FloatArray> = allocateMatrix(16, 64),
    buffer16x16: Array<FloatArray> = allocateMatrix(16, 16),
  ): HashAndQuality {
    val numRows = img.height
    val numCols = img.width

    fillFloatLumaFromBufferImage(img, buffer1)

    return pdqHash256FromFloatLuma(
      buffer1,
      buffer2,
      numRows,
      numCols,
      buffer64x64,
      buffer16x64,
      buffer16x16,
    )
  }

  // ----------------------------------------------------------------
  // Made public for test/demo access
  private fun fillFloatLumaFromBufferImage(
    img: BufferedImage,
    luma: FloatArray,
  ) // image numRows x numCols as row-major array
  {
    val numRows = img.height
    val numCols = img.width

    for (i in 0 until numRows) {
      for (j in 0 until numCols) {
        val rgb = img.getRGB(j, i) // xxx check semantics of these packed-as-int pixels
        val r = (rgb shr 16) and 0xff
        val g = (rgb shr 8) and 0xff
        val b = rgb and 0xff
        luma[i * numCols + j] =
          LUMA_FROM_R_COEFF * r + LUMA_FROM_G_COEFF * g + LUMA_FROM_B_COEFF * b
      }
    }
  }

  // ----------------------------------------------------------------
  private fun pdqHash256FromFloatLuma(
    fullBuffer1: FloatArray, // image numRows x numCols as row-major array
    fullBuffer2: FloatArray, // image numRows x numCols as row-major array
    numRows: Int,
    numCols: Int,
    buffer64x64: Array<FloatArray>,
    buffer16x64: Array<FloatArray>,
    buffer16x16: Array<FloatArray>,
  ): HashAndQuality {
    // Downsample (blur and decimate)
    val windowSizeAlongRows = computeJaroszFilterWindowSize(numCols)
    val windowSizeAlongCols = computeJaroszFilterWindowSize(numRows)

    jaroszFilterFloat(
      fullBuffer1,
      fullBuffer2,
      numRows,
      numCols,
      windowSizeAlongRows,
      windowSizeAlongCols,
      PDQ_NUM_JAROSZ_XY_PASSES,
    )

    decimateFloat(fullBuffer1, numRows, numCols, buffer64x64)

    // Quality metric.  Reuse the 64x64 image-domain downsample
    // since we already have it.
    val quality = computePDQImageDomainQualityMetric(buffer64x64)

    // 2D DCT
    dct64To16(buffer64x64, buffer16x64, buffer16x16)

    //  Output bits
    val hash = pdqBuffer16x16ToBits(buffer16x16)

    return HashAndQuality(hash, quality)
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class)
  fun dihedralFromFile(
    filename: String,
    hashingMetadata: HashingMetadata,
    dihFlags: Int,
  ): HashesAndQuality {
    var t1 = System.nanoTime()
    val img: BufferedImage
    try {
      img = ImageIO.read(File(filename))
    } catch (e: IOException) {
      throw e
    }
    var t2 = System.nanoTime()
    hashingMetadata.readSeconds = ((t2 - t1) / 1e9).toFloat()

    val numRows = img.height
    val numCols = img.width
    hashingMetadata.imageHeightTimesWidth = numRows * numCols

    val buffer1 = allocateMatrixAsRowMajorArray(numRows, numCols)
    val buffer2 = allocateMatrixAsRowMajorArray(numRows, numCols)
    val buffer64x64 = allocateMatrix(64, 64)
    val buffer16x64 = allocateMatrix(16, 64)
    val buffer16x16 = allocateMatrix(16, 16)
    val buffer16x16Aux = allocateMatrix(16, 16)

    t1 = System.nanoTime()
    val rv =
      dihedralFromBufferedImage(
        img,
        buffer1,
        buffer2,
        buffer64x64,
        buffer16x64,
        buffer16x16,
        buffer16x16Aux,
        dihFlags,
      )
    t2 = System.nanoTime()
    hashingMetadata.hashSeconds = ((t2 - t1) / 1e9).toFloat()

    return rv
  }

  // ----------------------------------------------------------------
  // The buffers could be allocated within this method rather than being passed in.
  // This is coded with an eye to the future wherein we could hash video frames,
  // all of which would be the same dimension, and the buffers could be reused.
  fun dihedralFromBufferedImage(
    img: BufferedImage,
    buffer1: FloatArray = allocateMatrixAsRowMajorArray(img.height, img.width), // image numRows x numCols as row-major array
    buffer2: FloatArray = allocateMatrixAsRowMajorArray(img.height, img.width), // image numRows x numCols as row-major array
    buffer64x64: Array<FloatArray> = allocateMatrix(64, 64),
    buffer16x64: Array<FloatArray> = allocateMatrix(16, 64),
    buffer16x16: Array<FloatArray> = allocateMatrix(16, 16),
    buffer16x16Aux: Array<FloatArray> = allocateMatrix(16, 16),
    dihFlags: Int = PDQ_DO_DIH_ALL,
  ): HashesAndQuality {
    val numRows = img.height
    val numCols = img.width

    fillFloatLumaFromBufferImage(img, buffer1)

    return pdqHash256esFromFloatLuma(
      buffer1,
      buffer2,
      numRows,
      numCols,
      buffer64x64,
      buffer16x64,
      buffer16x16,
      buffer16x16Aux,
      dihFlags,
    )
  }

  // ----------------------------------------------------------------
  private fun pdqHash256esFromFloatLuma(
    fullBuffer1: FloatArray, // image numRows x numCols as row-major array
    fullBuffer2: FloatArray, // image numRows x numCols as row-major array
    numRows: Int,
    numCols: Int,
    buffer64x64: Array<FloatArray>,
    buffer16x64: Array<FloatArray>,
    buffer16x16: Array<FloatArray>,
    buffer16x16Aux: Array<FloatArray>,
    dihFlags: Int,
  ): HashesAndQuality {
    // Downsample (blur and decimate)
    val windowSizeAlongRows = computeJaroszFilterWindowSize(numCols)
    val windowSizeAlongCols = computeJaroszFilterWindowSize(numRows)

    jaroszFilterFloat(
      fullBuffer1,
      fullBuffer2,
      numRows,
      numCols,
      windowSizeAlongRows,
      windowSizeAlongCols,
      PDQ_NUM_JAROSZ_XY_PASSES,
    )

    decimateFloat(fullBuffer1, numRows, numCols, buffer64x64)

    // Quality metric.  Reuse the 64x64 image-domain downsample
    // since we already have it.
    val quality = computePDQImageDomainQualityMetric(buffer64x64)

    // 2D DCT
    dct64To16(buffer64x64, buffer16x64, buffer16x16)

    //  Output bits
    var hash: Hash256? = null
    var hashRotate90: Hash256? = null
    var hashRotate180: Hash256? = null
    var hashRotate270: Hash256? = null
    var hashFlipX: Hash256? = null
    var hashFlipY: Hash256? = null
    var hashFlipPlus1: Hash256? = null
    var hashFlipMinus1: Hash256? = null

    if ((dihFlags and PDQ_DO_DIH_ORIGINAL) != 0) {
      hash = pdqBuffer16x16ToBits(buffer16x16)
    }
    if ((dihFlags and PDQ_DO_DIH_ROTATE_90) != 0) {
      dct16OriginalToRotate90(buffer16x16, buffer16x16Aux)
      hashRotate90 = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_ROTATE_180) != 0) {
      dct16OriginalToRotate180(buffer16x16, buffer16x16Aux)
      hashRotate180 = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_ROTATE_270) != 0) {
      dct16OriginalToRotate270(buffer16x16, buffer16x16Aux)
      hashRotate270 = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_FLIPX) != 0) {
      dct16OriginalToFlipX(buffer16x16, buffer16x16Aux)
      hashFlipX = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_FLIPY) != 0) {
      dct16OriginalToFlipY(buffer16x16, buffer16x16Aux)
      hashFlipY = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_FLIP_PLUS1) != 0) {
      dct16OriginalToFlipPlus1(buffer16x16, buffer16x16Aux)
      hashFlipPlus1 = pdqBuffer16x16ToBits(buffer16x16Aux)
    }
    if ((dihFlags and PDQ_DO_DIH_FLIP_MINUS1) != 0) {
      dct16OriginalToFlipMinus1(buffer16x16, buffer16x16Aux)
      hashFlipMinus1 = pdqBuffer16x16ToBits(buffer16x16Aux)
    }

    return HashesAndQuality(
      hash!!,
      hashRotate90!!,
      hashRotate180!!,
      hashRotate270!!,
      hashFlipX!!,
      hashFlipY!!,
      hashFlipPlus1!!,
      hashFlipMinus1!!,
      quality,
    )
  }

  // ----------------------------------------------------------------
  // Full 64x64 to 64x64 can be optimized e.g. the Lee algorithm.  But here we
  // only want slots (1-16)x(1-16) of the full 64x64 output. Careful experiments
  // showed that using Lee along all 64 slots in one dimension, then Lee along 16
  // slots in the second, followed by extracting slots 1-16 of the output, was
  // actually slower than the current implementation which is completely
  // non-clever/non-Lee but computes only what is needed.
  private fun dct64To16(
    A: Array<FloatArray>, // input: 64x64
    T: Array<FloatArray>, // temp buffer: 16x64
    B: Array<FloatArray>,
  ) // output: 16x16
  {
    val D = this.DCT_matrix

    // B = D A Dt
    // B = (D A) Dt ; T = D A
    // T is 16x64;

    // T = D A
    // Tij = sum {k} Dik Akj
    for (i in 0..15) {
      for (j in 0..63) {
        var sumk = 0.0.toFloat()
        for (k in 0..63) {
          sumk += D[i][k] * A[k][j]
        }
        T[i][j] = sumk
      }
    }

    // B = T Dt
    // Bij = sum {k} Tik Djk
    for (i in 0..15) {
      for (j in 0..15) {
        var sumk = 0.0.toFloat()
        for (k in 0..63) {
          sumk += T[i][k] * D[j][k]
        }
        B[i][j] = sumk
      }
    }
  }

  // ----------------------------------------------------------------
  // orig      rot90     rot180    rot270
  // noxpose   xpose     noxpose   xpose
  // + + + +   - + - +   + - + -   - - - -
  // + + + +   - + - +   - + - +   + + + +
  // + + + +   - + - +   + - + -   - - - -
  // + + + +   - + - +   - + - +   + + + +
  //
  // flipx     flipy     flipplus  flipminus
  // noxpose   noxpose   xpose     xpose
  // - - - -   - + - +   + + + +   + - + -
  // + + + +   - + - +   + + + +   - + - +
  // - - - -   - + - +   + + + +   + - + -
  // + + + +   - + - +   + + + +   - + - +
  // ----------------------------------------------------------------
  private fun dct16OriginalToRotate90(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if ((j and 1) != 0) {
          B[j][i] = A[i][j]
        } else {
          B[j][i] = -A[i][j]
        }
      }
    }
  }

  private fun dct16OriginalToRotate180(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if (((i + j) and 1) != 0) {
          B[i][j] = -A[i][j]
        } else {
          B[i][j] = A[i][j]
        }
      }
    }
  }

  private fun dct16OriginalToRotate270(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if ((i and 1) != 0) {
          B[j][i] = A[i][j]
        } else {
          B[j][i] = -A[i][j]
        }
      }
    }
  }

  private fun dct16OriginalToFlipX(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if ((i and 1) != 0) {
          B[i][j] = A[i][j]
        } else {
          B[i][j] = -A[i][j]
        }
      }
    }
  }

  private fun dct16OriginalToFlipY(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if ((j and 1) != 0) {
          B[i][j] = A[i][j]
        } else {
          B[i][j] = -A[i][j]
        }
      }
    }
  }

  private fun dct16OriginalToFlipPlus1(
    A: Array<FloatArray>, // input
    B: Array<FloatArray>,
  ) // output
  {
    for (i in 0..15) {
      for (j in 0..15) {
        B[j][i] = A[i][j]
      }
    }
  }

  private fun dct16OriginalToFlipMinus1(
    A: Array<FloatArray>, // input 16x16
    B: Array<FloatArray>,
  ) // output 16x16
  {
    for (i in 0..15) {
      for (j in 0..15) {
        if (((i + j) and 1) != 0) {
          B[j][i] = -A[i][j]
        } else {
          B[j][i] = A[i][j]
        }
      }
    }
  }

  // ----------------------------------------------------------------
  // Each bit of the 16x16 output hash is for whether the given frequency
  // component is greater than the median frequency component or not.
  private fun pdqBuffer16x16ToBits(dctOutput16x16: Array<FloatArray>): Hash256 {
    val hash = Hash256() // zero-filled by the constructor

    val dctMedian = torben(dctOutput16x16, 16, 16)
    for (i in 0..15) {
      for (j in 0..15) {
        if (dctOutput16x16[i][j] > dctMedian) {
          hash.setBit(i * 16 + j)
        }
      }
    }

    return hash
  }

  companion object {
    // ----------------------------------------------------------------
    // From Wikipedia: standard RGB to luminance (the 'Y' in 'YUV').
    private const val LUMA_FROM_R_COEFF = 0.299.toFloat()
    private const val LUMA_FROM_G_COEFF = 0.587.toFloat()
    private const val LUMA_FROM_B_COEFF = 0.114.toFloat()

    private val DCT_MATRIX_SCALE_FACTOR = sqrt(2.0 / 64.0).toFloat()

    // ----------------------------------------------------------------
    // Wojciech Jarosz 'Fast Image Convolutions' ACM SIGGRAPH 2001:
    // X,Y,X,Y passes of 1-D box filters produces a 2D tent filter.
    const val PDQ_NUM_JAROSZ_XY_PASSES: Int = 2

    // Since PDQ uses 64x64 blocks, 1/64th of the image height/width respectively is
    // a full block. But since we use two passes, we want half that window size per
    // pass. Example: 1024x1024 full-resolution input. PDQ downsamples to 64x64.
    // Each 16x16 block of the input produces a single downsample pixel.  X,Y passes
    // with window size 8 (= 1024/128) average pixels with 8x8 neighbors. The second
    // X,Y pair of 1D box-filter passes accumulate data from all 16x16.
    private const val PDQ_JAROSZ_WINDOW_SIZE_DIVISOR: Int = 128

    // ----------------------------------------------------------------
    // Flags for which dihedral-transforms are desired to be produced.
    const val PDQ_DO_DIH_ORIGINAL: Int = 0x01
    const val PDQ_DO_DIH_ROTATE_90: Int = 0x02
    const val PDQ_DO_DIH_ROTATE_180: Int = 0x04
    const val PDQ_DO_DIH_ROTATE_270: Int = 0x08
    const val PDQ_DO_DIH_FLIPX: Int = 0x10
    const val PDQ_DO_DIH_FLIPY: Int = 0x20
    const val PDQ_DO_DIH_FLIP_PLUS1: Int = 0x40
    const val PDQ_DO_DIH_FLIP_MINUS1: Int = 0x80
    const val PDQ_DO_DIH_ALL: Int = 0xff

    // ----------------------------------------------------------------
    fun decimateFloat(
      `in`: FloatArray, // numRows x numCols in row-major order
      inNumRows: Int,
      inNumCols: Int,
      out: Array<FloatArray>,
    ) // 64x64
    {
      // target centers not corners:
      for (i in 0..63) {
        val ini = (((i + 0.5) * inNumRows) / 64).toInt()
        for (j in 0..63) {
          val inj = (((j + 0.5) * inNumCols) / 64).toInt()
          out[i][j] = `in`[ini * inNumCols + inj]
        }
      }
    }

    // ----------------------------------------------------------------
    // This is all heuristic (see the PDQ hashing doc). Quantization matters since
    // we want to count *significant* gradients, not just the some of many small
    // ones. The constants are all manually selected, and tuned as described in the
    // document.
    private fun computePDQImageDomainQualityMetric(buffer64x64: Array<FloatArray>): Int {
      var gradientSum = 0

      for (i in 0..62) {
        for (j in 0..63) {
          val u = buffer64x64[i][j]
          val v = buffer64x64[i + 1][j]
          val d = (((u - v) * 100) / 255).toInt()
          gradientSum += abs(d.toDouble()).toInt()
        }
      }
      for (i in 0..63) {
        for (j in 0..62) {
          val u = buffer64x64[i][j]
          val v = buffer64x64[i][j + 1]
          val d = (((u - v) * 100) / 255).toInt()
          gradientSum += abs(d.toDouble()).toInt()
        }
      }

      // Heuristic scaling factor.
      var quality = gradientSum / 90
      if (quality > 100) quality = 100

      return quality
    }

    // ================================================================
    // Round up. See comments at top of file for details.
    fun computeJaroszFilterWindowSize(dimension: Int): Int {
      return ((dimension + PDQ_JAROSZ_WINDOW_SIZE_DIVISOR - 1) / PDQ_JAROSZ_WINDOW_SIZE_DIVISOR)
    }

    // ----------------------------------------------------------------
    fun jaroszFilterFloat(
      buffer1: FloatArray, // matrix as numRows x numCols in row-major order
      buffer2: FloatArray, // matrix as numRows x numCols in row-major order
      numRows: Int,
      numCols: Int,
      windowSizeAlongRows: Int,
      windowSizeAlongCols: Int,
      nreps: Int,
    ) {
      for (i in 0 until nreps) {
        boxAlongRowsFloat(buffer1, buffer2, numRows, numCols, windowSizeAlongRows)
        boxAlongColsFloat(buffer2, buffer1, numRows, numCols, windowSizeAlongCols)
      }
    }

    // ----------------------------------------------------------------
    // 7 and 4
    //
    //    0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1
    //    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
    //
    //    .                                PHASE 1: ONLY ADD, NO WRITE, NO SUBTRACT
    //    . .
    //    . . .
    //
    //  0 * . . .                          PHASE 2: ADD, WRITE, WITH NO SUBTRACTS
    //  1 . * . . .
    //  2 . . * . . .
    //  3 . . . * . . .
    //
    //  4   . . . * . . .                  PHASE 3: WRITES WITH ADD & SUBTRACT
    //  5     . . . * . . .
    //  6       . . . * . . .
    //  7         . . . * . . .
    //  8           . . . * . . .
    //  9             . . . * . . .
    // 10               . . . * . . .
    // 11                 . . . * . . .
    // 12                   . . . * . . .
    //
    // 13                     . . . * . .  PHASE 4: FINAL WRITES WITH NO ADDS
    // 14                       . . . * .
    // 15                         . . . *
    //
    //         = 0                                     =  0   PHASE 1
    //         = 0+1                                   =  1
    //         = 0+1+2                                 =  3
    //
    // out[ 0] = 0+1+2+3                               =  6   PHASE 2
    // out[ 1] = 0+1+2+3+4                             = 10
    // out[ 2] = 0+1+2+3+4+5                           = 15
    // out[ 3] = 0+1+2+3+4+5+6                         = 21
    //
    // out[ 4] =   1+2+3+4+5+6+7                       = 28   PHASE 3
    // out[ 5] =     2+3+4+5+6+7+8                     = 35
    // out[ 6] =       3+4+5+6+7+8+9                   = 42
    // out[ 7] =         4+5+6+7+8+9+10                = 49
    // out[ 8] =           5+6+7+8+9+10+11             = 56
    // out[ 9] =             6+7+8+9+10+11+12          = 63
    // out[10] =               7+8+9+10+11+12+13       = 70
    // out[11] =                 8+9+10+11+12+13+14    = 77
    // out[12] =                   9+10+11+12+13+14+15 = 84
    //
    // out[13] =                     10+11+12+13+14+15 = 75  PHASE 4
    // out[14] =                        11+12+13+14+15 = 65
    // out[15] =                           12+13+14+15 = 54
    // ----------------------------------------------------------------
    // ----------------------------------------------------------------
    // 8 and 5
    //
    //    0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1
    //    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
    //
    //    .                                PHASE 1: ONLY ADD, NO WRITE, NO SUBTRACT
    //    . .
    //    . . .
    //    . . . .
    //
    //  0 * . . . .                        PHASE 2: ADD, WRITE, WITH NO SUBTRACTS
    //  1 . * . . . .
    //  2 . . * . . . .
    //  3 . . . * . . . .
    //
    //  4   . . . * . . . .                PHASE 3: WRITES WITH ADD & SUBTRACT
    //  5     . . . * . . . .
    //  6       . . . * . . . .
    //  7         . . . * . . . .
    //  8           . . . * . . . .
    //  9             . . . * . . . .
    // 10               . . . * . . . .
    // 11                 . . . * . . . .
    //
    // 12                   . . . * . . .  PHASE 4: FINAL WRITES WITH NO ADDS
    // 13                     . . . * . .
    // 14                       . . . * .
    // 15                         . . . *
    //
    //         = 0                                     =  0   PHASE 1
    //         = 0+1                                   =  1
    //         = 0+1+2                                 =  3
    //         = 0+1+2+3                               =  6
    //
    // out[ 0] = 0+1+2+3+4                             = 10
    // out[ 1] = 0+1+2+3+4+5                           = 15
    // out[ 2] = 0+1+2+3+4+5+6                         = 21
    // out[ 3] = 0+1+2+3+4+5+6+7                       = 28
    //
    // out[ 4] =   1+2+3+4+5+6+7+8                     = 36   PHASE 3
    // out[ 5] =     2+3+4+5+6+7+8+9                   = 44
    // out[ 6] =       3+4+5+6+7+8+9+10                = 52
    // out[ 7] =         4+5+6+7+8+9+10+11             = 60
    // out[ 8] =           5+6+7+8+9+10+11+12          = 68
    // out[ 9] =             6+7+8+9+10+11+12+13       = 76
    // out[10] =               7+8+9+10+11+12+13+14    = 84
    // out[11] =                 8+9+10+11+12+13+14+15 = 92
    //
    // out[12] =                   9+10+11+12+13+14+15 = 84  PHASE 4
    // out[13] =                     10+11+12+13+14+15 = 75  PHASE 4
    // out[14] =                        11+12+13+14+15 = 65
    // out[15] =                           12+13+14+15 = 54
    // ----------------------------------------------------------------
    private fun box1DFloat(
      invec: FloatArray,
      inStartOffset: Int,
      outvec: FloatArray,
      outStartOffset: Int,
      vectorLength: Int,
      stride: Int,
      fullWindowSize: Int,
    ) {
      val halfWindowSize = (fullWindowSize + 2) / 2 // 7->4, 8->5

      val phase_1_nreps = halfWindowSize - 1
      val phase_2_nreps = fullWindowSize - halfWindowSize + 1
      val phase_3_nreps = vectorLength - fullWindowSize
      val phase_4_nreps = halfWindowSize - 1

      var li = 0 // Index of left edge of read window, for subtracts
      var ri = 0 // Index of right edge of read windows, for adds
      var oi = 0 // Index into output vector

      var sum = 0.0.toFloat()
      var currentWindowSize = 0

      // PHASE 1: ACCUMULATE FIRST SUM NO WRITES
      for (i in 0 until phase_1_nreps) {
        sum += invec[inStartOffset + ri]
        currentWindowSize++
        ri += stride
      }

      // PHASE 2: INITIAL WRITES WITH SMALL WINDOW
      for (i in 0 until phase_2_nreps) {
        sum += invec[inStartOffset + ri]
        currentWindowSize++
        outvec[outStartOffset + oi] = sum / currentWindowSize
        ri += stride
        oi += stride
      }

      // PHASE 3: WRITES WITH FULL WINDOW
      for (i in 0 until phase_3_nreps) {
        sum += invec[inStartOffset + ri]
        sum -= invec[inStartOffset + li]
        outvec[outStartOffset + oi] = sum / currentWindowSize
        li += stride
        ri += stride
        oi += stride
      }

      // PHASE 4: FINAL WRITES WITH SMALL WINDOW
      for (i in 0 until phase_4_nreps) {
        sum -= invec[inStartOffset + li]
        currentWindowSize--
        outvec[outStartOffset + oi] = sum / currentWindowSize
        li += stride
        oi += stride
      }
    }

    // ----------------------------------------------------------------
    private fun boxAlongRowsFloat(
      `in`: FloatArray, // matrix as numRows x numCols in row-major order
      out: FloatArray, // matrix as numRows x numCols in row-major order
      numRows: Int,
      numCols: Int,
      windowSize: Int,
    ) {
      for (i in 0 until numRows) {
        box1DFloat(`in`, i * numCols, out, i * numCols, numCols, 1, windowSize)
      }
    }

    // ----------------------------------------------------------------
    private fun boxAlongColsFloat(
      `in`: FloatArray, // matrix as numRows x numCols in row-major order
      out: FloatArray, // matrix as numRows x numCols in row-major order
      numRows: Int,
      numCols: Int,
      windowSize: Int,
    ) {
      for (j in 0 until numCols) {
        box1DFloat(`in`, j, out, j, numRows, numCols, windowSize)
      }
    }
  }
}
