# Compose Pill

Compose Multiplatform floating navigation and frosted toolbar primitives. The library owns the selected pill, optional drag preview, backdrop and scroll minimization. Navigation remains application-owned.

This is a pre-release implementation. The package and coordinates may change before its first publication.

```kotlin
val backdrop = rememberPillBackdrop()
val behavior = rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)
// Mark the scrollable content with Modifier.pillBackdropSource(backdrop).
// Attach Modifier.nestedScroll(behavior.nestedScrollConnection) to its scrolling parent.
PillNavigationBar(
    items = items,
    selectedKey = routeKey,
    onSelect = { key -> navigate(key) },
    backdrop = backdrop,
    expanded = behavior.expanded,
    onExpandRequest = behavior::expand,
    scrubEnabled = true,
)
```

`PillBarLayout` offers an optional backdrop/placement host and supplies measured bottom content padding. When placing the bar yourself, the caller owns safe areas, the keyboard and scroll clearance. Supply stable nonempty keys and accessible names; the app owns the current selection and navigation back stack. Badge/icon slots should not contain separately interactive controls. The adjacent action is separate from navigation.

A missing backdrop or `PillMaterial.Solid` uses an opaque surface. `PillBarDefaults.glass()` opts into refraction; `PillProgressiveBackdrop` draws a caller-sized progressive blur. Frost and glass use Haze; they are custom effects, not Apple's system Liquid Glass. Non-rounded glass shapes fall back to frost. Set `motionEnabled = false` and use a solid style when your application requests reduced motion or transparency. Older devices and renderers can use a simplified fallback. Do not include sensitive content in a backdrop source if it should not be sampled by floating chrome.

Run the desktop sample with `./gradlew :sample:run`. Run JVM tests with `./gradlew :pill:jvmTest`. Android/iOS/Wasm builds require their respective SDKs. No publication has been made yet.

Licensed under Apache 2.0; see [LICENSE](LICENSE).

For a narrow bar with too many destinations, items stay at least 48 dp wide in a horizontally scrollable row; scrubbing is disabled in that overflow state. `PillNavigationBar` keeps labels visible at larger font scales.
