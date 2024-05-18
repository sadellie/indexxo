// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.utils

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess
import pdqhashing.types.Hash256
import pdqhashing.types.Hash256AndMetadata
import pdqhashing.types.PDQHashFormatException

/**
 * Hashes with metadata
 *
 * If zero filenames are provided, stdin is read. Files should have one hex-formatted 256-bit hash
 * per line, optionally prefixed by "hash=". If a comma and other text follows the hash, it is used
 * as metadata; else, a counter is used as the metadata.
 *
 * Example:
 * ```
 * f8f8f0cce0f4e84d0e370a22028f67f0b36e2ed596623e1d33e6339c4e9c9b22
 * b0a10efd71cc3f429413d48d0ffffe12e34e0e17ada952a9d29684210aa9e5af
 * adad5a64b5a142e55362a09057dacd5ae63b847fc23794b766b319361fc93188
 * a5f4a457a48995e8c9065c275aaa5498b61ba4bdf8fcf80387c32f8b0bfc4f05
 * f8f80f31e0f417b00e37f5cd028f980fb36ed02a9662c1e233e6cc634e9c64dd
 * 8dad2599b1a1bd1853625f6553da32a1e63b7280c2374b4866b366c91bc9ce77
 * f0a1f102f1dcc0bd9c5309720fff018de34ef1e8ada9a956d2967ade0ea91a50
 * a5f05ba8a4896a17c106a3da5aaaab07b61b5b42f8fc07fc83c3d0740bfcb0fa
 * ```
 *
 * Example:
 * ```
 * f8f8f0cce0f4e84d0e370a22028f67f0b36e2ed596623e1d33e6339c4e9c9b22,file1.jpg
 * b0a10efd71cc3f429413d48d0ffffe12e34e0e17ada952a9d29684210aa9e5af,file2.jpg
 * adad5a64b5a142e55362a09057dacd5ae63b847fc23794b766b319361fc93188,file3.jpg
 * a5f4a457a48995e8c9065c275aaa5498b61ba4bdf8fcf80387c32f8b0bfc4f05,file4.jpg
 * f8f80f31e0f417b00e37f5cd028f980fb36ed02a9662c1e233e6cc634e9c64dd,file5.jpg
 * 8dad2599b1a1bd1853625f6553da32a1e63b7280c2374b4866b366c91bc9ce77,file6.jpg
 * f0a1f102f1dcc0bd9c5309720fff018de34ef1e8ada9a956d2967ade0ea91a50,file7.jpg
 * a5f05ba8a4896a17c106a3da5aaaab07b61b5b42f8fc07fc83c3d0740bfcb0fa,file8.jpg
 * ```
 *
 * Example:
 * ```
 * hash=f8f8...9b22,norm=128,delta=0,quality=100,filename=file1.jpg
 * hash=b0a1...e5af,norm=128,delta=124,quality=100,filename=file2.jpg
 * hash=adad...3188,norm=128,delta=122,quality=100,filename=file3.jpg
 * hash=a5f4...4f05,norm=128,delta=118,quality=100,filename=file4.jpg
 * hash=f8f8...64dd,norm=128,delta=124,quality=100,filename=file5.jpg
 * hash=8dad...ce77,norm=128,delta=122,quality=100,filename=file6.jpg
 * hash=f0a1...1a50,norm=128,delta=124,quality=100,filename=file7.jpg
 * hash=a5f0...b0fa,norm=128,delta=124,quality=100,filename=file8.jpg
 * ```
 */
