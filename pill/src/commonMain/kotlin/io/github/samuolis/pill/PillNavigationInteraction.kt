package io.github.samuolis.pill

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.roundToInt

data class PillIndicatorBounds(
    val offset: IntOffset,
    val size: IntSize,
)

data class PillScrubTarget(
    val key: String,
    val bounds: PillIndicatorBounds,
)

private val PillIndicatorBounds.centerX: Float
    get() = offset.x + size.width / 2f

private fun lerpInt(start: Int, end: Int, fraction: Float): Int =
    (start + (end - start) * fraction).roundToInt()

private fun nearestScrubTarget(
    centerX: Float,
    targets: List<PillScrubTarget>,
): PillScrubTarget? =
    targets.minByOrNull { target -> abs(target.bounds.centerX - centerX) }

/** Tracks a drag against measured, enabled destinations in physical pixels. */
@Stable
class PillScrubState {
    var isScrubbing by mutableStateOf(false)
        private set
    var previewKey by mutableStateOf<String?>(null)
        private set
    var indicatorBounds by mutableStateOf<PillIndicatorBounds?>(null)
        private set

    fun begin(indicator: PillIndicatorBounds, targets: List<PillScrubTarget>) {
        if (targets.isEmpty()) return
        isScrubbing = true
        previewKey = nearestScrubTarget(indicator.centerX, targets)?.key
    }

    /** Returns the new key only when the drag crosses into a different destination. */
    fun update(pointerX: Float, targets: List<PillScrubTarget>): String? {
        if (!isScrubbing || targets.isEmpty() || !pointerX.isFinite()) return null
        val sortedTargets = targets.sortedBy { it.bounds.centerX }
        val firstCenter = sortedTargets.first().bounds.centerX
        val lastCenter = sortedTargets.last().bounds.centerX
        val centerX = pointerX.coerceIn(firstCenter, lastCenter)
        val endIndex = sortedTargets.indexOfFirst { centerX <= it.bounds.centerX }
        val indicatorBounds =
            if (endIndex <= 0) {
                (if (endIndex == 0) sortedTargets.first() else sortedTargets.last()).bounds
            } else {
                val start = sortedTargets[endIndex - 1]
                val end = sortedTargets[endIndex]
                val distance = (end.bounds.centerX - start.bounds.centerX).coerceAtLeast(1f)
                val fraction = ((centerX - start.bounds.centerX) / distance).coerceIn(0f, 1f)
                val width = lerpInt(start.bounds.size.width, end.bounds.size.width, fraction)
                val height = lerpInt(start.bounds.size.height, end.bounds.size.height, fraction)
                PillIndicatorBounds(
                    offset = IntOffset(
                        x = (centerX - width / 2f).roundToInt(),
                        y = lerpInt(start.bounds.offset.y, end.bounds.offset.y, fraction),
                    ),
                    size = IntSize(width, height),
                )
            }
        this.indicatorBounds = indicatorBounds
        val targetKey = nearestScrubTarget(centerX = centerX, targets = sortedTargets)?.key
        return targetKey?.takeIf { it != previewKey }?.also {
            previewKey = it
        }
    }

    /** Ends preview and returns the key to commit. The last bounds remain available for settling. */
    fun finish(): String? {
        if (!isScrubbing) return null
        val key = previewKey
        cancel()
        return key
    }

    fun cancel() {
        isScrubbing = false
        previewKey = null
    }

    fun clearBounds() {
        indicatorBounds = null
    }
}

/** Fraction of a tab occupied by the moving pill; used to make tint follow geometry, not focus. */
fun pillIndicatorCoverage(
    itemBounds: PillIndicatorBounds,
    indicatorBounds: PillIndicatorBounds,
): Float {
    val itemStart = itemBounds.offset.x.toFloat()
    val itemEnd = itemStart + itemBounds.size.width
    val indicatorStart = indicatorBounds.offset.x.toFloat()
    val indicatorEnd = indicatorStart + indicatorBounds.size.width
    val overlap = (minOf(itemEnd, indicatorEnd) - maxOf(itemStart, indicatorStart)).coerceAtLeast(0f)
    return (overlap / itemBounds.size.width.coerceAtLeast(1)).coerceIn(0f, 1f)
}

/** Adds a drag recognizer and gives one haptic tick per destination crossed. */
@Composable
fun Modifier.pillNavigationScrub(
    key: Any,
    hapticFeedback: HapticFeedbackType?,
    onStart: (Float) -> Unit,
    onMove: (Float) -> Boolean,
    onEnd: () -> Unit,
    onCancel: () -> Unit,
    horizontalOnly: Boolean = true,
): Modifier {
    val haptics by rememberUpdatedState(LocalHapticFeedback.current)
    val feedback by rememberUpdatedState(hapticFeedback)
    val start by rememberUpdatedState(onStart)
    val move by rememberUpdatedState(onMove)
    val end by rememberUpdatedState(onEnd)
    val cancel by rememberUpdatedState(onCancel)
    return pointerInput(key, horizontalOnly) {
        val drag: (PointerInputChange, Float) -> Unit = { change, _ ->
            change.consume()
            if (move(change.position.x)) {
                feedback?.let(haptics::performHapticFeedback)
            }
        }
        if (horizontalOnly) {
            detectHorizontalDragGestures({ start(it.x) }, { end() }, { cancel() }, drag)
        } else {
            detectDragGestures({ start(it.x) }, { end() }, { cancel() }) { change, _ ->
                drag(change, 0f)
            }
        }
    }
}
