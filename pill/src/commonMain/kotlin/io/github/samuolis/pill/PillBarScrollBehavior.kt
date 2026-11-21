package io.github.samuolis.pill

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sign

enum class PillMinimizeBehavior { Never, OnScrollDown }

@Stable
class PillBarScrollBehavior internal constructor(
    private val behavior: PillMinimizeBehavior,
    private val threshold: Float,
) {
    var expanded by mutableStateOf(true)
        private set
    private val scrollObserver = PillScrollThresholdConnection(threshold) { scrollingDown ->
        if (behavior == PillMinimizeBehavior.OnScrollDown) expanded = !scrollingDown
    }

    fun expand() {
        expanded = true
        scrollObserver.reset()
    }

    val nestedScrollConnection: NestedScrollConnection = scrollObserver
}

/**
 * Reports the direction of consumed vertical scroll whenever it crosses the threshold in pixels.
 * A negative Compose Y delta is reported as scrolling down. The observer never consumes scroll.
 */
class PillScrollThresholdConnection(
    private val thresholdPx: Float,
    private val includeSideEffects: Boolean = false,
    private val inclusiveThreshold: Boolean = true,
    private val onThresholdReached: (scrollingDown: Boolean) -> Unit,
) : NestedScrollConnection {
    init {
        require(thresholdPx > 0f && thresholdPx.isFinite())
    }

    private var accumulated = 0f

    fun reset() {
        accumulated = 0f
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        val delta = consumed.y
        if (delta == 0f || (!includeSideEffects && source != NestedScrollSource.UserInput)) {
            return Offset.Zero
        }
        if (sign(accumulated) != sign(delta)) reset()
        accumulated += delta
        val distance = abs(accumulated)
        if (distance > thresholdPx || (inclusiveThreshold && distance == thresholdPx)) {
            val scrollingDown = accumulated < 0f
            reset()
            onThresholdReached(scrollingDown)
        }
        return Offset.Zero
    }
}

@Composable
fun rememberPillBarScrollBehavior(
    minimizeBehavior: PillMinimizeBehavior = PillMinimizeBehavior.Never,
    threshold: androidx.compose.ui.unit.Dp = 24.dp,
): PillBarScrollBehavior {
    val pixels = with(androidx.compose.ui.platform.LocalDensity.current) { threshold.toPx() }
    return remember(minimizeBehavior, pixels) { PillBarScrollBehavior(minimizeBehavior, pixels) }
}
