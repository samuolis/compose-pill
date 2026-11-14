package io.github.samuolis.pill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.imageio.ImageIO

class PillVisualTest {
    @get:Rule val compose = createComposeRule()

    @Test fun renderSolidNavigation() {
        compose.setContent {
            Box(Modifier.size(360.dp, 100.dp).background(Color(0xFFCCD5E7))) {
                PillNavigationBar(
                    items = listOf("Home", "Messages", "Profile").map { label ->
                        PillNavigationItem(label, label, { BasicText("●") })
                    },
                    selectedKey = "Home", onSelect = {}, style = PillBarDefaults.solid(),
                )
            }
        }
        val image = compose.onRoot().captureToImage().toAwtImage()
        val file = File("build/reports/pill-navigation.png")
        file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }

    @Test fun renderGlassOverBackdrop() {
        compose.setContent {
            val backdrop = rememberPillBackdrop()
            Box(Modifier.size(360.dp, 100.dp)) {
                Box(Modifier.fillMaxSize().pillBackdropSource(backdrop).background(
                    Brush.horizontalGradient(listOf(Color(0xFF304F9F), Color(0xFFFFAC52), Color(0xFF46A78C))),
                ))
                PillNavigationBar(
                    items = listOf("Home", "Messages", "Profile").map { label ->
                        PillNavigationItem(label, label, { BasicText("●") })
                    },
                    selectedKey = "Home", onSelect = {}, backdrop = backdrop,
                    style = PillBarDefaults.glass(), modifier = Modifier.offset(y = 16.dp),
                )
            }
        }
        compose.mainClock.advanceTimeBy(500)
        val image = compose.onRoot().captureToImage().toAwtImage()
        var darkPixels = 0
        for (y in 0 until image.height) for (x in 0 until image.width / 3) {
            val pixel = image.getRGB(x, y)
            if (((pixel shr 16) and 0xff) < 50 && ((pixel shr 8) and 0xff) < 50 && (pixel and 0xff) < 50) darkPixels++
        }
        kotlin.test.assertTrue(darkPixels > 5, "Selected destination must remain visible over glass")
        val file = File("build/reports/pill-glass.png")
        file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }

    @Test fun selectingAnotherDestinationMovesTheIndicator() {
        compose.setContent {
            var selected by remember { mutableStateOf("Home") }
            Box(Modifier.size(360.dp, 100.dp).background(Color(0xFFCCD5E7))) {
                PillNavigationBar(
                    items = listOf("Home", "Messages", "Profile").map { label ->
                        PillNavigationItem(label, label, { BasicText("●") })
                    },
                    selectedKey = selected, onSelect = { selected = it },
                    motionEnabled = false, style = PillBarDefaults.solid(),
                )
            }
        }
        val before = compose.onRoot().captureToImage().toAwtImage()
        compose.onNodeWithContentDescription("Messages").performClick()
        val after = compose.onRoot().captureToImage().toAwtImage()
        var changed = 0
        for (y in 0 until before.height) for (x in 0 until before.width) {
            if (before.getRGB(x, y) != after.getRGB(x, y)) changed++
        }
        kotlin.test.assertTrue(changed > 100, "Selection should visibly move the indicator")
    }

    @Test fun renderCompactNavigation() {
        compose.setContent {
            Box(Modifier.size(320.dp, 100.dp).background(Color(0xFFCCD5E7))) {
                PillNavigationBar(
                    items = listOf("Home", "Messages", "Search", "Profile").map { label ->
                        PillNavigationItem(label, label, { BasicText("●") })
                    },
                    selectedKey = "Messages", onSelect = {}, expanded = false,
                    motionEnabled = false, style = PillBarDefaults.solid(),
                )
            }
        }
        val image = compose.onRoot().captureToImage().toAwtImage()
        val file = File("build/reports/pill-compact.png")
        file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }
}
