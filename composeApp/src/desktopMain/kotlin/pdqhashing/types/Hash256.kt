// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.types

import java.io.PrintStream
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.inv

/** 256-bit hashes with Hamming distance */
class Hash256 : Comparable<Hash256> {
  // 16 slots of 16 bits each. See ../README.md for why not 8x32 or 32x8, etc.
  val w: ShortArray = ShortArray(HASH256_NUM_SLOTS)

  // ----------------------------------------------------------------
  init {
    for (i in 0 until HASH256_NUM_SLOTS) {
      w[i] = 0
    }
  }

  // ----------------------------------------------------------------
  fun clone(): Hash256 {
    val rv = Hash256()
    for (i in 0 until HASH256_NUM_SLOTS) {
      rv.w[i] = w[i]
    }
    return rv
  }

  // ----------------------------------------------------------------
  override fun toString(): String {
    return String.format(
      IO_FORMAT,
      (w[15].toInt() and 0xffff),
      (w[14].toInt() and 0xffff),
      (w[13].toInt() and 0xffff),
      (w[12].toInt() and 0xffff),
      (w[11].toInt() and 0xffff),
      (w[10].toInt() and 0xffff),
      (w[9].toInt() and 0xffff),
      (w[8].toInt() and 0xffff),
      (w[7].toInt() and 0xffff),
      (w[6].toInt() and 0xffff),
      (w[5].toInt() and 0xffff),
      (w[4].toInt() and 0xffff),
      (w[3].toInt() and 0xffff),
      (w[2].toInt() and 0xffff),
      (w[1].toInt() and 0xffff),
      (w[0].toInt() and 0xffff),
    )
  }

  fun clearAll() {
    for (i in 0 until HASH256_NUM_SLOTS) w[i] = 0
  }

  fun setAll() {
    for (i in 0 until HASH256_NUM_SLOTS) w[i] = 0.inv().toShort()
  }

  fun hammingNorm(): Int {
    var n = 0
    for (i in 0 until HASH256_NUM_SLOTS) {
      n += hammingNorm16(w[i])
    }
    return n
  }

  fun hammingDistance(that: Hash256): Int {
    var n = 0
    for (i in 0 until HASH256_NUM_SLOTS) {
      n += hammingNorm16((w[i].toInt() xor that.w[i].toInt()).toShort())
    }
    return n
  }

  fun hammingDistanceLE(that: Hash256, d: Int): Boolean {
    var e = 0
    for (i in 0 until HASH256_NUM_SLOTS) {
      e += hammingNorm16((w[i].toInt() xor that.w[i].toInt()).toShort())
      if (e > d) return false
    }
    return true
  }

  fun setBit(k: Int) {
    w[k and 255 shr 4] = (w[k and 255 shr 4].toInt() or (1 shl (k and 15))).toShort()
  }

  fun flipBit(k: Int) {
    w[k and 255 shr 4] = (w[k and 255 shr 4].toInt() xor (1 shl (k and 15))).toShort()
  }

  fun bitwiseXOR(that: Hash256): Hash256 {
    val rv = Hash256()
    for (i in 0 until HASH256_NUM_SLOTS) {
      rv.w[i] = (w[i].toInt() xor that.w[i].toInt()).toShort()
    }
    return rv
  }

  fun bitwiseAND(that: Hash256): Hash256 {
    val rv = Hash256()
    for (i in 0 until HASH256_NUM_SLOTS) {
      rv.w[i] = (w[i].toInt() and that.w[i].toInt()).toShort()
    }
    return rv
  }

  fun bitwiseOR(that: Hash256): Hash256 {
    val rv = Hash256()
    for (i in 0 until HASH256_NUM_SLOTS) {
      rv.w[i] = (w[i].toInt() or that.w[i].toInt()).toShort()
    }
    return rv
  }

  fun bitwiseNOT(): Hash256 {
    val rv = Hash256()
    for (i in 0 until HASH256_NUM_SLOTS) {
      rv.w[i] = w[i].inv().toShort()
    }
    return rv
  }

