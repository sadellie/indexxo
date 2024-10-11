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

import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.github.jk1.license.LicenseReportExtension
import com.github.jk1.license.filter.ExcludeTransitiveDependenciesFilter
import com.github.jk1.license.importer.XmlReportImporter
import com.github.jk1.license.render.SimpleHtmlReportRenderer
import com.github.jk1.license.task.ReportTask
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
  alias(libs.plugins.com.codingfeline.buildkonfig)
  alias(libs.plugins.serialization)
  alias(libs.plugins.com.github.jk1.dependency.license.report)
  alias(libs.plugins.detekt)
}

class BuildProperties {
  val buildType = properties["buildType"]?.toString() ?: "debug"

  private val propertiesFromFile = getPropertiesFromFile(
    project.file("$buildType.properties"),
  )

  init {
    println("Build type: $buildType")
    println("Properties from file")
    propertiesFromFile.forEach { k, v -> println("$k: $v") }
  }

  private fun getFromParameterOrProperties(key: String): String {
    val propertyValue = properties[key] ?: propertiesFromFile[key]
    if (propertyValue == null) error("Missing $key")
    return propertyValue.toString()
  }

  private fun getPropertiesFromFile(file: File): Properties {
    val properties = Properties()
    InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
      properties.load(reader)
    }

    return properties
  }

  val console = getFromParameterOrProperties("console") == "true"
  val customDataFolder = getFromParameterOrProperties("customDataFolder")
  val realDelete = getFromParameterOrProperties("realDelete")
  val logSeverity = getFromParameterOrProperties("logSeverity")
  val logToFile = getFromParameterOrProperties("logToFile")
  val appVersion = libs.versions.appVersion.get()
  val appVersionName = libs.versions.appVersionName.get()
  val htmlReportName = "third-party.html"
}

val buildProperties = BuildProperties()

buildkonfig {
  packageName = "io.github.sadellie.indexxo"

  defaultConfigs {
    buildConfigField(FieldSpec.Type.STRING, "buildType", buildProperties.buildType)
    buildConfigField(FieldSpec.Type.STRING, "appVersion", buildProperties.appVersion)
    buildConfigField(FieldSpec.Type.STRING, "appVersionName", buildProperties.appVersionName)
    buildConfigField(FieldSpec.Type.STRING, "customDataFolder", buildProperties.customDataFolder)
    buildConfigField(FieldSpec.Type.BOOLEAN, "realDelete", buildProperties.realDelete)
    buildConfigField(FieldSpec.Type.INT, "logSeverity", buildProperties.logSeverity)
    buildConfigField(FieldSpec.Type.BOOLEAN, "logToFile", buildProperties.logToFile)
  }
}

kotlin {
  jvm("desktop")
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    moduleName = "composeApp"
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(project.projectDir.path)
          }
        }
      }
    }
    binaries.executable()
  }

  sourceSets {
    val desktopMain by getting
    val desktopTest by getting

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)

      implementation(libs.io.coil.kt.coil3.coil.compose)
      implementation(libs.io.coil.kt.coil3.coil.svg)

      implementation(libs.io.github.koalaplot.koalaplot.core)

      implementation(libs.io.github.vinceglb.filekit.compose)

      implementation(libs.co.touchlab.kermit)

      implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)

      implementation(libs.cafe.adriel.voyager.voyager.navigator)
      implementation(libs.cafe.adriel.voyager.voyager.screenmodel)
      implementation(libs.cafe.adriel.voyager.voyager.transitions)
      implementation(libs.cafe.adriel.voyager.voyager.tab.navigator)
    }
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.swing)

      implementation(libs.cafe.adriel.voyager.voyager.koin)

      implementation(project.dependencies.platform(libs.io.insert.koin.koin.bom))
      implementation(libs.io.insert.koin.koin.core)
      implementation(libs.io.insert.koin.koin.core.coroutines)

      implementation(libs.androidx.room.runtime)
      implementation(libs.androidx.sqlite.sqlite.bundled)

      implementation(libs.androidx.datastore.datastore.preferences.core)

      implementation(libs.org.apache.tika.tika.core)
      implementation(libs.org.apache.tika.tika.parsers.standard.pckg)

      implementation(libs.org.apache.xmlgraphics.batik.all)
      implementation(libs.com.twelvemonkeys.imageio.imageio.batik)

      val osName: String = System.getProperty("os.name")
      println("Running $osName")
      val bytedecoPlatform = when {
        osName.startsWith("Win") -> "windows-x86_64"
        osName.startsWith("Linux") -> "linux-x86_64"
        else -> throw Error("Not supported: $osName")
      }
      implementation("org.bytedeco:javacv:1.5.10")
      api("org.bytedeco:ffmpeg:6.1.1-1.5.10")
      implementation("org.bytedeco:ffmpeg:6.1.1-1.5.10:$bytedecoPlatform-gpl")

      implementation(libs.net.java.dev.jna.jna.jpms)
      implementation(libs.net.java.dev.jna.jna.platform.jpms)
      implementation(libs.window.styler)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class) implementation(compose.uiTest)
    }

    desktopTest.dependencies { implementation(compose.desktop.currentOs) }
  }

  targets.all {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
      }
    }
  }
}

