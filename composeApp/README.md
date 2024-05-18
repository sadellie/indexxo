# Requirements

- Java: 18 (I use azul-18.0.2.1)
- IntelliJ IDEA (always latest stable)
- [Valkyrie plugin](https://plugins.jetbrains.com/plugin/24786-valkyrie--svg-to-imagevector)
- [Gradle zip](https://services.gradle.org/distributions/). Path must match `distributionUrl` specified in
  `/gradle/wrapper/gradle-wrapper.properties`

# How to run

- Open this project in **IntelliJ IDEA**
- Choose and run any configuration

Tip: Open this file in IntelliJ IDEA and use green play buttons within this document.

## Run configurations

Modify `buildType` parameter to choose different build properties ([debug](./debug.properties) by default).

| Command                                                              | Description                                                                         |
|----------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `gradlew run`                                                        | Run the app                                                                         |
| `gradlew run -PbuildType=release`                                    | Run the app. Release properties, no proguard                                        |
| `gradlew wasmJsBrowserRun`                                           | Run demo app in browser                                                             |
| `gradlew wasmJsBrowserDistribution -PbuildType=release`              | Build demo app. Release properties. Clear cache if you see white screen in browser  |
| `gradlew cleanDesktopTest desktopTest`                               | Run tests using desktop as platform                                                 |
| `gradlew packageReleaseDistributionForCurrentOS`                     | Package and minify the app for your OS. Debug properties                            |
| `gradlew packageReleaseDistributionForCurrentOS -PbuildType=release` | Package and minify the app for your OS. Release properties                          |
| `gradlew generateResourceAccessorsForCommonMain`                     | Update resource accessors. Avoid building whole project, when modifying strings.xml |
| `gradlew generateLicenseReport`                                      | Update third-party.html file. This file is included in distribution                 |
| `gradlew detekt`                                                     | Check code quality                                                                  |

### Add more configurations

- Copy `debug.properties`
- Rename to something else, for example `myConfig.properties`
- Modify properties how you want
- Run `gradlew run -PbuildType=myConfig`

Replace `myConfig` with anything

## Cross-platform packaging

See: [README.md](../ci/README.md) in CI folder.

## Properties

### `customDataFolder`

Where app data will be stored. Absolute and relative paths are supported. Leave empty to use `user.dir`

### `realDelete`

- `true` - will move files to system trash (platform dependent)
- `false` - doesn't move anything (faked with delay)

### `logSeverity`

Minimum (inclusive) message severity that will be logged (maps
to [Kermit's Severity levels](https://github.com/touchlab/Kermit/blob/e87d7da197cac4c970d7309dd6b8486a401d886b/kermit-core/src/commonMain/kotlin/co/touchlab/kermit/Severity.kt#L20)):

| level | value   |
|-------|---------|
| 0     | Verbose |
| 1     | Debug   |
| 2     | Info    |
| 3     | Warn    |
| 4     | Error   |
| 5     | Assert  |

### `logToFile`

- `true` - will write logs to `./logs` folder in `customDataFolder` and console
- `false` - will print logs to console only

### `console`

Can't be overridden in packaged executables.

- `true` - will attach console in distributable run
- `false` - won't attach console in distributable run

## Override build properties in distribution

Properties used in build are overridden by arguments passed to executable.

Example:

If the app was packaged with `realDelete = false`, pass `--realDelete true` to executable to override it.
