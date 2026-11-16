package io.github.samuolis.pill.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
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

@Composable
fun PillDemo() {
    var selected by remember { mutableStateOf("discover") }
    var material by remember { mutableStateOf("Frosted") }
    val behavior = rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)
    val destinations = listOf("discover" to "✦", "saved" to "♡", "profile" to "◉").map { (key, symbol) ->
        PillNavigationItem(key, key.replaceFirstChar { it.uppercase() }, icon = { active ->
            BasicText(symbol, style = TextStyle(color = if (active) Accent else Muted, fontSize = 19.sp))
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
                    BasicText("COMPOSE PILL  /  LIVE DEMO", style = TextStyle(color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                    BasicText(selected.replaceFirstChar { it.uppercase() }, Modifier.padding(top = 18.dp), style = TextStyle(color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Bold))
                    BasicText(when (selected) {
                        "saved" -> "Your saved collection, at a glance."
                        "profile" -> "A little space that is all yours."
                        else -> "A little more room to explore."
                    }, Modifier.padding(top = 6.dp), style = TextStyle(color = Muted, fontSize = 14.sp))
                }
            }
            item {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    BasicText("Choose a surface", style = TextStyle(color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                    Row(Modifier.padding(top = 12.dp).selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Frosted", "Glass", "Solid").forEach { option ->
                            Box(Modifier.defaultMinSize(minHeight = 48.dp)
                                .background(if (material == option) Ink else Color.White, RoundedCornerShape(50))
                                .selectable(selected = material == option, role = Role.RadioButton) { material = option }
                                .padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
                                BasicText(option, style = TextStyle(color = if (material == option) Color.White else Ink, fontSize = 12.sp))
                            }
                        }
                    }
                    BasicText("Tap a destination or drag across the bar. Scroll to see it compact.", Modifier.padding(top = 18.dp), style = TextStyle(color = Muted, fontSize = 13.sp, lineHeight = 19.sp))
                }
            }
            items(listOf("A calmer way to navigate" to "Designed for the places your app goes.", "Make it your own" to "Bring your colors, icons and destinations.", "Built to move" to "Selection follows along as you explore.", "Thoughtful by default" to "A small surface with room for detail.")) { (title, description) ->
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp)
                    .background(Color.White, RoundedCornerShape(18.dp)).padding(20.dp)) {
                    BasicText(title, style = TextStyle(color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold))
                    BasicText(description, Modifier.padding(top = 5.dp), style = TextStyle(color = Muted, fontSize = 13.sp))
                }
            }
        }
    }
}
