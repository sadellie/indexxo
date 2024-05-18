// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.indexer

import java.io.PrintStream
import java.util.*
import pdqhashing.types.Hash256
import pdqhashing.types.Hash256AndMetadata
import pdqhashing.types.MIHDimensionExceededException

/**
 * See hashing/pdq/README-MIH.md in this repo for important information regarding parameter
 * selection and performance.
 */
class MIH256<Metadata> {
  // ----------------------------------------------------------------
  // MIH data:
  // 1. Array of all hashes+metadata in the index.
  // Vector<>
  private val _allHashesAndMetadatas = Vector<Hash256AndMetadata<Metadata>>()

  // 2. For each slot index i=0..15:
  //      For each of up to 65,536 possible slot values v at that index:
  //        Hashset of indices within the _allHashesAndMetadatas array of all hashes
  //        having slot value v at slot index i.
  private val _slotValuesToIndices = Vector<MutableMap<Short, Vector<Int>>>()

  // ----------------------------------------------------------------
  init {
    for (i in 0 until Hash256.HASH256_NUM_SLOTS) {
      val element: MutableMap<Short, Vector<Int>> = HashMap()
      _slotValuesToIndices.add(element)
    }
  }

  fun size(): Int {
    return _allHashesAndMetadatas.size
  }

  // ---------------------------------------------------------------
  // BULK HASH INSERTION
  fun insertAll(pairs: Vector<Hash256AndMetadata<Metadata>>) {
    for (pair in pairs) {
      insert(pair.hash, pair.metadata)
    }
  }

  // ---------------------------------------------------------------
  // HASH INSERTION
  fun insert(hash: Hash256, metadata: Metadata) {
    val sizeBeforeInsert = _allHashesAndMetadatas.size

    for (i in 0 until Hash256.HASH256_NUM_SLOTS) {
      val slotValue = hash.w[i]
      val indicesForSlotValue = _slotValuesToIndices[i]
      if (!indicesForSlotValue.containsKey(slotValue)) {
        indicesForSlotValue[slotValue] = Vector()
      }
      val index = sizeBeforeInsert
      indicesForSlotValue[slotValue]!!.add(index)
    }

    _allHashesAndMetadatas.add(Hash256AndMetadata(hash, metadata))
  }

  // ----------------------------------------------------------------
  // HASH QUERY FOR ALL MATCHES
  //
  // MIH query algorithm:
  // Given needle hash n
  // For each slot index i:
  //   Get slot value v of n at index i
  //     Find the array indices of hashes in the MIH whose i'th slot value
  //     is within slotwise distance of v. Do this by finding all the
  //     nearest-neighbor values w of v and finding the indices of all
  //     hashes having value w at slot index i.
  @Throws(MIHDimensionExceededException::class)
  fun queryAll(needle: Hash256, d: Int, matches: Vector<Hash256AndMetadata<Metadata>?>) {
    val indices: MutableSet<Int> = HashSet()
    val slotwise_d = d / 16 // Floor of d/16; see comments at top of file.

    // Find candidates
    for (i in 0 until Hash256.HASH256_NUM_SLOTS) {
      val slotValue = needle.w[i]
      val indicesForSlotValue: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
      when (slotwise_d) {
        0 -> queryAll0(slotValue, indicesForSlotValue, indices)
        1 -> queryAll1(slotValue, indicesForSlotValue, indices)
        2 -> queryAll2(slotValue, indicesForSlotValue, indices)
        3 -> queryAll3(slotValue, indicesForSlotValue, indices)
        else ->
          throw MIHDimensionExceededException(
            String.format(
              "PDQ MIH queryAll: distance threshold %d out of bounds 0..%d. Please use linear search.",
              d,
              MIH_MAX_D,
            )
          )
      }
    }

    // Prune candidates
    for (index in indices) {
      val pair = _allHashesAndMetadatas[index]
      val hash = pair.hash
      val metadata = pair.metadata
      if (hash.hammingDistance(needle) <= d) {
        matches.add(pair) // Not: not cloned, for safe of performance
      }
    }
  }

  private fun queryAllNeighborAux(
    neighbor: Short,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indices: MutableSet<Int>,
  ) {
    if (indicesForSlotValue.containsKey(neighbor)) {
      for (index in indicesForSlotValue[neighbor]!!) {
        indices.add(index)
      }
    }
  }

  private fun queryAll0(
    neighbor0: Short,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indices: MutableSet<Int>,
  ) {
    queryAllNeighborAux(neighbor0, indicesForSlotValue, indices)
  }

