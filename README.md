# UI Analyzer

UI Analyzer is a Kotlin tool for finding UI quality issues in Android projects. It can analyze XML layouts, Jetpack Compose source code, and runtime UI snapshots captured from an emulator or device.

The project provides two entry points:

- CLI analyzer for scripts and local checks.
- Compose Desktop application for interactive analysis, rule selection, filtering, and report browsing.

## Features

- Static XML analysis for layout structure, text, colors, dimensions, touch targets, and adaptive consistency rules.
- Static Compose analysis for components, modifiers, colors, text styles, spacing, touch targets, and accessibility-related issues.
- Runtime analysis from ADB or a saved UI snapshot.
- Runtime package guard: if the captured screen belongs to another app, runtime analysis stops after a diagnostic warning to avoid noisy false positives.
- JSON and Markdown report generation.
- Desktop UI with static/runtime modes, rule drawer, filters, issue details, and theme switching.

## Requirements

- JDK 24.
- Android SDK platform tools if you want to capture runtime snapshots with ADB.
- Windows, macOS, or Linux for the desktop app. Native file dialogs are handled through FileKit.

## CLI Usage

General form:

```powershell
.\gradlew.bat :cli:run --args="<android-project-path> [output-file] [--rules=all|static|xml|compose|runtime] [runtime-snapshot-json|--runtime-adb] [adb-serial]"
```

On Unix-like shells:

```bash
./gradlew :cli:run --args="<android-project-path> [output-file] [--rules=all|static|xml|compose|runtime] [runtime-snapshot-json|--runtime-adb] [adb-serial]"
```

If `output-file` is omitted, the report is written to `analysis-report.json` in the project working directory. The report format is selected by extension: `.md` / `.markdown` produces Markdown, everything else produces JSON.

### Static Analysis

Analyze XML and Compose static sources:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject analysis-report.json --rules=static"
```

Analyze only XML:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject xml-report.json --rules=xml"
```

Analyze only Compose:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject compose-report.md --rules=compose"
```

### Runtime Analysis

Capture the current device/emulator screen through ADB:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject runtime-report.json --rules=runtime --runtime-adb"
```

Capture from a specific device:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject runtime-report.json --rules=runtime --runtime-adb emulator-5554"
```

Analyze a saved runtime snapshot:

```powershell
.\gradlew.bat :cli:run --args="C:\path\to\AndroidProject runtime-report.md --rules=runtime C:\path\to\runtime-snapshot.json"
```

## Desktop App

Run the Compose Desktop application:

```powershell
.\gradlew.bat :desktop:desktop-impl:run
```

The desktop app lets you:

- Choose Static or Runtime mode.
- Choose XML, Compose, or both for static analysis.
- Select individual rules.
- Run analysis.
- Inspect issues with filters by source, severity, and rule.
- Write reports to JSON or Markdown depending on the selected output file extension.

## Common Development Commands

Compile the main modules:

```powershell
.\gradlew.bat :core:core-api:compileKotlin :report:report-impl:compileKotlin :cli:compileKotlin :desktop:desktop-impl:compileKotlin
```

Run CLI tests:

```powershell
.\gradlew.bat :cli:test
```

Build everything:

```powershell
.\gradlew.bat build
```

## Notes

- Runtime analysis checks the package name captured in the snapshot against the Android project package. If they do not match, only diagnostic runtime rules run.
- For ADB runtime analysis, open the target app on the emulator/device before running the command.
- All text written in English so you have to KNOW IT TO READ THIS README FILE XDDDDD KKKKK XPPPP
- Screencast: https://drive.google.com/file/d/1MXgNvREv8AvN2z1FcGNMFBF2Aes779j0/view?usp=sharing
