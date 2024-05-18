// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.utils

object MatrixUtil {
  fun allocateMatrix(numRows: Int, numCols: Int): Array<FloatArray> =
    Array(numRows) { FloatArray(numCols) }

  fun allocateMatrixAsRowMajorArray(numRows: Int, numCols: Int): FloatArray =
    FloatArray(numRows * numCols)

  // ================================================================
  // The following code is public domain.
  // Algorithm by Torben Mogensen, implementation by N. Devillard.
  // This code in public domain.
  // C/array -> Java/matrix port: John Kerl
  // ================================================================
  /** Torben's median algorithm. */
  fun torben(m: Array<FloatArray>, numRows: Int, numCols: Int): Float {
    val n = numRows * numCols
    val midn = (n + 1) / 2

    var i: Int
    var j: Int
    var less: Int
    var greater: Int
    var equal: Int
    var min: Float
    var max: Float
    var guess: Float
    var maxltguess: Float
    var mingtguess: Float

    max = m[0][0]
    min = max
    i = 0
    while (i < numRows) {
      j = 0
      while (j < numCols) {
        val v = m[i][j]
        if (v < min) min = v
        if (v > max) max = v
        j++
      }
      i++
    }

    while (true) {
      guess = (min + max) / 2
      less = 0
      greater = 0
      equal = 0
      maxltguess = min
      mingtguess = max

      i = 0
      while (i < numRows) {
        j = 0
        while (j < numCols) {
          val v = m[i][j]
          if (v < guess) {
            less++
            if (v > maxltguess) maxltguess = v
          } else if (v > guess) {
            greater++
            if (v < mingtguess) mingtguess = v
          } else equal++
          j++
        }
        i++
      }

      if (less <= midn && greater <= midn) break
      else if (less > greater) max = maxltguess else min = mingtguess
    }
    return if (less >= midn) {
      maxltguess
    } else if (less + equal >= midn) {
      guess
    } else {
      mingtguess
    }
  }
}
