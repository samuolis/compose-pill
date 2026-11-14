package io.github.samuolis.pill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
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
                Box(Modifier.fillMaxSize().pillBackdropSource(backdrop).background(Color(0xFF789AC3)))
                PillNavigationBar(
                    items = listOf("Home", "Messages", "Profile").map { label ->
                        PillNavigationItem(label, label, { BasicText("●") })
                    },
                    selectedKey = "Home", onSelect = {}, backdrop = backdrop,
                    style = PillBarDefaults.glass(), modifier = Modifier.offset(y = 16.dp),
                )
            }
        }
        val image = compose.onRoot().captureToImage().toAwtImage()
        val file = File("build/reports/pill-glass.png")
        file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }
}
