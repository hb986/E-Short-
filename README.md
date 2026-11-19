# E SHORT — Offline Android Video Clip Maker

E SHORT is an independent Android app for splitting a local video into consecutive short MP4 clips. It does not require an account, API key, or network connection for its implemented workflow.

## Working features in this build
- Select a video from the phone using Android's document picker.
- Preview the selected source video in an installed video player.
- Choose a clip length: 15, 30, 45, 60, or 90 seconds.
- Choose a maximum clip count: 1, 3, 5, 10, or 20.
- Export consecutive MP4 clips using AndroidX Media3 Transformer.
- Save exported clips to the public Movies/E SHORT folder.
- Show export progress, completion, and errors; play the last exported clip.

## Important scope
This release is deliberately limited to functions implemented in the app. It does not claim to provide AI highlight detection, automatic speech recognition/captions, face tracking, URL downloading, a multi-track timeline, effects, or professional editing. Those features are not presented as working controls.

The clipper creates consecutive segments from the beginning of the selected video; it does not analyze content or select viral moments. The final segment can be shorter than the chosen duration.

## Build
GitHub Actions builds the debug APK with JDK 17 and Gradle 8.9.

- Application ID: `com.eshort.app`
- Minimum Android: Android 10 (API 29)
- Output folder: `Movies/E SHORT`
