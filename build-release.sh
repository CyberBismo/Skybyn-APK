#!/bin/bash

# Clean and build release APK
./gradlew clean
./gradlew assembleRelease

# Output location will be:
# app/build/outputs/apk/release/app-release.apk 