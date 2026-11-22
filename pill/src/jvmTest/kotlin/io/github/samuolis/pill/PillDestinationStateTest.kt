package io.github.samuolis.pill

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class PillDestinationStateTest {
    @get:Rule val rule = createComposeRule()

    @Test fun narrowBarShowsCurrentDestination() {
        rule.setContent {
            Box(Modifier.width(160.dp)) {
                PillNavigationBar(
                    items = (1..5).map { PillNavigationItem("item$it", "Item $it", icon = { BasicText("I") }) },
                    selectedKey = "item5", onSelect = {}, motionEnabled = false,
                    style = PillBarDefaults.solid(),
                )
            }
        }
        rule.onNodeWithContentDescription("Item 5").assertIsSelected().assertIsDisplayed()
    }

    @Test fun rememberedIconContentFollowsDestinationKeyOnReorder() {
        val order = mutableStateOf(listOf("A", "B"))
        rule.setContent {
            PillNavigationBar(
                items = order.value.map { name ->
                    PillNavigationItem(name, name, icon = {
                        val iconName = remember { "Icon " + name }
                        BasicText(iconName)
                    })
                },
                selectedKey = "A", onSelect = {}, motionEnabled = false,
                style = PillBarDefaults.solid(),
            )
        }
        val iconOfA = hasText("Icon A") and hasAnyAncestor(hasContentDescription("A"))
        rule.onNode(iconOfA, useUnmergedTree = true).assertExists()
        rule.runOnIdle { order.value = listOf("B", "A") }
        rule.onNode(iconOfA, useUnmergedTree = true).assertExists()
    }
}
