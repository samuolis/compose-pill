package io.github.samuolis.pill

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import androidx.compose.ui.unit.dp

class PillContractsTest {
    private fun item(key: String) = PillNavigationItem(key, key, icon = {})

    @Test
    fun validatesStableKeysAndSelection() {
        assertFailsWith<IllegalArgumentException> { item("") }
        assertFailsWith<IllegalArgumentException> { validPillSelection(listOf(item("a"), item("a")), "a") }
        assertFailsWith<IllegalArgumentException> { validPillSelection(listOf(item("a")), "missing") }
        validPillSelection(listOf(item("a")), null)
    }

    @Test
    fun rejectsInvalidEffectDimensions() {
        assertFailsWith<IllegalArgumentException> { PillBarStyle(blurRadius = (-1).dp) }
        assertFailsWith<IllegalArgumentException> { PillBarStyle(elevation = Float.NaN.dp) }
    }

    @Test
    fun scrubTracksMeasuredTargetsAndIgnoresInvalidCoordinates() {
        val left = PillScrubTarget("left", PillIndicatorBounds(IntOffset.Zero, IntSize(80, 48)))
        val right = PillScrubTarget("right", PillIndicatorBounds(IntOffset(80, 0), IntSize(120, 48)))
        val targets = listOf(right, left)
        val scrub = PillScrubState()
        scrub.begin(left.bounds, targets)

        assertNull(scrub.update(Float.NaN, targets))
        assertEquals("right", scrub.update(100f, targets))
        assertEquals(IntSize(104, 48), scrub.indicatorBounds?.size)
        assertNull(scrub.update(110f, targets))
        assertEquals("right", scrub.finish())
        assertNull(scrub.finish())
    }
}

class PillScrollTest {
    @Test
    fun customScrollObserverUsesStrictThresholdAndIncludesSideEffects() {
        val directions = mutableListOf<Boolean>()
        val connection = PillScrollThresholdConnection(
            thresholdPx = 32f,
            includeSideEffects = true,
            inclusiveThreshold = false,
        ) { directions.add(it) }
        fun scroll(y: Float) {
            assertEquals(
                Offset.Zero,
                connection.onPostScroll(Offset(0f, y), Offset.Zero, NestedScrollSource.SideEffect),
            )
        }

        scroll(-32f)
        assertEquals(emptyList(), directions)
        scroll(-1f)
        scroll(28f)
        scroll(-20f)
        scroll(-13f)
        scroll(33f)
        assertEquals(listOf(true, true, false), directions)
    }

    @Test
    fun minimizesOnlyOnConsumedUserScrollAndExpandsOnReverse() {
        val state = PillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown, 20f)
        val connection = state.nestedScrollConnection
        val zero = androidx.compose.ui.geometry.Offset.Zero
        val user = androidx.compose.ui.input.nestedscroll.NestedScrollSource.UserInput
        connection.onPostScroll(Offset(0f, -100f), zero, NestedScrollSource.SideEffect)
        kotlin.test.assertTrue(state.expanded)
        connection.onPostScroll(Offset(0f, -12f), zero, user)
        kotlin.test.assertTrue(state.expanded)
        connection.onPostScroll(Offset(0f, -8f), zero, user)
        kotlin.test.assertFalse(state.expanded)
        connection.onPostScroll(androidx.compose.ui.geometry.Offset(0f, 25f), zero, user)
        kotlin.test.assertTrue(state.expanded)
        connection.onPostScroll(zero, androidx.compose.ui.geometry.Offset(0f, -100f), user)
        kotlin.test.assertTrue(state.expanded)
    }
}
