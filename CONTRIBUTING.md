# Contribute to Indexxo

## I found a bug or want to suggest an improvement

### Open an issue

- Check for duplicates. If found, leave a comment and I will prioritize the issue.
- Do not share personal information
- Use common sense
- Issue templates, logs and screenshots are not mandatory

#### Log files

If the app crashed, and you want to share log files:

- Open **App settings**
- Click **Data folder**
- Check `logs` folder
- Open log file
- COPY AND PASTE the text (DO NOT attach as file)
- Remove any personal information (file paths)

Paste logs **AS TEXT** in code blocks so they can be found in search, hide in spoiler if too long.

#### Screenshots

- Avoid attaching screenshots with your personal information

### Start a discussion

You can start a discussion instead of opening an issue if you feel like it would be fitting.

## I want to translate

UI is not finalized. Translation contributions are not accepted yet.

## I want to make a code contribution

Code contributions are not accepted yet, but exceptions can be made. Before you make a contribution, please open an
issue.

### How to build

Build instructions are [here](./composeApp/README.md)

### Code formatting

- `ktfmt --google-style`
- Common sense
- Trailing comma is preferred
- Do not touch icon files!

### Code analysis

This project uses `detekt`. Please try to fix reported issues if you make a contribution. It's not mandatory, but
benefits the code base.

Use `gradlew detekt` to generate a report. Check console or `./composeApp/build/reports/detekt.html`.

### Licensing

You must disclose all sources when contributing. No "AI" generated content.

### Rewrite to other language?

Kotlin is

```kotlin
fun
```

## I can't use GitHub

If you don't have a GitHub account, feel free to contact me directly. Be aware that I will probably open an issue
myself. No personal information will be shared.

## I don't like this Contributing guidelines

If you think that something here is confusing or left unexplained, feel free to open an issue.
