# Compose Pill

**Floating navigation and glass surfaces for Compose Multiplatform.**

Use a pill-shaped tab bar with animated selection, optional drag-to-select, and scroll-to-minimize behavior. Your app owns the selected destination and navigation.

[Documentation](https://samuolis.github.io/compose-pill/) · [Live demo](https://samuolis.github.io/compose-pill/demo/) · [Releases](https://github.com/samuolis/compose-pill/releases) · [Klibs.io](https://klibs.io/project/samuolis/compose-pill)

**Platforms** · Android, iOS, JVM desktop, and Compose Wasm.

## Quick start

Add `mavenCentral()` and `google()` to your Gradle repositories. In your Compose Multiplatform module:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.samuolis:pill:0.2.0")
        }
    }
}
```

Create destinations with stable keys and pass the current key to the bar:

```kotlin
PillNavigationBar(
    items = listOf(
        PillNavigationItem("home", "Home", icon = { selected -> HomeIcon(selected) }),
        PillNavigationItem("saved", "Saved", icon = { selected -> SavedIcon(selected) }),
    ),
    selectedKey = currentRoute,
    onSelect = { route -> navigate(route) },
)
```

Supply your own icon composables, route state, and navigation callback. The bar uses a solid surface until you provide a backdrop.

## Documentation

- [Usage guide](docs/usage.md) for placement, solid and frosted surfaces, scroll behavior, accessibility, and supported targets.
- [Sample app](sample/src/commonMain/kotlin/io/github/samuolis/pill/sample/PillDemo.kt) for a complete integration with a scrollable screen.
- [Contributing](CONTRIBUTING.md) and [release setup](docs/releasing.md) for developing the library.

Licensed under [Apache 2.0](LICENSE). The sample and site use [Inter](third-party/INTER-LICENSE.txt) and [Lucide icons](third-party/LUCIDE-LICENSE.txt).
