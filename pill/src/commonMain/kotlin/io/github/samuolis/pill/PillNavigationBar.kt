package io.github.samuolis.pill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class PillNavigationItem(
    val key: String,
    val label: String,
    val icon: @Composable (selected: Boolean) -> Unit,
    val enabled: Boolean = true,
    val badge: (@Composable () -> Unit)? = null,
    val contentDescription: String = label,
) {
    init {
        require(key.isNotBlank()) { "Destination keys cannot be blank" }
        require(label.isNotBlank()) { "Destination labels cannot be blank" }
        require(contentDescription.isNotBlank()) { "Destination descriptions cannot be blank" }
    }
}

internal fun validPillSelection(items: List<PillNavigationItem>, selectedKey: String?) {
    require(items.map { it.key }.distinct().size == items.size) { "Destination keys must be unique" }
    require(selectedKey == null || items.any { it.key == selectedKey }) { "Selected key is not a destination" }
}

internal fun scrubIndex(position: Float, width: Float, count: Int, rtl: Boolean): Int? {
    if (count == 0 || width <= 0f || !position.isFinite() || position < 0f || position > width) return null
    val index = (position / (width / count)).toInt().coerceIn(0, count - 1)
    return if (rtl) count - 1 - index else index
}

@Composable
fun PillNavigationBar(
    items: List<PillNavigationItem>,
    selectedKey: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: PillBackdrop? = null,
    expanded: Boolean = true,
    onExpandRequest: () -> Unit = {},
    scrubEnabled: Boolean = false,
    motionEnabled: Boolean = true,
    style: PillBarStyle = PillBarDefaults.frosted(),
    adjacentAction: (@Composable () -> Unit)? = null,
) {
    validPillSelection(items, selectedKey)
    if (items.isEmpty()) return
    val direction = LocalLayoutDirection.current
    val fontScale = LocalDensity.current.fontScale
    val showExpanded = expanded || fontScale > 1.3f
    val rtl = direction == LayoutDirection.Rtl
    var previewKey by remember { mutableStateOf<String?>(null) }
    var dragX by remember { mutableFloatStateOf(0f) }
    val currentItems by rememberUpdatedState(items)
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentExpanded by rememberUpdatedState(expanded)
    val currentOnExpandRequest by rememberUpdatedState(onExpandRequest)
    LaunchedEffect(selectedKey, items.map { it.key }) { previewKey = null }
    val maxBarWidth by animateDpAsState(
        if (showExpanded) 400.dp else 64.dp * items.size,
        animationSpec = if (motionEnabled) spring() else snap(), label = "pill-width",
    )
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        PillSurface(Modifier.weight(1f, fill = false).widthIn(max = maxBarWidth), backdrop, style) {
            BoxWithConstraints(Modifier.fillMaxWidth().padding(4.dp)) {
                val contentWidth = maxOf(maxWidth, 48.dp * items.size)
                val overflow = contentWidth > maxWidth
                val slotWidth = contentWidth / items.size
                val widthPx = with(LocalDensity.current) { contentWidth.toPx() }
                val shownIndex = items.indexOfFirst { it.key == (previewKey ?: selectedKey) }
                val indicatorOffset by animateDpAsState(
                    targetValue = slotWidth * shownIndex.coerceAtLeast(0),
                    animationSpec = if (motionEnabled) spring() else snap(), label = "pill-indicator",
                )
                val displayedOffset = if (previewKey != null) {
                    with(LocalDensity.current) {
                        val slotPx = slotWidth.toPx()
                        val logicalX = if (rtl) widthPx - dragX else dragX
                        (logicalX - slotPx / 2).coerceIn(0f, widthPx - slotPx).toDp()
                    }
                } else indicatorOffset
                val barHeight by animateDpAsState(
                    if (showExpanded) 56.dp * fontScale.coerceAtLeast(1f) else 48.dp,
                    animationSpec = if (motionEnabled) spring() else snap(), label = "pill-height",
                )
                val gesture = if (scrubEnabled && !overflow) Modifier.pointerInput(items.map { it.key to it.enabled }, widthPx, rtl, selectedKey) {
                    detectHorizontalDragGestures(
                        onDragStart = { start ->
                            dragX = start.x
                            previewKey = scrubIndex(dragX, widthPx, currentItems.size, rtl)?.let { currentItems[it] }?.takeIf { it.enabled }?.key
                        },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            dragX = change.position.x
                            previewKey = scrubIndex(dragX, widthPx, currentItems.size, rtl)?.let { currentItems[it] }?.takeIf { it.enabled }?.key
                        },
                        onDragEnd = {
                            val target = previewKey
                            previewKey = null
                            if (target != null) {
                                if (!currentExpanded) currentOnExpandRequest()
                                currentOnSelect(target)
                            }
                        },
                        onDragCancel = { previewKey = null },
                    )
                } else Modifier
                Box(Modifier.then(if (overflow) Modifier.horizontalScroll(rememberScrollState()) else Modifier)) {
                Box(gesture.width(contentWidth).heightIn(min = barHeight).selectableGroup()) {
                    if (shownIndex >= 0) {
                        Box(Modifier.matchParentSize()) {
                            Box(Modifier.offset(x = displayedOffset).width(slotWidth).fillMaxHeight().background(style.selectedColor, style.shape))
                        }
                    }
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        items.forEach { item ->
                            val selected = item.key == selectedKey
                            Column(
                                Modifier.weight(1f).fillMaxHeight().sizeIn(minWidth = 48.dp, minHeight = barHeight)
                                    .selectable(selected, enabled = item.enabled, role = Role.Tab) {
                                        if (!expanded) onExpandRequest()
                                        onSelect(item.key)
                                    }.semantics { contentDescription = item.contentDescription }
                                    .alpha(if (item.enabled) 1f else 0.4f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box { item.icon(selected); item.badge?.invoke() }
                                val label: @Composable () -> Unit = {
                                    androidx.compose.foundation.text.BasicText(
                                        item.label,
                                        style = androidx.compose.ui.text.TextStyle(
                                            color = if (selected) style.selectedContentColor else style.contentColor,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        ),
                                    )
                                }
                                if (motionEnabled) AnimatedVisibility(showExpanded) { label() }
                                else if (showExpanded) label()
                            }
                        }
                    }
                }
                }
            }
        }
        adjacentAction?.invoke()
    }
}

@Composable
fun PillToolbar(
    modifier: Modifier = Modifier,
    backdrop: PillBackdrop? = null,
    style: PillBarStyle = PillBarDefaults.frosted(),
    content: @Composable RowScope.() -> Unit,
) {
    PillSurface(modifier, backdrop, style) {
        Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically, content = content)
    }
}
