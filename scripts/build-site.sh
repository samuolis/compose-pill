#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
./gradlew :sample:wasmJsBrowserProductionWebpack --no-daemon
output=build/site
bundle=sample/build/kotlin-webpack/wasmJs/productionExecutable
rm -rf "$output"
mkdir -p "$output/demo"
cp site/index.html site/style.css site/favicon.svg "$output/"
cp sample/src/wasmJsMain/resources/index.html "$output/demo/"
cp "$bundle"/sample.js "$bundle"/*.wasm "$output/demo/"
test -s "$output/demo/sample.js"
test -s "$output/demo/index.html"
