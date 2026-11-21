# Using Compose Pill

Compose Pill exposes standard Compose UI types. Add `implementation("io.github.samuolis:pill:0.2.0")` to a Compose Multiplatform source set and resolve it from Maven Central. Try the [interactive Wasm demo](https://samuolis.github.io/compose-pill/demo/) or visit the [documentation website](https://samuolis.github.io/compose-pill/).

## Destinations and state

Create a `PillNavigationItem` per destination with a stable, nonempty key, localized label and icon composable. Keys identify destinations across reorder and locale changes; labels do not. `selectedKey` and navigation are controlled by the application. `onSelect` is invoked once for a click or a committed scrub, including a reselected item. A canceled scrub does not navigate. Icons receive committed selection state, not transient scrub preview.

An icon or badge is visual content, not a nested interactive control. Provide a complete localized `contentDescription` if a badge conveys meaningful information. Use `adjacentAction` for an action such as Create rather than treating it as a destination.

## Backdrop and placement

`PillBarLayout` places the bar above safe-drawing insets and returns measured bottom `PaddingValues` for a scrolling body. The sample passes those values as `LazyColumn` content padding. It installs a shared backdrop source around content. If using your own layout, create `rememberPillBackdrop()`, apply `Modifier.pillBackdropSource(backdrop)` to the content behind the bar, and pass the same backdrop to `PillNavigationBar` or `PillSurface`. Avoid placing the bar inside the source it samples.

The supplied host moves the bar with safe-drawing insets; consumers needing a different keyboard policy should own placement. Verify integration with already-consumed insets and other bottom overlays. When no backdrop is provided, surfaces use an opaque fallback.

## Appearance and motion

`PillBarDefaults.solid()`, `frosted()` and `glass()` return a `PillBarStyle`; copy it to use your Compose colors, shape and dimensions. Frost and Glass are custom effects rendered by Haze. They are not Apple system Liquid Glass. Unsupported effects may fall back; Glass with a non-rounded shape uses Frosted. `PillProgressiveBackdrop` draws within the supplied modifier bounds, with start/end intensities from zero to one.

Set `motionEnabled = false` when reduced motion is requested. Use a solid style for reduced transparency or when contrast over content is inadequate. The library does not currently read every operating-system accessibility preference automatically. At font scale above 1.3, the bar keeps labels expanded. If destinations cannot fit at 48 dp each, the row scrolls horizontally and disables scrub so horizontal panning remains usable.

## Scrolling

`rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)` supplies a nested-scroll observer and expanded state. Attach its `nestedScrollConnection` to the scroll owner and pass `behavior.expanded` and `behavior::expand` to the bar. The default is `Never`. The observer does not consume the page's scroll. Users can still activate individual destinations when compact.

Apps that own the bar's state and animation can attach a `PillScrollThresholdConnection` to their scroll owner. It reports direction after the configured pixel threshold and resets the accumulated distance when direction reverses. By default it observes direct input and includes equality at the threshold. Set `includeSideEffects = true` to observe flings and other non-input scroll, or `inclusiveThreshold = false` to require movement past the threshold.

## Platform status

The library targets Android, iOS, JVM desktop and Compose Wasm. Compile and automated shared tests cover these targets; behavior and optical fidelity still depend on device, OS and graphics backend. The sample includes JVM desktop, iOS and Compose Wasm entry points. Platform accessibility, glass performance and keyboard/safe-area handling must be verified for the consuming app.
