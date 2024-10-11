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

import io.github.sadellie.indexxo.core.common.maxSystemThreads
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

    val actual = uniqueFrames(chair19sdBar, converter, pdqHasher, 1, maxSystemThreads)
    val expected = setOf(
      "eaf1017837246aacc5dea259648fb7d62f435c89d9e98a259772be216c8d055c",
      "8d3c659db786de9310f6d4b0359c96569b623256f03c32fe5e43c4ec0b887919",
      "bfa4abd26271c006908b08f331da1d7c7e56f6238cbc208fc237948b39d8aff6",
      "d869cf33e2d3743944a37e1a60c13c7cce17987ca56998540b166e465e5dd3a3",
      "e0f1fe8737249553c5de5da6648f40292a01a376d96175da956201de6c8dfaa3",
      "b5a4542d62713ff9908bf70c31dae2837e1609dc8cb4df71c237eb7439d85009",
      "8d3c9a62b786216c90f62bcfb59c69a99b62cda9f03ccd095e633b130b8886f6",
      "d86d30c8e2d38bc6c5a381e5e0c9c303ce376783a56967ab0b3691b95edd2c5c",
      "df83b48f23d34d78169d363c6d0f6b36ce5ed88701a301e9cbb9fc12f8038154",
      "c0cf6c31670633b9938e317bad886cc6e878cf6e1538d2725e98b03ee1e61d85",
      "8ad61e257606e3c243c89c963858c19c9b0b722d54f6ab439eec56b8ad562bfe",
      "959ac49b32539913c69b8bd1f8ddc66c9d2d65c4406d78d803cd1294b4b3b72f",
      "df834b702253b2971690c9c32d0d94c9ce5e277801a3fe16cbb903edf8037eab",
      "8ad6e1da77861c3d43c9636938583e639b0b85d254f654bc9eeca947ad56d401",
      "c0cf93ce6706cc4693cede84ad88b339e878309115383d8d5e984fc1e3e6ea7a",
      "959a3964325366ecc69b642ef8dd19939d2d9a3b406d87270bcde56bb4b340d0",
      "b51a224fdb2724b0aa5e55f8a9075a691db6a04b5abf35612e196667e5fbc044",
      "ba435ad8da5e5a4d2baa6b61692c596d14a2b2a835b66355cd6a955aeae69c81",
      "a04f88650e720e1a6e0bff42fc52f0c348e30ae10fea9fcb3b4ccccda0ae6aee",
      "871670720601f0e57efdc08b3c79718701e7180260e3c9ff983f1ef0bfb3362b",
      "941a5d305b235b4f0a5eaa07a80781921db65ab45abfca1e0e199998c5fb3fbb",
      "a04f771a8e72f1e5ff0b00adfc520f3c48e3f51e0fea60347b4c3332a8ae9511",
      "aa432527da5ea5b02ba8949e692ca69214a24d5735b69caacd6a6aa5eae6637e",
      "c7160f858f0b07187efd3e343c79043801f7e7fd60e33200983fc00fbfb3c9d4",
      "610b5ec9236071a44297e75e9d32d8b39fc83aad638dcc0dcc709cc7cff96114",
      "0f9e22c4b461acbb43deb9e16ce2495c59e69b71334af0f4d4f2e38a8f043689",
      "345ef4637625db0613c24df4c8667219ca9d900736d866a79925366d9aaccbbe",
      "5acb886ee1340691168b1bcb39b7e3f60cb331db661f7a5e91a74920da519e23",
      "610ba13623708e5bc69718a19d33274c9fc8c552638d33f2cc706338cff99eeb",
      "345e0b9c763524f997c2b20bc8678de6de9d6ff836d899589925c9929aac3441",
      "079edd3bb46153c443de469e6ce2b6a359e6648e334a0f0bc4f21c758f04c976",
      "52cb7791e134f96e168be43439b71c090cb3ce24661f85a181a7b6dfda5161dc",
      "ad2d899cd06c449abff8ee63d68725a7d86694da1dd2988d98f988e4c447c466",
      "eedfc97c8d00d507ccbc609c0e0032df5b7cd310ad8da797f8581c4817e378e5",
      "f87a3336c539ee30eaed45c983d28f0d8f333e70488f3227cdaca24e91126ecc",
      "bb8a43d6c8543fad99a9ca365a5598750e2971bad8d80d3dad0db6c202b6d24f",
      "ad2d6661904c9b65bfb8109cd687d858d86663251dd2677298f9771bc4471b99",
      "f878dcc9853911cfeaadba3683d270f28d33c18f4883cdd8cdacddb191129133",
      "eedf36838d002af8ccbc9f630e00c9205b7c2cefad8d5868f858e39717e3871a",
      "bb8ebc29d855c05299e935c95bd5678a0e298e45f8d8f2c2ad2d493dc2b62db0",
      "56c9f04ba0d76ae356d2b596eb0deb44c194c1f8835386938e09be0b961e96de",
      "67ff243f730433f8130473e01b3ff81fbcd1069e1040d34cc24fd87c7c370dc3",
      "239c5ae9f582e05d03a73f3cbe5841ee94c16b52d6072c39db5c34a1e34b3c74",
      "32aa8e952657b95a4659d94ace6a52b5e984ac364d1579e6971a72d62962a769",
      "56c90fb4b0d7951c56f26a69eb0d14bbc1943e178353796c9e0961f4b65e6921",
      "019ca51ef5823fb60387c0c3be58be1194c194bdd606d3c6d35ceb5ec34bc38b",
      "67ffdbc07704cc07130c8c1f9b3f07e0bcd1f96110402cf3c24f27837c37f23c",
      "32aa716ab65766ad465926b5ce6aad4ae98453cb4d159659d71a9d29296258b6",
      "929c9ca969dbe78e93969d4e18a539983b89339e739e6330e6336631666624c6",
      "9c08cfc031fb101f6780abf0987ec20ffbe1dbfc421e5683f5e0611c3c6b8c73",
      "cfc936033c8e4d24c6c337e44df093136ed8993426cbc99ab366cc9b33338e6c",
      "895d656b24aa9ab532d5015acdab68a5aeb47156164bfc29a0b5cb36693e26d9",
      "929c435669db1870939662b118a5c6463b89cc61739e9ccfe63399ce6666db39",
      "c7c949fc2c8eb2dac6c3c81b4df06ccc6cd866cb26cb3665b366136433333193",
      "dc08303e31ffcfe06780540f98fe3df0fbe12403421ea97cf5e09e633c6b628c",
      "895d9a9464aa654ab2d5fea5cdab975aaeb48ea9164b03d6a0b53449693ec926",
      "21b3480f9787ad246b4c82c705b30b591e967fa27f0af81ef43fa029a47e2df8",
      "341eb787987fdf0759f1c9142de0049e86c1f2dc924f7c64093f8165a6fa18b3",
      "f4e6e3afe2d2078e3e192c6d50e6a1f34bc3d5092a5f52b4a56a0a83f12b8752",
      "614a1d2dcd2a75ad0ca467be78b5ae34c3945856871ad6ce0c6a29cfb3abb219",
      "21b3b6d0970752536b4c7d3805b3f4a61e96805c7f0a07e1f43f5fd4a47e5207",
      "74e61c50c252f8713e19d39250e25e0c4bc32af62a1fad4ba16af57cf12b78ad",
      "341e4878987f20f859f132eb2de0bb61a6c10d03924f839b493f7c9ae6fae70c",
      "e14be2d2ed2a8a520ca49c4978b551cbf394a7a9c71a29315c6ad630f3af4de6",
      "da9c9294292f5897f1a3c1429a131c3939787191e3ade71e6612e661d2e3d7ae",
      "ce37638028fc243cb380fee2823fd30ed863fb7929a72c2721b10fce3e1bc499",
      "0fc13836787af218a4f669e8cb46b6916c2d9b2bb6f84db4334748cb87b27d04",
      "9b62c92a7da98e96e6d55448936a79a48d3651d37cf2868d74e4a5646b4e2a33",
      "da946d6a292fa768f1a33cbc9a13e3c639788e6ee3ad18e16612199ed2e32851",
      "8fc1c5c17c7a0dc2a4f69417cf46496c6c2d24c4b6f8b24b3347b33487b682fb",
      "ce379c7f28fcdbc3b380011dc23f2cf1d863048629a7d3d8a1b1f0313e1b3b66",
      "1b6236957da97169e6d589b3936a865b8d36ae2c6cf2793234e44a996a4e81cc",
      "341e4b25360d6926c1070c077863e5c6cfa79c9638f671e333be1e3652724f6e",
      "09f2a46cb3385d8156e55a3a608fa66605f8fccc52bf7f91a00902129fff916f",
      "614be18f6358c38c9052a62d2d364f6c9af2363c6da3db4966ebb49c0727e5c4",
      "5c850ec6e66df72b03b0f09035da0ccc50ad566607ead53bf55ca8b8caaa3bc5",
      "341eb4da360d96d9c107f37878631a39cfa7636938f60e1c33bee1c95272b091",
      "614b1e7063583c73945259d22d36b0939af2c9c3eda3a4b666eb4b6387271a3b",
      "09c05b93b338a27e56e5a5c5608f599901f8033352bf806ea009fded9fff6e90",
      "5c95f139e66d08d403b0076f35daf33350ada99907ea2ac4f55c5747caaac43a",
      "f7c56867e26334d3307be4d996113c196169c609cc1578c1299d1cbc96b2db7e",
      "a663bb0efd9c64f8413d6c99aa43ff76941e436e6887e0d00dcd3fd27c030807",
      "e390c28db7369e79652e4e73c34496b334346ca39940d26b7cc8b616c3e771d4",
      "f33631a4a8cdce521468c233f716d5dcc14fe9c43dd24a7a589895782d56aaad",
      "b7c597d8e263cb2c307b1b269611c3e6616939f6cc15973e299de3c396b22481",
      "e3903d72f7366186672fb58ec344694c343c935c99403d947cc869e9c3e78e2b",
      "a663e4f1fd989b17413d9766a2438089941efc9168871f2f0dcdc02d7803fff8",
      "f336ce5ba8cd31bd14683dccf716aa23c14b163b3dd2b58558186a872d565552",
      "c18c05cc6926e7c2df663aeac3e79a5b1bf1f309e6057c412cc98c1c96196b5e",
      "9b66458e3479f22c2d9fa7c11fe3053ede88a0662e807e78c54f15da3f01fc05",
      "94c9af663c734d688ab3904096b230f1cea459a3b350d6eb799c26b6cb4cd5f4",
      "ce33ef24612c588678c80d6a4ab6af948bdd0acc7bd5d4d2901abf706a5456af",
      "c19cfa336926183ddfe6c515c3e765a41bf10cf6e60583be2cc973e3961980a1",
      "94c950993c73b2978ab36fbf96b2cf0e4ea4a65cb3502914799cd949cb4c2e0b",
      "9b66ba7134790dd22d9d583e1fe3fac1d6885f992e800187c54fea253f0103fa",
      "ceb310db612ca77978caf2954ab6506b8bddf5337bd52b2d901a408f6a54a950",
      "c059849c492ce5c4cfe67ce6e1e682db9bf2f349c705f64128c98c1d96195b5e",
      "dbf641ce1658fb2c2c8defc909f3c51e5f88e0262e803e78e14f81da0f817f81",
      "950c2a361c794f6c9ab3d24cb4b328704ea719e392505ceb7d9c26b7c34cf1f4",
      "8ea3eb64431d518679d80d635ca66fb40add4a8c7bd594d2b51a2b705ad4d52b",
      "c0197b63492c1a3bcfe68719e1e67d2d9bf24cb6c70529be28c973e29619a4a1",
      "954cd5c91c79b0919eb32db3b4b3d78fcea7e61c9250a3147d9cd948c34c0e0b",
      "dbf6be71165804d32c8d5c3609f33ae15f889fd92e80c187e14f7e250f81817e",
      "8e23149b430dae7979d8b29c5ca6904b0add35737bd56b2db41ad48f4ad42ad4",
      "1393cb467e4c3e79131cc3f984ed786dc7199fd839c0ee06264332cdd1a9a552",
      "46d39b6d313d47997170cd267cdc331c866698d21702e73e3fc694ebc0197871",
      "4ed663ec2b1994f346496973d1b8d2c7964c35726c9544ac7316986784fc0ff8",
      "138631c7646ced33a425678ce98999b6d333b2785657cd946a933e41954cd2db",
      "1bd336b97e4cc1e7131c3c3684edd792c719606739c011f92643ed32d1a95aad",
      "44d69e132b196b6d464996bcd1b87d38924ccacd6c95bb537316679884fcf007",
      "46d36592313db966717032d9fcdccce38666776d17029cc13fc66b14c019978e",
      "1386cf38646813cca42598f3e9896649d333ddc75657b66b6a93c1be954c3d24",
      "84f8f33371db7c898ef04a23b5467bec35527abb87582205745eb8f15a219552",
      "ca25aab573dc9c301d4665564d729c5eb9445c52cc461712b168fce766c9eba9",
      "d3ad79b9649fd663dbe5e089e013d1667007f011d21d88af210b125b0f743ff8",
      "9f70201f2689369a4813cffc182736fcec11fef89913bdb8e43d564d339c6103",
      "84f80ccd71db93768ef0b5dcb5468433355285448758ddfa755e470e5a216aad",
      "d1ada667648f39dcdbe51f76e0132e9960072feed20d7750210beda40f74c007",
      "ca25754a73dc73cf1d469aa94d7263a1b944abadcd46e8edb168031866c97456",
      "9f70ffe066c9d9e5481330039827c90bec111907d9136267e43da9b2339cfefc",
      "13b0fc1bf05a33857fc75941c029d47e33765d3a0fd98ae5f44e6ea16c01155c",
      "731890b178ce7736cc76b690d8b4631e9836d24683d41e3e6769e66669c8e9c9",
      "46e546b0a50f892f2a92e3eb957c7ed46623f7905a8c204fa11bc40b3954bff6",
      "364d3a1b2ddbd89c19631c3acde1c9b4cd6178ecd681b4b4363c4ccc3c9d4363",
      "13b013e5e05adc7a7fc6a6bec0292b813376aac50fd9751af44e915e6c01eaa3",
      "4ee5bb4fb50ff6d02a931c1c957c912b6623286f5a9cdfb0a11b3bf439d44009",
      "63186f4e788efdc94c76496f98b49ce198342db983d4e1e16369199969c85e36",
      "364dc5e42ddb03631923e3c5cde1364bcd618713d6814b4b363cb3333c9db49c",
      "c1ce353b37f1c3411549d939e1762f06dd5ec1d96eb97956132326e9a261d114",
      "96c3f91e23369be605b0b6db312e606f80644d39662e99cecce47c7ec3988791",
      "d41bbe91632429eb401c7393b42385ac8c0b6a633bccb3f847768c43f7147bbe",
      "c39643b47673314c50e53c71647bcac45531ef833359336499b1d6d496cd293b",
      "814eebc436713cbe054926c6e176d0f9c94e3f366e99e6ad1223d916a2412eeb",
      "d49b416e6724d614505c8c6db4237a539c1b959c3bec6c47477673bcf7348541",
      "96c396f163366c9905b069a4312effd9a064bad6662e66f1cce48391c3987c6e",
      "c396ac4b7673c63350e5c38e647b353b5531107c3359cc1b99b1292b96cdd6c4",
      "fee0c08f9eae7aa666c7dfd9307927960f46c04ee5f89d54901326e96269d014",
      "e479239c9b2649e6b490524bbd86fa4ffd247109b226f8c66666862e79c8b1f1",
      "abb56a254bfad00c31927573652c0d3c5a134ae4b08d37fec5468c43373c7abe",
      "b12c8936ce73e34ce1c5f8e1e0d340e5a871dba3e773526c33332c842c9d1b5b",
      "fee03f709eae845964c720263079d8690f463fb1e5f862ab9013d91662692feb",
      "abb595dacbfb2ff333928a8c652c72c35a13951bb0adc801c54673bc373c8541",
      "e479dc639b26b619b490adb4bd8605b0fd248ef6b2260739666679d179c84e0e",
      "902c76c9ce331cb3e1c5071ee0d3af1aa871245ce753ad933333d37b2c9dc4a4",
      "25e6f1861a33b6f1e25d5f0919b9f0b60d4696abe05c4fc2415e903dbf6165c4",
      "5966bacedb23c9b926927b2c3c523b64d351674cb346c4d30e6c3652e1d8c1b9",
      "7093db2c4f661c5bf708f5a34dec5a1c58133c01b509e568544b3a97ea34cf6e",
      "4c33186c8c76631373c7d186690793ce8604cdeee6136e795b399cf8b48d6b13",
      "25e69e791a334d0ee25da0f619b91f490d466954e05cb03d415e6fc2bf619a3b",
      "70b3b4d34f66e7a4f7080a5c6decb5e35813c3feb5091a97544bc568ea343091",
      "59664d39d9237746269294d33c52c69bd35198bbb3463b2c0e6cc9ade1d83e46",
      "4c33ef9b8c76ddec73c73e7969076e3186043219661391865b396307b48d94ec",
    ).map { Hash256.fromHexString(it) }.toSet()

    assertEquals(expected, actual)
  }

  @Test
  fun testUniqueFramesHighFrameRate() = runBlocking {
    val converter = Java2DFrameConverter()
    val pdqHasher = PDQHasher()

    val actual = uniqueFrames(chair19sdBar, converter, pdqHasher, 5, maxSystemThreads)
    assertEquals(752, actual.size)
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
      framePerSecond = 10,
      maxThreads = maxSystemThreads,
      callback = {},
    )
    val expected = listOf(
      SimilarIndexedObjectsGroup(
        duplicates = listOf(
          chair19sdBar,
          chair20sdBar,
          chair22sdGreyBar,
          chair22sdSepiaBar,
          chair22withLargeLogoBar,
          chair22withSmallLogoBar,
          chairOrig22sdBar,
        ),
        totalSizeBytes = 17358916L,
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