  fun dumpBits(o: PrintStream) {
    for (i in HASH256_NUM_SLOTS - 1 downTo 0) {
      val word = (w[i].toInt()) and 0xffff
      for (j in 15 downTo 0) {
        if ((word and (1 shl j)) != 0) {
          o.printf(" 1")
        } else {
          o.printf(" 0")
        }
      }
      o.printf("\n")
    }
    o.printf("\n")
  }

  fun dumpBitsAcross(o: PrintStream) {
    for (i in HASH256_NUM_SLOTS - 1 downTo 0) {
      val word = (w[i].toInt()) and 0xffff
      for (j in 15 downTo 0) {
        if ((word and (1 shl j)) != 0) {
          o.printf(" 1")
        } else {
          o.printf(" 0")
        }
      }
    }
    o.printf("\n")
  }

  fun dumpWords(o: PrintStream) {
    for (i in HASH256_NUM_SLOTS - 1 downTo 0) {
      val word = w[i]
      if (i < HASH256_NUM_SLOTS - 1) o.printf(" ")
      o.printf("%04x", (word.toInt()) and 0xffff)
    }
    o.printf("\n")
  }

  // Helper method to do sign extension
  private fun toWord(w: Short): Int {
    return w.toInt() and 0xffff
  }

  fun dumpInt64Vals(o: PrintStream) {
    var i = HASH256_NUM_SLOTS - 1
    while (i >= 0) {
      var `val` = toWord(w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(w[i--]).toLong()
      `val` = (`val` shl 16) xor toWord(w[i--]).toLong()
      o.printf("%d", `val`)
      if (i >= 0) o.printf(" ")
    }
    o.printf("\n")
  }

  /**
   * Flips some number of bits randomly, with replacement. (I.e. not all flipped bits are guaranteed
   * to be in different positions; if you pass argument of 10 then maybe 2 bits will be flipped and
   * flipped back, and only 6 flipped once.)
   */
  fun fuzz(numErrorBits: Int): Hash256 {
    val rv = this.clone()
    for (i in 0 until numErrorBits) {
      rv.flipBit(RNG.nextInt(256))
    }
    return rv
  }

  // ================================================================
  // For Java collections: so we can make hashmaps of PDQ hashes, and so on.
  override fun hashCode(): Int {
    return w.contentHashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other is Hash256) {
      for (i in 0 until HASH256_NUM_SLOTS) {
        if (w[i] != other.w[i]) {
          return false
        }
      }
      return true
    } else {
      return false
    }
  }

  override fun compareTo(other: Hash256): Int {
    for (i in 0 until HASH256_NUM_SLOTS) {
      if (w[i] < other.w[i]) {
        return -1
      } else if (w[i] > other.w[i]) {
        return 1
      }
    }
    return 0
  }

  companion object {
    const val HASH256_NUM_SLOTS: Int = 16

    private const val IO_FORMAT = "%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x%04x"

    private val HEX_PATTERN: Pattern =
      Pattern.compile(
        "([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})" +
          "([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})" +
          "([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})" +
          "([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})"
      )

    private val RNG = Random() // for the fuzz() method

    // ----------------------------------------------------------------
    @Throws(PDQHashFormatException::class)
    fun fromHexString(s: String): Hash256 {
      val rv = Hash256()

      val matcher = HEX_PATTERN.matcher(s)
      if (matcher.find()) {
        for (i in 0 until HASH256_NUM_SLOTS) {
          val group = matcher.group(16 - i) // 1-up and reversed
          rv.w[i] = group.toInt(16).toShort()
        }
      } else {
        throw PDQHashFormatException(s)
      }

      return rv
    }

    // ----------------------------------------------------------------
    fun hammingNorm16(h: Short): Int {
      return Integer.bitCount((h.toInt()) and 0xffff)
    }
  }
}
