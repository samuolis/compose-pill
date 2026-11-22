package io.github.samuolis.pill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Position of the label relative to the icon. */
enum class PillItemLayout { Vertical, Horizontal }

/** The top offsets used by a stacked item with fixed rows during collapse. */
data class PillStackedOffsets(val icon: Dp, val label: Dp)

/** Shares icon and label placement with bars that draw their own chrome. */
@Composable
fun PillNavigationItemContent(
    layout: PillItemLayout,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    gap: Dp = 0.dp,
    stackedOffsets: PillStackedOffsets? = null,
) {
    when {
        layout == PillItemLayout.Horizontal -> Row(
            modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(Modifier.width(gap))
            label()
        }
        stackedOffsets == null -> Column(
            modifier,
            verticalArrangement = Arrangement.spacedBy(gap, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon()
            label()
        }
        else -> Box(modifier) {
            Box(Modifier.align(Alignment.TopCenter).offset(y = stackedOffsets.icon), contentAlignment = Alignment.Center) {
                icon()
            }
            Box(Modifier.align(Alignment.TopCenter).offset(y = stackedOffsets.label), contentAlignment = Alignment.Center) {
                label()
            }
        }
    }
}
