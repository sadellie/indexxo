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

package io.github.sadellie.indexxo.core.model

import indexxo.composeapp.generated.resources.Res
import indexxo.composeapp.generated.resources.stage_computing_hashes
import indexxo.composeapp.generated.resources.stage_duplicate_hashes_analyzing
import indexxo.composeapp.generated.resources.stage_duplicate_hashes_computing_full_hash
import indexxo.composeapp.generated.resources.stage_duplicate_names_analyzing
import indexxo.composeapp.generated.resources.stage_empty_files_analyzing
import indexxo.composeapp.generated.resources.stage_empty_folders_analyzing
import indexxo.composeapp.generated.resources.stage_indexing
import indexxo.composeapp.generated.resources.stage_similar_images_comparing
import indexxo.composeapp.generated.resources.stage_similar_videos_comparing
import indexxo.composeapp.generated.resources.stage_walking
import okio.Path

data class Walking(override val path: Path) : IndexingStage.WithPath {
  override val nameRes = Res.string.stage_walking
}

data class Indexing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_indexing
}

data class DuplicateHashesAnalyzing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_duplicate_hashes_analyzing
}

data class DuplicateHashesComputingFullHash(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_duplicate_hashes_computing_full_hash
}

data class DuplicateNamesAnalyzing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_duplicate_names_analyzing
}

data class EmptyFilesAnalyzing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_empty_files_analyzing
}

data class EmptyFoldersAnalyzing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_empty_folders_analyzing
}

data class ComputingHash(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_computing_hashes
}

data class SimilarImagesComparing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_similar_images_comparing
}

data class SimilarVideosComparing(
  override val value: Float,
  override val indexedObject: IndexedObject,
) : IndexingStage.WithValue, IndexingStage.WithIndexedObject {
  override val nameRes = Res.string.stage_similar_videos_comparing
}
