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

package io.github.sadellie.indexxo.core.data

import io.github.sadellie.indexxo.core.model.SimilarIndexedObjectsGroup
import kotlinx.coroutines.runBlocking
import org.bytedeco.javacv.Java2DFrameConverter
import pdqhashing.hasher.PDQHasher
import pdqhashing.types.Hash256
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyzeSimilarVideoTest {
  private val chair19sdBar =
    "./testDirs/analyzeSimilarVideosTest/chair-19-sd-bar.mp4".toIndexedObject()
  private val chair20sdBar =
    "./testDirs/analyzeSimilarVideosTest/chair-20-sd-bar.mp4".toIndexedObject()
  private val chair22sdGreyBar =
    "testDirs/analyzeSimilarVideosTest/chair-22-sd-grey-bar.mp4".toIndexedObject()
  private val chair22sdSepiaBar =
    "testDirs/analyzeSimilarVideosTest/chair-22-sd-sepia-bar.mp4".toIndexedObject()
  private val chair22withLargeLogoBar =
    "testDirs/analyzeSimilarVideosTest/chair-22-with-large-logo-bar.mp4".toIndexedObject()
  private val chair22withSmallLogoBar =
    "testDirs/analyzeSimilarVideosTest/chair-22-with-small-logo-bar.mp4".toIndexedObject()
  private val chairOrig22fhdNoBar =
    "testDirs/analyzeSimilarVideosTest/chair-orig-22-fhd-no-bar.mp4".toIndexedObject()
  private val chairOrig22hdNoBar =
    "testDirs/analyzeSimilarVideosTest/chair-orig-22-hd-no-bar.mp4".toIndexedObject()
  private val chairOrig22sdBar =
    "testDirs/analyzeSimilarVideosTest/chair-orig-22-sd-bar.mp4".toIndexedObject()
  private val doorknobHdNoBar =
    "testDirs/analyzeSimilarVideosTest/doorknob-hd-no-bar.mp4".toIndexedObject()
  private val patternHdNoBar =
    "testDirs/analyzeSimilarVideosTest/pattern-hd-no-bar.mp4".toIndexedObject()
  private val patternLongerNoBar =
    "testDirs/analyzeSimilarVideosTest/pattern-longer-no-bar.mp4".toIndexedObject()
  private val patternSdGreyBar =
    "testDirs/analyzeSimilarVideosTest/pattern-sd-grey-bar.mp4".toIndexedObject()
  private val patternSdWithLargeLogoBar =
    "testDirs/analyzeSimilarVideosTest/pattern-sd-with-large-logo-bar.mp4".toIndexedObject()
  private val patternSdWithSmallLogoBar =
    "testDirs/analyzeSimilarVideosTest/pattern-sd-with-small-logo-bar.mp4".toIndexedObject()

  @Test
  fun testUniqueFrames() = runBlocking {
    val converter = Java2DFrameConverter()
    val pdqHasher = PDQHasher()

    val actual = uniqueFrames(chair19sdBar, converter, pdqHasher, 1)
    val expected = setOf(
      "e271017837246aaccddea259648fb7d62f435c89d9e99b2497763e216c8d055c",
      "8d3c659db786de9318f6d430b59c96561b623256f03c32fe5e63c4e40b887919",
      "b724abd26271c006988b0af371da9d7c7a16f6238cbc318ec233948b39d8aff6",
      "d869cd37e2d3742944a37e9a60c928ec4e1798dca56998540b166e465eddd3b3",
      "e071fe8737249453c5de5ca6248f40292f43a376d92164db976241de6c8dfaa3",
      "b724542d62713ff9988bf70c31dae2837a1609dc8cbcdf71c233eb7439d85009",
      "8d389a62b786217c18f62bcfb59c69b91b62cda9f03ccd095e633b1b0b8886e6",
      "d96d32c8e2d38bd64da38165e4c9c3134e376703a56967ab0b3691b15edd2c4c",
      "24cb66b3da70971c34bc4d0f6b2dd6d6f8d6d1a3217101e99eb9fd123c0b8154",
      "31cf9c3bcaa6e631268e22f37308e98ac9d85e4e6a7886729e1ae27ec5c63985",
      "719acc198f253db261e9a7a51c60747ca9837b097424a9434bec57b8495e2bfe",
      "649832919ff36c9b73db8859265d43209c8df4e43f2d2cf8cb4f48d49093932f",
      "24cb994cda7068e334bcb2f06b292929f8d62e5c2171fe169eb902ed380b7eab",
      "719e33e69f25c24961e9585a3e7c8383ad8384f6743454bccbeca847ed5ed401",
      "31cd67c4caa639ce268edd0e73081675c9d8a1b16a7879ad9e1a1f81c5c6c67a",
      "6490c96e9df1936472db7724244dbcdf9c8d0b1b3f2dd107c94f252b90936cd0",
      "f49a224f5b25a4b02a5e55e8ab075a6d1db6a04b5aaf35612e596667e5ebc054",
      "9a435ad8da5e5a4f2ba86b616b2cd92d94a2b2a035b66776cd6a945acae69c81",
      "a00f08e50e700e1a7f0bff40fe52f0c748e30ae10ffa9fca3b0cccc4b0ae6afe",
      "871670720f0b70e57efdc18b3c79718741f7180260e3c9dc983b1ef09bb3362b",
      "841a5db05b215b4f2a5eaa05a807a5921db65ab45aafca9e2e199990e5eb3fab",
      "a1cf771a0e70f1e5ff0b00bdfe520f3848e3f41e0ffa60347b0c3332b0be9501",
      "9243a527da5ea5b02ba8949e6b2c269294a24d5f35b69889cd6a6ba5cae6637e",
      "87160f858f0b0f1a7efd3e343e790c3841f767f560e33221983bc10f9bb3c9c0",
      "16f33b721598024f65a9c0a58e5c95661153337be6adcf1d98f431d673abc454",
      "4fb9f3ccdc661e31561854cedaf29629ac2e2c32ed6a1d223a72e8c2d3c6630d",
      "03a691d840c9a0e530fc420fdb093fcc440699d1b3f865b7cda19b7c24fe6efe",
      "526c59661d33b49b074dfe6487253c83f97b8698983fb7886f2742688493c9a7",
      "16f3c48d1598f5b065a93f5a8e5c6a991153cc84e6ad30e298f4ce2971ab3bab",
      "63a6ee2740ed7f1ab2fcbdf0db0bc033c606662eb3f89a48cda1648326fe9101",
      "07390c334866e1ce4218ab31d27269d6ac2ed3cdcd6ae2dd3a72173dd1c69cf2",
      "526c269919334b64034d019b8725c37cf97b7967983f48776f279d9784933658",
      "ad2d989cd06c449abfb86f63d68725a7d86694da1dd6988d98f988e4c447c466",
      "eadfc97c8d009507ccfc609c0e00725f5b7cdb10ad8da797f8581c4817e378e5",
      "f87a32368539ee30eaedc5c983d28f0d8f333e70488b3227cdacb24f91126ecc",
      "ab8a63d6c8543fa999a9ca365b15d8750e2971bae8c80d3dad0db6e242b6d24b",
      "ad2d6763d06c1b45bfb8109cd687da58d86661251dd6675298f9671ac4471b99",
      "f878cdc9853911cfeaed3a3683d270f28c33c98f4883cdd8cdacddb191129133",
      "eadf36838d006af8ccbc9f630e008d205b7c24efad8d5868f858e3b717e3871a",
      "bf8e9c29d855c05699e935c95bd5278a0e298e45f8d8f2c2ad2d491dc2b62db4",
      "7494b1adcbc946a19e236a53cab6c7e98f560ed41c3c3c5218c1d1c1dbca5bce",
      "6b8668f8c41077c00efb661f3ec39e78f14fdf205b20790f61233d1e0e9382f3",
      "21c10b07d69cec0bc976c0b99f436f03da03a47e496992f84d947b6b8e9ff164",
      "3ed3c2529145dd6a5baeccb56b9634d2a41a748a0c75d3a5347697b45bc62859",
      "74945e5283c9b95e9e2395ecca163a168f56f12b1c3cc7ad18c12e3edbcaa431",
      "21c1f4f8d69c13f4cb763f469f6390fcda035b8149696d074d9484948e9f0e9b",
      "6b86b707c410c83f2efb99f03fc36187f34f25df5b2086f06123c2e10e937d0c",
      "3ed33dad914522955bae334a6396cb2da41a8a750c752c5a3476684b5bc6d7a6",
      "929c9ca969dba78e93969d4e18a539983b8d339673be6330e6336631666620c7",
      "dc08cfc131ff101f6780abf098fec20ffbe1d374423e5683f5a0610c3c6b8d72",
      "e7c916032c8e0d25c68337e44df093326ef8993c26ebc99eb326cc9b33338a6d",
      "8959656b24aa9ab532d5015a8dab68a5aeb47156176bfc29a0f5cba6692e27d8",
      "929c415669db1870939662b118a5c6673b8dcc6973be9cc9e63319ce6666db38",
      "c7c9c9fc2c8eb2dac683c81b4df06ccd6e9866c326eb3661b326336433337192",
      "dc08203e31ffcfe06780540e98fe3df0fbe12403423ea97cf5a09ee33c6b628d",
      "895d8a94a4aa454a32d5fea4cdab975aaeb486a117eb03d6a0f53459693ed827",
      "91c9a5034c4c96d2a5bc0b4c12d30d915f587e92ff29780ef01f942d94feb5fc",
      "d83fdf07487f6d0025f0861816e0325edb43497c6a2fe434acbfd3e0525a3cd3",
      "c49c0fa909193c7cf0e9e1e647a6a73b0a0dd438aa7cd2a4a56a5e87e1ab1f56",
      "8d6a71a91d28c7aa70a52cb243b598f48e16e3d61d720e9ef9ea694a070f9679",
      "91c95a4c4c4c6929a5bcb4b312d3f26e5f58816dff2907f1f01f0bd294fe4a03",
      "c49cf0f61939c387f0e95e1947a658c40a0d2bc7aa7cad5ba56ae178e1abe0a9",
      "d82f20f8487992ff25f079e716e0cda19b43b68308271bcbacbf2c1f525ac32c",
      "8d7a8e561d2a185570a5d34d43b5670bce161c295f7ab161f9ea96b5070f6986",
      "da9c929d292f5893f1a3c1429a131d3939687191e3ade71e6612e261d2e3d7ae",
      "ce37638028fc243cb380dee6c23fd20ed863fb7929a72c27e1b11fcc3e0b8499",
      "0fc938377878f218a4f469e8cf46b6916c2d993ba6f84db4334748cb87b27d04",
      "9b62c92a7da98e96e6d5544c976a78a48d3651d37cf2860db4e435646a4e2e33",
      "da986d62292fa76cf1a33ebd9a13e2c439688e6ee3ad18e166121d9ed2e32851",
      "8fc9c7c8787a0dc6a4f69417cf46486c6c2d24c4b6f8b2493347b73487b282fb",
      "ce379c7f28fcdbc3b3800119c23f2df1d863048629a7d358e1b1e0313e1b7b66",
      "1b6236957da97149e6d58bb3932a865b8d36ae2c6cf27972b4e44a99424ec1cc",
      "4d269b05618cc9660c8778a7e0fa8d849f073896f1e663db763c367096734f4e",
      "53e249c4267cbb11fdc13670409f0e4e2f72edc8d62efda1221902169ef3b36d",
      "1873318f3cd963cc59d2d20db5af272eca72123ca4b3c97123699cda83a6e5e4",
      "06b7e36e732911bba89498da15caa4e47a254762837b570b774ca8bccba619c7",
      "4d2660da698c36990c878358e0fa727b9f074769f1e69c24763cc98fd673b0b1",
      "1873ca703cd99c3359d22df2b5afd8d1ca52edc3a4b3368e2369632583a61a1b",
      "53a2b639267c44eeddc1898f409ff1b12f701237962e025e2219fde99ef34c92",
      "06b71c917329ee44a894232515ca5b1b7a27b89d83fba8f4774c5743cba6e638",
      "f6e56867e26334d3307be4d9971334196161c609cc1578c1299d1cbc96b2df5e",
      "a663bb0efd9c64f8403dea99a243ff769c1e436ee886e0d00d4d3fd27a03040f",
      "e390c2cd97369e79652e4e72c3449eb334346ca39940c26b7cc8b616c3e771f4",
      "f33631a4a8cdce521568c233f71655dcc14ba9c43dd34a7a581895782f56aead",
      "b6c59798e263cb2c307b1b279613cbe6616139f6cc15973e299de34396b224a1",
      "e7b03d72f736618665aeb18dc746614c3434935c99403d947cc969e9c3e78e0b",
      "a663e4f1fd989b17403d9767a2430289941efc91e8861f2f0d4dc02d7a03fff8",
      "f336ce5b88cd31ad15683dccf716aa23c14b563b3dd3b58558186a872d565152",
      "cbe02fc46b66e6c21a6232eec3a79e5b3b71f309c6057c4128cd0c1c1e197f5e",
      "93604d8e74d9b228e99faec8ffe31d3ed6080266ae80fe78054f99d23f01fc05",
      "9eb1856e3e334c684f37984096f234f16e2459a39350d6eb7d9826364b4cd5f4",
      "c635e724218d5886bcca0462aab6b79483dfa8ccfbd554d2d01ab7786a5456af",
      "cbecd03b6b66193d1a62cd15c3a761a43bf10cf6c60583be28cdf3e31e99a0a1",
      "9eb56a913e33b3974f3767bb96f2cb0e6e24a65c935029147d9849494b4c2a0b",
      "9360b27174d90dd3e99f5137ffe3e2c1d608fd99ae800187054f662d3f0103fa",
      "c63118db218ca779bccafb9daab6486b83595733fbd5ab2d501a48876a54a950",
      "c119849c492ce5c4cbe67ceec1e692db9bf2b349c705f64128c98c1d96195f5e",
      "dbf641ce1658fb2c2c8debc809f3451e5f88e0262ec03e78e54f81da9f817e81",
      "944c2a361c794f6c8eb3d64494b32871cca719c392505ceb7d9c26b7c34cf5f4",
      "8ea3eb64411d598679d841625ca6efb40add4a8c7bd594d2b55a2b70dad4d42b",
      "c11b7f63492c1a3bdbe68319c1e67d2c9bf24cbec70529be28c973e29619a0a1",
      "944ed5c91c79b0939eb329bbb4b3d78ecea7e63c9250a3147d9cd948c34c0a0b",
      "dbf6be31145805d32c8d543709f3bae15f881fd92e80c187e54f7e250f81817e",
      "8ea3149b410da67979d8be9d5c26104b0addb5737b956b2db01ad48f4ad42bd4",
      "a3720f1cb4d1c096e03fcce31e69db219f1339c67206c606b6e3e9cd58b1f552",
      "bd9de2cca86ddc1447c69966c3b83e7a344e59b28f02477c4a04a78b9cf9d071",
      "f627a5b6e1846a3c356a66494b3c718bca46926c27536cace39603670de45ff8",
      "60c84862fd3876be129333cc96ed94d0611bf318da57add61f510d21c9ac7adb",
      "2372f0e3b4d13f69603f331c1e6924de9f13c739720639f9b6c3163258b10aad",
      "f6275a59e18495c3b56a99b64b3c8e74ca466d9327539353e396fc980de4a007",
      "bd9d1d37a86d23eb47c66699c3b8c1c5344ea64d8f0af8834a0458749cf92f8e",
      "e0c8b799fd3889411293cc3396ed6b2f611b0ce7da5752291f51f2dec9ac8524",
      "84f8f39271db7c899ef14a63b5467bcc31525abb875822177456b8f159219552",
      "ce25babd739c9c301d4665d64d709c5cf94458d28c461f02b160fce766d9e3a9",
      "d3ad59b8648fd623dbf4e0c9e033d9666447f031d21d88bd6113125b0c743ff8",
      "9f70301f26c93e9a4813cf7c182536f6ec11faf8d953bda8e435564d338c6903",
      "84f80ced71dbd3769ef1b59cb54684333152a5648758dde87456470e59216aad",
      "d1ada6c7648f79dcdbf49f36e0132e9964470feed20d77426103eda40c74c007",
      "ca25754a739c7bcf1d469a294d7063a3f944afad8c46e8fdb160031866d93c56",
      "9f70ffe826c9d965491330839825c909ec110d87db534a7fe435a9b2338cb6fc",
      "3730641bc0db7f857d8ed948836984f67672f5329b891ac5c55eeaa9cc891554",
      "276e9731d8c4638e1c3666b492b471963936de6683c41e366e2ee6e669c88dc9",
      "6265ceb1958e942f08d333a2d47c2e5c23275f98cedcb06f900b400399dcbffe",
      "723b2d9b8591c9a449638c9ec5e1cb3c6c6374ccd691b49c337b4c4c3c9d2763",
      "37309be4c0db807a5d8666f781297b0976720acd9b89e53ac55e1556cc89eaab",
      "6265354e958e6bd028dbcc5dd67cd1a33727a067cedc4f90900bbffc99dc4001",
      "276e7cced8c49cf11c36d9cb92b49e693936279983c4e1c96e2e191969c87236",
      "723bc2648591365b49637361c5e124c36c638b33d6914b63337bb3b33c9dd89c",
      "8dce353b3771c3411559d939e1f62f06dd5ee0d96e995956132326e9a261d114",
      "96c3f91e63669be605b0b6db312e606e80644d39660eb9cecce47c7ec3989391",
      "d01bbe91622469eb401c7393b42385ac980b6a633bccb3f847768c43f7347bbe",
      "c39643b47673394c50e51c71647bcac45531e783335b33e499b1d6c496cd293b",
      "814eebc432713cbe150926c6e176d0f9c95e3f366e99e6a91223d916a2612eeb",
      "d49b616e6734d615505c8c6cb4237a539c1bb59c3bcc4c47477673bcf7348441",
      "96c316f163666c9985b0e9a4312eff998264bad6660e6ef1cce48391c3987c6e",
      "c396bc4b7673c6b350e5430e647b153b5531107c335bcc1b99b1292b96cdd6c4",
      "edc6c0af1ea663f2a4c9dd59307987c68d46e06fef989916903326e96269f114",
      "cd792b9c9267d9e6a490565b31267a4ef9246039720ef9c646661e2ef1d8b1b1",
      "b8936a054bf3c848d19473f3652c2d6c58134ae5b2cd33bcc5668c43373c5bbe",
      "983c8136c733734cf5c5fcf16473d0e4ac71ca93275b536c1333b494a48d1b1b",
      "edc63f501ea69d1d84c12ea6307978390d461fb0e798e6ed9033d91662692eeb",
      "b89395fa4bf337b7d1948c0c652cd2935813b51ab2cdcc43c56673bc373ca441",
      "cdf9d4639a672619a4b0ada4312695b1f9249fc6724e16394e66e1d1f1d84e4e",
      "982c7ec9c7328cb3f1c5030e64732f1bac71356c275bac9313334b7ba48de4e4",
    ).map { Hash256.fromHexString(it) }.toSet()

    assertEquals(expected, actual)
  }

  @Test
  fun testUniqueFramesHighFrameRate() = runBlocking {
    val converter = Java2DFrameConverter()
    val pdqHasher = PDQHasher()

    val actual = uniqueFrames(chair19sdBar, converter, pdqHasher, 5)
    assertEquals(720, actual!!.size)
  }

  @Test
  fun testAlmostSimilar() = runBlocking {
    val index = listOf(
      chair19sdBar,
      chair20sdBar,
      chair22sdGreyBar,
      chair22sdSepiaBar,
      chair22withLargeLogoBar,
      chair22withSmallLogoBar,
      chairOrig22fhdNoBar,
      chairOrig22hdNoBar,
      chairOrig22sdBar,
      doorknobHdNoBar,
      patternHdNoBar,
      patternLongerNoBar,
      patternSdGreyBar,
      patternSdWithLargeLogoBar,
      patternSdWithSmallLogoBar,
    )
    val actual = analyzeSimilarVideos(
      indexedObjects = index,
      minHashSimilarity = 0.7f,
      minFrameSimilarity = 0.7f,
      framePerSecond = 5,
      callback = {},
    )
    val expected = listOf(
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          chair19sdBar,
          chair20sdBar,
          chair22sdGreyBar,
          chair22sdSepiaBar,
          chair22withSmallLogoBar,
          chairOrig22sdBar,
        ),
        totalSizeBytes = 14738193L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          patternSdGreyBar,
          patternSdWithLargeLogoBar,
          patternSdWithSmallLogoBar,
        ),
        totalSizeBytes = 3286157L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          chair20sdBar,
          chair22withLargeLogoBar,
        ),
        totalSizeBytes = 4971028L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          chairOrig22fhdNoBar,
          chairOrig22hdNoBar,
        ),
        totalSizeBytes = 11019259L,
      ),
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          patternHdNoBar,
          patternLongerNoBar,
        ),
        totalSizeBytes = 4734643L,
      ),
    )

    assertEquals(expected, actual)
  }
}
