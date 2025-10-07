#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WRAPPER_DIR="$ROOT_DIR/clickgui-mod/gradle/wrapper"
JAR_PATH="$WRAPPER_DIR/gradle-wrapper.jar"
GRADLE_VERSION="7.6.1"
WRAPPER_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

if command -v unzip >/dev/null 2>&1; then
  UNZIP_CMD="unzip"
else
  echo "\n[ERROR] unzip utility is required to extract Gradle wrapper jar." >&2
  echo "Install unzip or manually extract gradle-wrapper.jar from ${WRAPPER_URL}." >&2
  exit 1
fi

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

ZIP_FILE="$TMP_DIR/gradle-${GRADLE_VERSION}.zip"

echo "Downloading Gradle ${GRADLE_VERSION} distribution..."
curl -fsSL "$WRAPPER_URL" -o "$ZIP_FILE"

echo "Extracting gradle-wrapper.jar..."
$UNZIP_CMD -q "$ZIP_FILE" "gradle-${GRADLE_VERSION}/lib/gradle-wrapper.jar" -d "$TMP_DIR"
mkdir -p "$WRAPPER_DIR"
cp "$TMP_DIR/gradle-${GRADLE_VERSION}/lib/gradle-wrapper.jar" "$JAR_PATH"

echo "gradle-wrapper.jar saved to $JAR_PATH"
