# Compose Pill

**A floating navigation bar for the places your app goes.**

Compose Pill brings animated selection, expressive surfaces and flexible navigation to Compose Multiplatform. Use your own destinations, icons and colors while your app stays in control of navigation.

[Documentation & live Wasm demo](https://samuolis.github.io/compose-pill/) · [Usage guide](docs/usage.md) · [Releases](https://github.com/samuolis/compose-pill/releases) · [Contributing](CONTRIBUTING.md)

Android · iOS · Desktop JVM · Compose Wasm

## What you get

- **Navigation that moves:** animated selection, optional drag-to-select, scroll minimization, badges and a separate adjacent action.
- **Surfaces that fit:** solid, frosted and custom glass effects with configurable colors, shape and motion.
- **Your state, your design:** supply stable destination keys, Compose icon content and the selected key. The app owns navigation and its back stack.
- **Space for content:** the optional `PillBarLayout` host provides safe-area placement and measured bottom padding for scrolling content.

## Get started

Resolve dependencies from Maven Central and add Compose Pill to your shared source set:

```kotlin
commonMain.dependencies {
    implementation("io.github.samuolis:pill:0.1.0")
}
```

Provide destinations and react to selections using your existing navigation code:

```kotlin
val destinations = listOf(
    PillNavigationItem("home", "Home", icon = { selected -> HomeIcon(selected) }),
    PillNavigationItem("saved", "Saved", icon = { selected -> SavedIcon(selected) }),
)

PillNavigationBar(
    items = destinations,
    selectedKey = currentRoute,
    onSelect = { route -> navigate(route) },
    scrubEnabled = true,
)
```

`HomeIcon`, `SavedIcon`, `currentRoute` and `navigate` are supplied by your application. For backdrop sampling, placement, scroll behavior and complete API guidance, see the [usage guide](docs/usage.md).

## Try the sample

Try the [interactive Wasm demo](https://samuolis.github.io/compose-pill/demo/) in a supported browser. It downloads the Compose WebAssembly runtime on first load. The same sample also runs on desktop and iOS:

```shell
./gradlew :sample:run                                  # desktop
./gradlew :sample:wasmJsBrowserDevelopmentWebpack       # web bundle
./gradlew :sample:linkDebugFrameworkIosSimulatorArm64   # iOS simulator framework
```

For iOS, open `iosApp/PillSample.xcodeproj` after building the framework. Android and iOS builds require their platform SDKs.

## Platform and accessibility notes

Frost and glass are custom effects built with [Haze](https://github.com/chrisbanes/haze); they are not Apple's native Liquid Glass API. Effects can fall back on unsupported renderers. Apps should apply reduced-motion and reduced-transparency preferences, provide localized accessible destination names, and verify contrast, safe areas, keyboard behavior and performance on their target devices. See [usage](docs/usage.md) for details.

## Development

Run `./gradlew :pill:jvmTest` for JVM tests. See [contributing](CONTRIBUTING.md) and [release setup](docs/releasing.md). Compose Pill is licensed under [Apache 2.0](LICENSE).
