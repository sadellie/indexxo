// ================================================================
// Copyright (c) Meta Platforms, Inc. and affiliates.
// ================================================================
package pdqhashing.types

/** Little container for multiple-value method returns in Java. */
class HashesAndQuality // Note: references not copies
(
  var hash: Hash256,
  var hashRotate90: Hash256,
  var hashRotate180: Hash256,
  var hashRotate270: Hash256,
  var hashFlipX: Hash256,
  var hashFlipY: Hash256,
  var hashFlipPlus1: Hash256,
  var hashFlipMinus1: Hash256,
  var quality: Int,
)
