package io.github.samuolis.pill.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.samuolis.pill.PillBarDefaults
import io.github.samuolis.pill.PillBarLayout
import io.github.samuolis.pill.PillMinimizeBehavior
import io.github.samuolis.pill.PillNavigationBar
import io.github.samuolis.pill.PillNavigationItem
import io.github.samuolis.pill.rememberPillBarScrollBehavior

private val Ink = Color(0xFF202036)
private val Muted = Color(0xFF6B6B80)
private val Accent = Color(0xFF6558D3)

// Navigation icons use Lucide's ISC-licensed paths; see third-party/LUCIDE-LICENSE.txt.
private fun outlineIcon(name: String, vararg paths: String): ImageVector {
    val builder = ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f)
    paths.forEach { path ->
        builder.addPath(
            pathData = PathParser().parsePathString(path).toNodes(),
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
    return builder.build()
}

private val HomeIcon = outlineIcon("Home",
    "M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",
    "M3 10a2 2 0 0 1 .709-1.528l7-6a2 2 0 0 1 2.582 0l7 6A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z")
private val SavedIcon = outlineIcon("Saved",
    "M17 3a2 2 0 0 1 2 2v15a1 1 0 0 1-1.496.868l-4.512-2.578a2 2 0 0 0-1.984 0l-4.512 2.578A1 1 0 0 1 5 20V5a2 2 0 0 1 2-2z")
private val FolderIcon = outlineIcon("Folder",
    "M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z")
private val ProfileIcon = outlineIcon("Profile",
    "M12 13a5 5 0 1 0 0-10a5 5 0 0 0 0 10z", "M20 21a8 8 0 0 0-16 0")

@Composable
internal expect fun sampleFont(): FontFamily

@Composable
fun PillDemo() {
    val uiFont = sampleFont()
    var selected by remember { mutableStateOf("discover") }
    var material by remember { mutableStateOf("Frosted") }
    val behavior = rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)
    val destinations = listOf("discover" to HomeIcon, "saved" to SavedIcon, "profile" to ProfileIcon).map { (key, icon) ->
        PillNavigationItem(key, key.replaceFirstChar { it.uppercase() }, icon = { active ->
            Image(icon, contentDescription = null, modifier = Modifier.size(21.dp),
                colorFilter = ColorFilter.tint(if (active) Accent else Muted))
        })
    }
    val style = when (material) {
        "Solid" -> PillBarDefaults.solid()
        "Glass" -> PillBarDefaults.glass()
        else -> PillBarDefaults.frosted()
    }
    PillBarLayout(
        bar = { backdrop ->
            PillNavigationBar(destinations, selected, { selected = it }, backdrop = backdrop,
                expanded = behavior.expanded, onExpandRequest = behavior::expand, scrubEnabled = true, style = style)
        },
    ) { contentPadding ->
        LazyColumn(
            Modifier.fillMaxSize().background(Color(0xFFF3F2F8)).nestedScroll(behavior.nestedScrollConnection),
            contentPadding = contentPadding,
        ) {
            item {
                Column(Modifier.fillMaxWidth().background(Color(0xFFE9E6FA)).padding(horizontal = 24.dp, vertical = 30.dp)) {
                    BasicText("COMPOSE PILL  /  LIVE DEMO", style = TextStyle(fontFamily = uiFont, color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                    BasicText(selected.replaceFirstChar { it.uppercase() }, Modifier.padding(top = 18.dp), style = TextStyle(fontFamily = uiFont, color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Bold))
                    BasicText(when (selected) {
                        "saved" -> "Your saved collection, at a glance."
                        "profile" -> "A little space that is all yours."
                        else -> "A little more room to explore."
                    }, Modifier.padding(top = 6.dp), style = TextStyle(fontFamily = uiFont, color = Muted, fontSize = 14.sp))
                }
            }
            item {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    BasicText("Choose a surface", style = TextStyle(fontFamily = uiFont, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                    Row(Modifier.padding(top = 12.dp).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Frosted", "Glass", "Solid").forEach { option ->
                            Box(Modifier.defaultMinSize(minHeight = 48.dp)
                                .background(if (material == option) Ink else Color.White, RoundedCornerShape(50))
                                .selectable(selected = material == option, role = Role.RadioButton) { material = option }
                                .padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
                                BasicText(option, style = TextStyle(fontFamily = uiFont, color = if (material == option) Color.White else Ink, fontSize = 12.sp))
                            }
                        }
                    }
                    BasicText("Tap a destination or drag across the bar. Scroll to see it compact.", Modifier.padding(top = 18.dp), style = TextStyle(fontFamily = uiFont, color = Muted, fontSize = 13.sp, lineHeight = 19.sp))
                }
            }
            items(listOf("Design systems" to "Updated today · 12 items", "Mobile patterns" to "Updated yesterday · 8 items", "Product ideas" to "Updated this week · 6 items", "Inspiration" to "Your favorite places to start")) { (title, description) ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp)
                    .background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).background(Color(0xFFEEECFC), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Image(FolderIcon, contentDescription = null, modifier = Modifier.size(20.dp), colorFilter = ColorFilter.tint(Accent))
                    }
                    Column {
                        BasicText(title, style = TextStyle(fontFamily = uiFont, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold))
                        BasicText(description, Modifier.padding(top = 5.dp), style = TextStyle(fontFamily = uiFont, color = Muted, fontSize = 12.sp))
                    }
                }
            }
        }
    }
}
