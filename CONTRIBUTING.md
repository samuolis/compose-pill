# Contributing

Contributions are welcome. Open an issue for significant API or renderer changes and keep the library independent of application navigation and design systems.

Use a branch and pull request. Run `./gradlew :pill:jvmTest :pill:compileAndroidMain :pill:iosSimulatorArm64Test :pill:compileKotlinWasmJs :sample:compileKotlinJvm :sample:wasmJsBrowserDevelopmentWebpack` on a macOS machine with the Android and iOS SDKs. Run `./gradlew :pill:publishAllPublicationsToLocalFixtureRepository` and `./gradlew -p consumer-fixture compileKotlinJvm` to exercise published artifacts.

For visual changes, inspect solid, frosted and glass appearances in light/dark themes, compact/expanded states, large text and narrow widths. Include device and platform limitations in the PR. Screenshot or compilation checks alone are not an accessibility review.

Contributions are licensed under Apache 2.0. Do not commit credentials, signing keys or local SDK configuration.
