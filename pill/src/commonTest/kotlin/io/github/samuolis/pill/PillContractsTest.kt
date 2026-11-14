package io.github.samuolis.pill

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

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
    fun minimizesOnlyOnConsumedUserScrollAndExpandsOnReverse() {
        val state = PillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown, 20f)
        val connection = state.nestedScrollConnection
        val zero = androidx.compose.ui.geometry.Offset.Zero
        val user = androidx.compose.ui.input.nestedscroll.NestedScrollSource.UserInput
        connection.onPostScroll(androidx.compose.ui.geometry.Offset(0f, -12f), zero, user)
        kotlin.test.assertTrue(state.expanded)
        connection.onPostScroll(androidx.compose.ui.geometry.Offset(0f, -10f), zero, user)
        kotlin.test.assertFalse(state.expanded)
        connection.onPostScroll(androidx.compose.ui.geometry.Offset(0f, 25f), zero, user)
        kotlin.test.assertTrue(state.expanded)
        connection.onPostScroll(zero, androidx.compose.ui.geometry.Offset(0f, -100f), user)
        kotlin.test.assertTrue(state.expanded)
    }
}
