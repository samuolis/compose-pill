package io.github.samuolis.pill.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.github.samuolis.pill.*

@Composable
fun PillDemo() {
    var selected by remember { mutableStateOf("home") }
    val behavior = rememberPillBarScrollBehavior(PillMinimizeBehavior.OnScrollDown)
    val destinations = listOf("home", "messages", "profile").map { key ->
        PillNavigationItem(key, key.replaceFirstChar { it.uppercase() }, icon = { active ->
            BasicText(if (active) "●" else "○")
        })
    }
    PillBarLayout(
        bar = { backdrop ->
            PillNavigationBar(destinations, selected, { selected = it }, backdrop = backdrop,
                expanded = behavior.expanded, onExpandRequest = behavior::expand, scrubEnabled = true)
        },
    ) { contentPadding ->
        LazyColumn(Modifier.fillMaxSize().background(Color(0xFFE7EAF5))
            .nestedScroll(behavior.nestedScrollConnection), contentPadding = contentPadding) {
            items((1..40).toList()) { index ->
                BasicText("$selected · Item $index", Modifier.fillMaxWidth().padding(24.dp))
            }
        }
    }
}
