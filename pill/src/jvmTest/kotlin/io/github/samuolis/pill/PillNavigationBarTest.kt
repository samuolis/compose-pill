package io.github.samuolis.pill

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.down
import androidx.compose.ui.test.cancel
import androidx.compose.ui.test.moveTo
import androidx.compose.ui.test.up
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class PillNavigationBarTest {
    @get:Rule val compose = createComposeRule()

    @Test fun selectionAndDisabledItemsExposeActions() {
        var selected by mutableStateOf("home")
        var count = 0
        compose.setContent {
            PillNavigationBar(
                items = listOf(
                    PillNavigationItem("home", "Home", icon = { BasicText("H") }),
                    PillNavigationItem("search", "Search", icon = { BasicText("S") }),
                    PillNavigationItem("disabled", "Disabled", icon = { BasicText("D") }, enabled = false),
                ),
                selectedKey = selected,
                onSelect = { selected = it; count++ },
                style = PillBarDefaults.solid(),
            )
        }
        compose.onNodeWithContentDescription("Home").assertIsSelected()
        compose.onNodeWithContentDescription("Disabled").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Search").performClick().assertIsSelected()
        compose.runOnIdle { assertEquals(1, count) }
    }

    @Test fun horizontalDragCommitsOnRelease() {
        var selected by mutableStateOf("home")
        var count = 0
        compose.setContent {
            PillNavigationBar(
                items = listOf(
                    PillNavigationItem("home", "Home", icon = { BasicText("H") }),
                    PillNavigationItem("search", "Search", icon = { BasicText("S") }),
                ),
                selectedKey = selected,
                onSelect = { selected = it; count++ },
                scrubEnabled = true,
                style = PillBarDefaults.solid(),
            )
        }
        compose.onNodeWithContentDescription("Home").performTouchInput {
            down(center)
            moveTo(center.copy(x = center.x + width * 0.8f))
            moveTo(center.copy(x = center.x + width * 1.3f))
            up()
        }
        compose.runOnIdle { assertEquals("search", selected); assertEquals(1, count) }
    }

    @Test fun canceledDragDoesNotNavigate() {
        var selected by mutableStateOf("home")
        var count = 0
        compose.setContent {
            PillNavigationBar(
                items = listOf(
                    PillNavigationItem("home", "Home", icon = { BasicText("H") }),
                    PillNavigationItem("search", "Search", icon = { BasicText("S") }),
                ),
                selectedKey = selected,
                onSelect = { selected = it; count++ },
                scrubEnabled = true,
                style = PillBarDefaults.solid(),
            )
        }
        compose.onNodeWithContentDescription("Home").performTouchInput {
            down(center)
            moveTo(center.copy(x = center.x + width * 0.8f))
            moveTo(center.copy(x = center.x + width * 1.3f))
            cancel()
        }
        compose.runOnIdle { assertEquals("home", selected); assertEquals(0, count) }
    }

    @Test fun largeTextKeepsCompactNavigationLabelsVisible() {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 2f)) {
                PillNavigationBar(
                    items = listOf(PillNavigationItem("home", "Home", icon = { BasicText("H") })),
                    selectedKey = "home", onSelect = {}, expanded = false, motionEnabled = false,
                    style = PillBarDefaults.solid(),
                )
            }
        }
        compose.onNodeWithText("Home", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test fun narrowLayoutKeepsMinimumDestinationWidth() {
        compose.setContent {
            Box(Modifier.width(160.dp)) {
                PillNavigationBar(
                    items = (1..5).map { PillNavigationItem("item$it", "Item $it", icon = { BasicText("I") }) },
                    selectedKey = "item1", onSelect = {}, motionEnabled = false,
                    style = PillBarDefaults.solid(),
                )
            }
        }
        val width = compose.onNodeWithContentDescription("Item 1").fetchSemanticsNode().size.width
        kotlin.test.assertTrue(width >= with(compose.density) { 48.dp.roundToPx() })
    }

    @Test fun longScaledLabelFitsInsideItsDestination() {
        val label = "Long destination label for accessibility"
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 2f)) {
                Box(Modifier.width(180.dp)) {
                    PillNavigationBar(
                        items = listOf(PillNavigationItem("long", label, icon = { BasicText("I") })),
                        selectedKey = "long", onSelect = {}, motionEnabled = false,
                        style = PillBarDefaults.solid(),
                    )
                }
            }
        }
        val item = compose.onNodeWithContentDescription(label).getUnclippedBoundsInRoot()
        val text = compose.onNodeWithText(label, useUnmergedTree = true).getUnclippedBoundsInRoot()
        kotlin.test.assertTrue(text.bottom <= item.bottom, "The label must fit inside its target")
    }
}
