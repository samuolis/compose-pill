package io.github.samuolis.pill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** Places a bar above the safe area and provides its measured height as scroll-content clearance. */
@Composable
fun PillBarLayout(
    modifier: Modifier = Modifier,
    backdrop: PillBackdrop = rememberPillBackdrop(),
    bar: @Composable (PillBackdrop) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    PillBarLayout(false, modifier, backdrop, bar, content)
}

/** Overlays a fading blur behind the bar without changing content clearance. */
@Composable
fun PillBarLayout(
    progressiveEdgeBlur: Boolean,
    modifier: Modifier = Modifier,
    backdrop: PillBackdrop = rememberPillBackdrop(),
    bar: @Composable (PillBackdrop) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var barHeight by remember { mutableIntStateOf(0) }
    val bottomPadding = with(LocalDensity.current) { barHeight.toDp() }
    Box(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().pillBackdropSource(backdrop)) {
            content(PaddingValues(bottom = bottomPadding))
        }
        if (progressiveEdgeBlur && bottomPadding > 0.dp) {
            PillProgressiveBackdrop(
                backdrop = backdrop,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(bottomPadding),
            )
        }
        Box(
            Modifier.align(Alignment.BottomCenter).onSizeChanged { barHeight = it.height }
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) { bar(backdrop) }
    }
}
