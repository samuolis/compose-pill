# Using Compose Pill

Follow the [README quick start](../README.md#quick-start) to install the library and display a bar. Try the [interactive Wasm demo](https://samuolis.github.io/compose-pill/demo/) or browse the [sample app](../sample/src/commonMain/kotlin/io/github/samuolis/pill/sample/PillDemo.kt).

## Choose an entry point

| API | Use it to |
| --- | --- |
| `PillNavigationBar` | Render a bar with app-owned selection, labels, icons, and optional badges. |
| `PillBarLayout` | Place the bar over scrollable content and reserve bottom clearance. |
| `PillToolbar` or `PillSurface` | Build your own controls with the same surface styles. |
| `PillProgressiveBackdrop` | Blur the edge of a scrollable area behind a bar. |
| `rememberPillBarScrollBehavior` | Toggle the bar between expanded and compact as the user scrolls. |
| `PillScrollThresholdConnection` | Observe scrolling while keeping your own bar state and animation. |

## Destinations and state

Create a `PillNavigationItem` per destination with a stable, nonempty key, localized label and icon composable. Keys identify destinations across reorder and locale changes; labels do not. `selectedKey` and navigation are controlled by the application. `onSelect` is invoked once for a click or a committed scrub, including a reselected item. A canceled scrub does not navigate. Icons receive the committed selection. The sliding indicator shows the drag preview. If destinations can reorder, key any state remembered inside an icon or badge slot by destination.

An icon or badge is visual content, not a nested interactive control. Provide a complete localized `contentDescription` if a badge conveys meaningful information. Use `adjacentAction` for an action such as Create rather than treating it as a destination.

## Backdrop and placement

`PillBarLayout` places the bar above safe-drawing insets and returns measured bottom `PaddingValues` for a scrolling body. The sample passes those values as `LazyColumn` content padding. It installs a shared backdrop source around content. If using your own layout, create `rememberPillBackdrop()`, apply `Modifier.pillBackdropSource(backdrop)` to the content behind the bar, and pass the same backdrop to `PillNavigationBar` or `PillSurface`. Avoid placing the bar inside the source it samples.

The supplied host moves the bar with safe-drawing insets; consumers needing a different keyboard policy should own placement. Verify integration with already-consumed insets and other bottom overlays. When no backdrop is provided, surfaces use an opaque fallback.

## Appearance and motion

`PillBarDefaults.solid()`, `frosted()` and `glass()` return a `PillBarStyle`; copy it to use your Compose colors, shape and dimensions. Frost and Glass are custom effects rendered by Haze. They are not Apple system Liquid Glass. Unsupported effects may fall back; Glass with a non-rounded shape uses Frosted. `PillProgressiveBackdrop` draws within the supplied modifier bounds, with start/end intensities from zero to one.

Set `motionEnabled = false` when reduced motion is requested. Use a solid style for reduced transparency or when contrast over content is inadequate. The library does not currently read every operating-system accessibility preference automatically. At font scale above 1.3, the bar keeps labels expanded. If destinations cannot fit at 48 dp each, the row scrolls horizontally and disables scrub. When your app selects a destination programmatically, the row keeps its current scroll position.

## Scrolling

`rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)` supplies a nested-scroll observer and expanded state. Attach its `nestedScrollConnection` to the scroll owner and pass `behavior.expanded` and `behavior::expand` to the bar. The default is `Never`. The observer does not consume the page's scroll. Users can still activate individual destinations when compact.

Apps that own the bar's state and animation can attach a `PillScrollThresholdConnection` to their scroll owner. It reports direction after the configured pixel threshold and resets the accumulated distance when direction reverses. By default it observes direct input and includes equality at the threshold. Set `includeSideEffects = true` to observe flings and other non-input scroll, or `inclusiveThreshold = false` to require movement past the threshold.

## Supported platforms

| Target | Published variants |
| --- | --- |
| Android | API 24 and later |
| iOS | ARM64 device and ARM64 simulator |
| Desktop | JVM |
| Browser | Compose Wasm |

Android, iOS, JVM, and Wasm build checks run in CI. The JVM suite covers bar interaction and visuals, and the iOS sample has a simulator navigation test. Check contrast, keyboard and safe-area behavior, and performance on the platforms your app ships.

## Run the sample

| Platform | Command |
| --- | --- |
| Desktop | `./gradlew :sample:run` |
| Browser | `./gradlew :sample:wasmJsBrowserDevelopmentWebpack` |
| iOS simulator | `./gradlew :sample:linkDebugFrameworkIosSimulatorArm64`, then open `iosApp/PillSample.xcodeproj` |
