// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.types

import java.io.PrintStream

/** 256-bit hashes with Hamming distance */
class HashInt64 {
  // 4 slots of 64 bits each. See ../sql/README.md for why int64 values are
  // useful
  val w: LongArray

  // ----------------------------------------------------------------
  constructor() {
    this.w = LongArray(HASH_INT64_NUM_VALS)
    for (i in 0 until HASH_INT64_NUM_VALS) {
      w[i] = 0
    }
  }

  // ----------------------------------------------------------------
  // Helper method to do sign extension
  private fun toWord(w: Short): Int {
    return w.toInt() and 0xffff
  }

  constructor(that: Hash256) {
    this.w = LongArray(HASH_INT64_NUM_VALS)
    var j = 0
    var i = Hash256.HASH256_NUM_SLOTS - 1
    while (i >= 0) {
      var `val` = toWord(that.w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(that.w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(that.w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(that.w[i--]).toLong()
      w[j++] = `val`
    }
  }

  // ----------------------------------------------------------------
  fun dumpVals(o: PrintStream) {
    for (i in 0 until HASH_INT64_NUM_VALS) {
      o.printf("%d", w[i])
      if (i < HASH_INT64_NUM_VALS - 1) o.printf(" ")
    }
    o.printf("\n")
  }

  companion object {
    const val HASH_INT64_NUM_VALS: Int = 4
  }
}