object HashReaderUtil {
  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashAndMetadataFromStream(
    reader: BufferedReader,
    lineCounter: Int,
  ): Hash256AndMetadata<String?>? {
    val prefix = "hash="
    val line = reader.readLine()
    val hash: Hash256
    val metadata: String

    if (line == null) {
      return null
    }

    // Split hash from metadata on comma
    val pair = line.split(",".toRegex(), limit = 2).toTypedArray()
    metadata =
      if (pair.size == 1) {
        "idx=$lineCounter"
      } else {
        pair[1]
      }
    hash =
      if (pair[0].startsWith(prefix)) {
        Hash256.fromHexString(pair[0].replace(prefix, ""))
      } else {
        Hash256.fromHexString(pair[0])
      }
    return Hash256AndMetadata(hash, metadata)
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesAndMetadataFromStream(
    reader: BufferedReader,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    while (true) {
      val counter = vectorOfPairs.size + 1
      val pair = loadHashAndMetadataFromStream(reader, counter) ?: break
      vectorOfPairs.add(pair)
    }
  }

  fun loadHashesAndMetadataFromStreamOrDie(
    programName: String?,
    reader: BufferedReader,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    try {
      loadHashesAndMetadataFromStream(reader, vectorOfPairs)
    } catch (e: IOException) {
      System.err.printf("%s: could not read hashes from input stream.\n", programName)
      System.exit(1)
    } catch (e: PDQHashFormatException) {
      System.err.printf(
        "%s: could not parse hash \"%s\" from input stream.\n",
        programName,
        e.unacceptableInput,
      )
      System.exit(1)
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesAndMetadataFromFile(
    filename: String,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    val reader = BufferedReader(FileReader(filename))
    loadHashesAndMetadataFromStream(reader, vectorOfPairs)
    reader.close()
  }

  fun loadHashesAndMetadataFromFileOrDie(
    programName: String?,
    filename: String,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    try {
      loadHashesAndMetadataFromFile(filename, vectorOfPairs)
    } catch (e: IOException) {
      System.err.printf("%s: could not read hashes from file \"%s\".\n", programName, filename)
      System.exit(1)
    } catch (e: PDQHashFormatException) {
      System.err.printf(
        "%s: malformed hash \"%s\" in file \"%s\".\n",
        programName,
        e.unacceptableInput,
        filename,
      )
      System.exit(1)
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesAndMetadataFromFiles(
    filenames: Array<String>,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    if (filenames.isEmpty()) {
      val reader = BufferedReader(InputStreamReader(System.`in`))
      loadHashesAndMetadataFromStream(reader, vectorOfPairs)
    } else {
      for (filename in filenames) {
        loadHashesAndMetadataFromFile(filename, vectorOfPairs)
      }
    }
  }

  fun loadHashesAndMetadataFromFilesOrDie(
    programName: String?,
    filenames: Array<String>,
    vectorOfPairs: Vector<Hash256AndMetadata<String?>?>,
  ) {
    if (filenames.isEmpty()) {
      val reader = BufferedReader(InputStreamReader(System.`in`))
      loadHashesAndMetadataFromStreamOrDie(programName, reader, vectorOfPairs)
    } else {
      for (filename in filenames) {
        loadHashesAndMetadataFromFileOrDie(programName, filename, vectorOfPairs)
      }
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashFromStream(reader: BufferedReader): Hash256? {
    val line = reader.readLine() ?: return null
    return Hash256.fromHexString(line)
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesFromStream(reader: BufferedReader, vectorOfHashes: Vector<Hash256?>) {
    while (true) {
      val hash = loadHashFromStream(reader) ?: break
      vectorOfHashes.add(hash)
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesFromFile(filename: String, vectorOfHashes: Vector<Hash256?>) {
    val reader = BufferedReader(FileReader(filename))
    loadHashesFromStream(reader, vectorOfHashes)
    reader.close()
  }

  fun loadHashesFromFileOrDie(
    programName: String?,
    filename: String,
    vectorOfHashes: Vector<Hash256?>,
  ) {
    try {
      loadHashesFromFile(filename, vectorOfHashes)
    } catch (e: IOException) {
      System.err.printf("%s: could not read hashes from file \"%s\".\n", programName, filename)
      System.exit(1)
    } catch (e: PDQHashFormatException) {
      System.err.printf(
        "%s: could not parse hash \"%s\" from file \"%s\".\n",
        programName,
        e.unacceptableInput,
        filename,
      )
      System.exit(1)
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesFromStdin(vectorOfHashes: Vector<Hash256?>) {
    val reader = BufferedReader(InputStreamReader(System.`in`))
    loadHashesFromStream(reader, vectorOfHashes)
  }

  fun loadHashesFromStdinOrDie(programName: String?, vectorOfHashes: Vector<Hash256?>) {
    try {
      val reader = BufferedReader(InputStreamReader(System.`in`))
      loadHashesFromStream(reader, vectorOfHashes)
    } catch (e: IOException) {
      System.err.printf("%s: could not read hashes from standard input.\n", programName)
      System.exit(1)
    } catch (e: PDQHashFormatException) {
      System.err.printf(
        "%s: could not parse hash \"%s\" from standard input.\n",
        programName,
        e.unacceptableInput,
      )
      exitProcess(1)
    }
  }

  // ----------------------------------------------------------------
  @Throws(IOException::class, PDQHashFormatException::class)
  fun loadHashesFromFiles(filenames: Array<String>, vectorOfHashes: Vector<Hash256?>) {
    if (filenames.isEmpty()) {
      loadHashesFromStdin(vectorOfHashes)
    } else {
      for (filename in filenames) {
        loadHashesFromFile(filename, vectorOfHashes)
      }
    }
  }

  fun loadHashesFromFilesOrDie(
    programName: String?,
    filenames: Array<String>,
    vectorOfHashes: Vector<Hash256?>,
  ) {
    if (filenames.isEmpty()) {
      loadHashesFromStdinOrDie(programName, vectorOfHashes)
    } else {
      for (filename in filenames) {
        loadHashesFromFileOrDie(programName, filename, vectorOfHashes)
      }
    }
  }
}
