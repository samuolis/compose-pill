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
    private var accumulated = 0f

    fun expand() { expanded = true; accumulated = 0f }

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (behavior == PillMinimizeBehavior.Never || source != NestedScrollSource.UserInput || consumed.y == 0f) return Offset.Zero
            if (sign(accumulated) != sign(consumed.y)) accumulated = 0f
            accumulated += consumed.y
            if (abs(accumulated) >= threshold) {
                expanded = accumulated > 0f
                accumulated = 0f
            }
            return Offset.Zero
        }
    }
}

@Composable
fun rememberPillBarScrollBehavior(
    minimizeBehavior: PillMinimizeBehavior = PillMinimizeBehavior.Never,
    threshold: androidx.compose.ui.unit.Dp = 24.dp,
): PillBarScrollBehavior {
    val pixels = with(androidx.compose.ui.platform.LocalDensity.current) { threshold.toPx() }
    require(pixels > 0f && pixels.isFinite())
    return remember(minimizeBehavior, pixels) { PillBarScrollBehavior(minimizeBehavior, pixels) }
}
