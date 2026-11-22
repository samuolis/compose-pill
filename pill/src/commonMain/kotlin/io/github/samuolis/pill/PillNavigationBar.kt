package io.github.samuolis.pill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
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
    itemLayout: PillItemLayout = PillItemLayout.Vertical,
    scrubHapticFeedback: HapticFeedbackType? = HapticFeedbackType.SegmentTick,
) {
    validPillSelection(items, selectedKey)
    if (items.isEmpty()) return
    val direction = LocalLayoutDirection.current
    val fontScale = LocalDensity.current.fontScale
    val showExpanded = expanded || fontScale > 1.3f
    val rtl = direction == LayoutDirection.Rtl
    val scrub = remember { PillScrubState() }
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentExpanded by rememberUpdatedState(expanded)
    val currentOnExpandRequest by rememberUpdatedState(onExpandRequest)
    val itemKeys = items.map { it.key to it.enabled }
    LaunchedEffect(selectedKey, itemKeys) { scrub.cancel() }
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
                val density = LocalDensity.current
                val slotPx = with(density) { slotWidth.toPx() }
                val selectedIndex = items.indexOfFirst { it.key == selectedKey }
                val targetOffset = slotWidth * selectedIndex.coerceAtLeast(0)
                val indicator = remember { Animatable(targetOffset, Dp.VectorConverter) }
                fun logicalOffset(physicalX: Int): Dp = with(density) {
                    if (rtl) (slotPx * (items.size - 1) - physicalX).toDp() else physicalX.toDp()
                }
                LaunchedEffect(targetOffset, scrub.isScrubbing, motionEnabled, rtl) {
                    if (!scrub.isScrubbing) {
                        scrub.indicatorBounds?.let {
                            indicator.snapTo(logicalOffset(it.offset.x))
                            scrub.clearBounds()
                        }
                        if (motionEnabled) indicator.animateTo(targetOffset, spring())
                        else indicator.snapTo(targetOffset)
                    }
                }
                val indicatorOffset = indicator.value
                val selectedOffsetPx = with(density) { indicatorOffset.toPx() }
                val physicalOffset = if (rtl) slotPx * (items.size - 1) - selectedOffsetPx else selectedOffsetPx
                val selectedBounds = PillIndicatorBounds(
                    IntOffset(physicalOffset.roundToInt(), 0),
                    IntSize(slotPx.roundToInt(), 1),
                )
                fun boundsAt(index: Int): PillIndicatorBounds {
                    val slot = if (rtl) items.lastIndex - index else index
                    return PillIndicatorBounds(IntOffset((slot * slotPx).roundToInt(), 0), selectedBounds.size)
                }
                val targets = remember(itemKeys, slotPx, rtl) {
                    items.mapIndexedNotNull { index, item ->
                        if (item.enabled) PillScrubTarget(item.key, boundsAt(index)) else null
                    }
                }
                val bounds = scrub.indicatorBounds ?: selectedBounds
                val displayedOffset = scrub.indicatorBounds?.let { logicalOffset(it.offset.x) } ?: indicatorOffset
                val barHeight by animateDpAsState(
                    if (showExpanded) 56.dp * fontScale.coerceAtLeast(1f) else 48.dp,
                    animationSpec = if (motionEnabled) spring() else snap(), label = "pill-height",
                )
                val canScrub = scrubEnabled && !overflow && targets.isNotEmpty()
                LaunchedEffect(canScrub) { if (!canScrub) scrub.cancel() }
                val gesture = if (canScrub) {
                    Modifier.pillNavigationScrub(
                        key = itemKeys,
                        hapticFeedback = scrubHapticFeedback,
                        onStart = { scrub.begin(bounds, targets) },
                        onMove = { scrub.update(it, targets) != null },
                        onEnd = {
                            val target = scrub.finish()
                            if (target != null) {
                                if (!currentExpanded) currentOnExpandRequest()
                                currentOnSelect(target)
                            }
                        },
                        onCancel = scrub::cancel,
                    )
                } else Modifier
                Box(Modifier.then(if (overflow) Modifier.horizontalScroll(rememberScrollState()) else Modifier)) {
                    Box(gesture.width(contentWidth).heightIn(min = barHeight).selectableGroup()) {
                        if (selectedIndex >= 0 || scrub.isScrubbing) {
                            Box(Modifier.matchParentSize()) {
                                Box(Modifier.offset(x = displayedOffset).width(slotWidth).fillMaxHeight().background(style.selectedColor, style.shape))
                            }
                        }
                        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            items.forEachIndexed { index, item ->
                                key(item.key) {
                                    val selected = item.key == selectedKey
                                    val requester = remember { BringIntoViewRequester() }
                                    LaunchedEffect(overflow, selected) {
                                        if (overflow && selected) {
                                            withFrameNanos { }
                                            requester.bringIntoView()
                                        }
                                    }
                                    val active = (scrub.isScrubbing || selectedIndex >= 0) &&
                                        pillIndicatorCoverage(boundsAt(index), bounds) > 0.5f
                                    PillNavigationItemContent(
                                        layout = itemLayout,
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                            .sizeIn(minWidth = 48.dp, minHeight = barHeight)
                                            .bringIntoViewRequester(requester)
                                            .clip(style.shape)
                                            .selectable(selected, enabled = item.enabled, role = Role.Tab) {
                                                if (!expanded) onExpandRequest()
                                                onSelect(item.key)
                                            }.clearAndSetSemantics { contentDescription = item.contentDescription }
                                            .alpha(if (item.enabled) 1f else 0.4f),
                                        gap = if (itemLayout == PillItemLayout.Horizontal && showExpanded) 4.dp else 0.dp,
                                        icon = { Box { item.icon(active); item.badge?.invoke() } },
                                        label = {
                                            val label: @Composable () -> Unit = {
                                                androidx.compose.foundation.text.BasicText(
                                                    item.label,
                                                    style = androidx.compose.ui.text.TextStyle(
                                                        color = if (active) style.selectedContentColor else style.contentColor,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                    ),
                                                )
                                            }
                                            if (motionEnabled) AnimatedVisibility(showExpanded) { label() }
                                            else if (showExpanded) label()
                                        },
                                    )
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
