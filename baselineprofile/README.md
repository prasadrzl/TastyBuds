# Baseline Profiles & Startup Benchmark

This module improves app startup and reduces first-run jank by generating a
[Baseline Profile](https://developer.android.com/topic/performance/baselineprofiles/overview):
a list of hot classes/methods that Android AOT-compiles at install time.

## What's here
- `BaselineProfileGenerator` — records the critical journey (cold start → home settles).
- `StartupBenchmark` — measures cold-start time with vs. without the profile, so the win is quantifiable.

## Generate the profile
Runs on a **Gradle Managed Device** (an emulator Gradle spins up automatically — no device
needs to be plugged in; also works in CI):

```bash
./gradlew :app:generateReleaseBaselineProfile
```

The generated profile is written to `app/src/release/generated/baselineProfiles/` and is
packaged into the release APK/AAB automatically (applied at runtime via `profileinstaller`).

To generate against a physically connected device/emulator instead, set
`useConnectedDevices = true` in this module's `build.gradle.kts`.

## Measure the improvement
```bash
./gradlew :baselineprofile:pixel6Api34BenchmarkAndroidTest
```
Compares `startupNoCompilation` vs `startupBaselineProfile` (cold start, 5 iterations each).

## Notes
- Requires API 28+ for generation (macrobenchmark constraint).
- First managed-device run downloads the emulator system image.
