#!/bin/bash
GRADLE_VER="4.10.2"
DIST_URL="https://services.gradle.org/distributions"
if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
  pushd gradle/wrapper
  wget --no-check-certificate $DIST_URL/gradle-$GRADLE_VER-all.zip
  unzip -qq gradle-$GRADLE_VER-all.zip
  cp -u gradle-$GRADLE_VER/samples/userguide/wrapper/sha256-verification/gradle/wrapper/gradle-wrapper.jar .
  rm -rf gradle-$GRADLE_VER gradle-$GRADLE_VER-all.zip
  popd
fi
./gradlew $@ clean build
