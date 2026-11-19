# E SHORT — Android starter

Independent Android application. The existing U-SHORT repository is untouched.

## Included
- Kotlin + Jetpack Compose Android project.
- Purple / blue / pink glass-inspired interface.
- Home, Studio, setup, editor, AI tool overview and settings screens.
- AUTO VIRAL, CUSTOM and PRO EDIT workflow selection.
- Clip duration, aspect ratio and clip count controls.
- GitHub Actions workflow to build and upload a debug APK artifact.

## Current implementation status
This is the first Android UI foundation. Real video ingestion, authorized source download, AI highlight detection, face tracking, speech recognition, subtitle generation, rendering and final export are not yet connected. The app must not present those engines as working until implemented and tested.

## Build
The GitHub Actions workflow builds with JDK 17 and Gradle 8.9 using `gradle assembleDebug`.

Application ID: `com.eshort.app`
