package io.github.samuolis.pill

import androidx.compose.ui.geometry.Offset
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
    fun mapsScrubPositionsInBothDirectionsAndRejectsOutside() {
        assertEquals(0, scrubIndex(0f, 300f, 3, false))
        assertEquals(2, scrubIndex(299f, 300f, 3, false))
        assertEquals(2, scrubIndex(0f, 300f, 3, true))
        assertEquals(0, scrubIndex(300f, 300f, 3, true))
        assertNull(scrubIndex(-1f, 300f, 3, false))
        assertNull(scrubIndex(301f, 300f, 3, false))
        assertNull(scrubIndex(Float.NaN, 300f, 3, false))
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
