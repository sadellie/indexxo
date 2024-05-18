# Linux container

This container is used to build Indexxo for Linux from Windows host (macOS should work too).

```bash
docker-compose run --rm amd64_ubuntu
```

Build is a separate action until tested properly. In container call:

```bash
bash gradlew packageDistributionForCurrentOS
```

```bash
bash gradlew packageReleaseDistributionForCurrentOS -PbuildType=release
```