  private fun queryAll1(
    neighbor0: Short,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indices: MutableSet<Int>,
  ) {
    queryAllNeighborAux(neighbor0, indicesForSlotValue, indices)
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      queryAllNeighborAux(neighbor1.toShort(), indicesForSlotValue, indices)
    }
  }

  private fun queryAll2(
    neighbor0: Short,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indices: MutableSet<Int>,
  ) {
    queryAllNeighborAux(neighbor0, indicesForSlotValue, indices)
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      queryAllNeighborAux(neighbor1.toShort(), indicesForSlotValue, indices)
      for (i2 in i1 + 1..15) {
        val neighbor2 = neighbor1 xor (1 shl i2)
        queryAllNeighborAux(neighbor2.toShort(), indicesForSlotValue, indices)
      }
    }
  }

  private fun queryAll3(
    neighbor0: Short,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indices: MutableSet<Int>,
  ) {
    queryAllNeighborAux(neighbor0, indicesForSlotValue, indices)
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      queryAllNeighborAux(neighbor1.toShort(), indicesForSlotValue, indices)
      for (i2 in i1 + 1..15) {
        val neighbor2 = neighbor1 xor (1 shl i2)
        queryAllNeighborAux(neighbor2.toShort(), indicesForSlotValue, indices)
        for (i3 in i2 + 1..15) {
          val neighbor3 = neighbor2 xor (1 shl i3)
          queryAllNeighborAux(neighbor3.toShort(), indicesForSlotValue, indices)
        }
      }
    }
  }

  // ----------------------------------------------------------------
  // HASH QUERY FOR ANY MATCHES
  @Throws(MIHDimensionExceededException::class)
  fun queryAny(needle: Hash256, d: Int): Hash256AndMetadata<Metadata>? {
    val indicesChecked = BitSet(_allHashesAndMetadatas.size)
    val slotwise_d = d / 16 // Floor of d/16; see comments at top of file.

    when (slotwise_d) {
      0 -> {
        var i = 0
        while (i < Hash256.HASH256_NUM_SLOTS) {
          val slotValue = needle.w[i]
          val indicesForSlotValue: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
          val pair = queryAny0(slotValue, needle, d, indicesForSlotValue, indicesChecked)
          if (pair != null) {
            return pair
          }
          i++
        }
        return null
      }

      1 -> {
        var i = 0
        while (i < Hash256.HASH256_NUM_SLOTS) {
          val slotValue = needle.w[i]
          val indicesForSlotValue: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
          val pair = queryAny1(slotValue, needle, d, indicesForSlotValue, indicesChecked)
          if (pair != null) {
            return pair
          }
          i++
        }
        return null
      }

      2 -> {
        var i = 0
        while (i < Hash256.HASH256_NUM_SLOTS) {
          val slotValue = needle.w[i]
          val indicesForSlotValue: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
          val pair = queryAny2(slotValue, needle, d, indicesForSlotValue, indicesChecked)
          if (pair != null) {
            return pair
          }
          i++
        }
        return null
      }

      3 -> {
        var i = 0
        while (i < Hash256.HASH256_NUM_SLOTS) {
          val slotValue = needle.w[i]
          val indicesForSlotValue: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
          val pair = queryAny3(slotValue, needle, d, indicesForSlotValue, indicesChecked)
          if (pair != null) {
            return pair
          }
          i++
        }
        return null
      }

      else ->
        throw MIHDimensionExceededException(
          String.format(
            "PDQ MIH queryAny: distance threshold %d out of bounds 0..%d. Please use linear search.",
            d,
            MIH_MAX_D,
          )
        )
    }
  }

  private fun queryAnyNeighborAux(
    neighbor: Short,
    needle: Hash256,
    d: Int,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indicesChecked: BitSet,
  ): Hash256AndMetadata<Metadata>? {
    val v = indicesForSlotValue[neighbor]
    if (v != null) {
      for (index in v) {
        val pair = _allHashesAndMetadatas[index]
        if (!indicesChecked[index]) {
          if (pair.hash.hammingDistanceLE(needle, d)) {
            return pair
          }
        }
        indicesChecked.set(index)
      }
    }
    return null
  }

  private fun queryAny0(
    neighbor0: Short,
    needle: Hash256,
    d: Int,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indicesChecked: BitSet,
  ): Hash256AndMetadata<Metadata>? {
    val pair = queryAnyNeighborAux(neighbor0, needle, d, indicesForSlotValue, indicesChecked)
    if (pair != null) {
      return pair
    }
    return null
  }

  private fun queryAny1(
    neighbor0: Short,
    needle: Hash256,
    d: Int,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indicesChecked: BitSet,
  ): Hash256AndMetadata<Metadata>? {
    var pair = queryAnyNeighborAux(neighbor0, needle, d, indicesForSlotValue, indicesChecked)
    if (pair != null) {
      return pair
    }
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      pair =
        queryAnyNeighborAux(neighbor1.toShort(), needle, d, indicesForSlotValue, indicesChecked)
      if (pair != null) {
        return pair
      }
    }
    return null
  }

  private fun queryAny2(
    neighbor0: Short,
    needle: Hash256,
    d: Int,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indicesChecked: BitSet,
  ): Hash256AndMetadata<Metadata>? {
    var pair = queryAnyNeighborAux(neighbor0, needle, d, indicesForSlotValue, indicesChecked)
    if (pair != null) {
      return pair
    }
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      pair =
        queryAnyNeighborAux(neighbor1.toShort(), needle, d, indicesForSlotValue, indicesChecked)
      if (pair != null) {
        return pair
      }
      for (i2 in i1 + 1..15) {
        val neighbor2 = neighbor1 xor (1 shl i2)
        pair =
          queryAnyNeighborAux(neighbor2.toShort(), needle, d, indicesForSlotValue, indicesChecked)
        if (pair != null) {
          return pair
        }
      }
    }
    return null
  }

  private fun queryAny3(
    neighbor0: Short,
    needle: Hash256,
    d: Int,
    indicesForSlotValue: Map<Short, Vector<Int>>,
    indicesChecked: BitSet,
  ): Hash256AndMetadata<Metadata>? {
    var pair = queryAnyNeighborAux(neighbor0, needle, d, indicesForSlotValue, indicesChecked)
    if (pair != null) {
      return pair
    }
    for (i1 in 0..15) {
      val neighbor1 = neighbor0.toInt() xor (1 shl i1)
      pair =
        queryAnyNeighborAux(neighbor1.toShort(), needle, d, indicesForSlotValue, indicesChecked)
      if (pair != null) {
        return pair
      }
      for (i2 in i1 + 1..15) {
        val neighbor2 = neighbor1 xor (1 shl i2)
        pair =
          queryAnyNeighborAux(neighbor2.toShort(), needle, d, indicesForSlotValue, indicesChecked)
        if (pair != null) {
          return pair
        }
        for (i3 in i2 + 1..15) {
          val neighbor3 = neighbor2 xor (1 shl i3)
          pair =
            queryAnyNeighborAux(neighbor3.toShort(), needle, d, indicesForSlotValue, indicesChecked)
          if (pair != null) {
            return pair
          }
        }
      }
    }
    return null
  }

  // ----------------------------------------------------------------
  // LINEAR SEARCH
  fun bruteForceQueryAll(needle: Hash256?, d: Int, matches: Vector<Hash256AndMetadata<Metadata>?>) {
    for (pair in _allHashesAndMetadatas) {
      if (pair.hash.hammingDistance(needle!!) <= d) {
        matches.add(pair)
      }
    }
  }

  // ----------------------------------------------------------------
  // LINEAR SEARCH
  fun bruteForceQueryAny(needle: Hash256?, d: Int): Hash256AndMetadata<Metadata>? {
    for (pair in _allHashesAndMetadatas) {
      if (pair.hash.hammingDistanceLE(needle!!, d)) {
        return pair
      }
    }
    return null
  }

  // ----------------------------------------------------------------
  // OPS/REGRESSION ROUTINE
  fun dump(o: PrintStream) {
    o.printf("ALL HASHES:\n")
    for (pair in _allHashesAndMetadatas) {
      o.printf("%s\n", pair.hash.toString())
      o.flush()
    }
    o.printf("MULTI-INDICES:\n")
    for (i in 0 until Hash256.HASH256_NUM_SLOTS) {
      o.printf("\n")
      o.printf("--------------- slot_index=%d\n", i)
      val ati: Map<Short, Vector<Int>> = _slotValuesToIndices[i]
      for ((slotValue, indices) in ati) {
        o.printf("slot_value=%04x\n", slotValue.toInt())
        for (index in indices) {
          o.printf("  %d\n", index)
          o.flush()
        }
        o.flush()
      }
      o.flush()
    }
  }

  companion object {
    private const val MIH_MAX_D = 63
    private const val MIH_MAX_SLOTWISE_D = 3
  }
}