dependencies { add("kspDesktop", libs.androidx.room.compiler) }

room { schemaDirectory("$projectDir/schemas") }

composeCompiler {
  featureFlags = setOf(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

compose.desktop {
  application {
    mainClass = "io.github.sadellie.indexxo.MainKt"
    jvmArgs += listOf("-Xmx6G")

    buildTypes.release.proguard {
      this.isEnabled = buildProperties.buildType == "release"
      configurationFiles.from("rules.pro")
    }

    nativeDistributions {
      packageName = "Indexxo"
      packageVersion = buildProperties.appVersion
      description = "Indexxo"
      copyright = "Copyright (C) 2024 Elshan Agaev"
      vendor = "sadellie"
      licenseFile.set(project.parent?.file("LICENSE"))

      targetFormats(TargetFormat.Msi, TargetFormat.Deb)

      modules(
        "jdk.unsupported", // Datastore https://youtrack.jetbrains.com/issue/CMP-2686
        "java.sql", // tika
      )

      appResourcesRootDir.set(project.file("include"))
      windows {
        iconFile.set(appResourcesRootDir.file("windows/logo.ico"))
        dirChooser = true
        console = buildProperties.console
        upgradeUuid = "2011BB6C-6025-4C8D-9928-B276195AD4BC"
        menu = true
        shortcut = true
      }

      linux {
        modules(
          "jdk.security.auth" // filekit https://github.com/vinceglb/FileKit/issues/107
        )
      }
    }
  }
}

detekt {
  config.setFrom("detekt-config.yml")
  source.setFrom(
    "src/commonMain",
    "src/commonTest",
    "src/desktopMain",
    "src/desktopTest",
  )

  basePath = projectDir.absolutePath
}

tasks.named<Detekt>("detekt").configure {
  this.exclude(
    "**/pdqhashing/**",
    "**/indexxo/window/**",
  )
  reports {
    xml.required.set(false)
    txt.required.set(false)
    md.required.set(false)
    sarif.required.set(false)
    html.required.set(true)
    html.outputLocation.set(file("build/reports/detekt.html"))
  }
}

licenseReport {
  unionParentPomLicenses = false
  configurations = LicenseReportExtension.ALL
  filters = arrayOf(ExcludeTransitiveDependenciesFilter())
  importers = arrayOf(
    XmlReportImporter(
      "Additional licenses",
      project.layout.projectDirectory.file("./additional_licenses.xml").asFile,
    ),
  )
  renderers = arrayOf(SimpleHtmlReportRenderer(buildProperties.htmlReportName))
  outputDir = project.layout.projectDirectory.dir("include").dir("common").toString()
}

tasks.named<ReportTask>("generateLicenseReport").configure {
  val config = this.config
  doLast {
    File(config.outputDir).listFiles()?.forEach { file ->
      if (file.name != buildProperties.htmlReportName) file.deleteRecursively()
    }

    val reportFile = File(config.outputDir).resolve(buildProperties.htmlReportName)
    val reportContent = reportFile.readLines().toMutableList()

    // rename title
    reportContent[5] = reportContent[5].replace("composeApp", "Indexxo")
    reportContent[11] = reportContent[11].replace("composeApp", "Indexxo")

    // remove timestamp
    repeat(8) {
      reportContent.removeAt(reportContent.lastIndex - 2)
    }

    val writer = reportFile.writer()
    reportContent.forEach {
      writer.write(it)
      writer.appendLine()
    }
    writer.close()
  }
}
