package io.github.samuolis.pill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeSourceRetention
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

@Stable
class PillBackdrop internal constructor(internal val state: HazeState)

@Composable
fun rememberPillBackdrop(): PillBackdrop {
    val state = rememberHazeState()
    return remember(state) { PillBackdrop(state) }
}

fun Modifier.pillBackdropSource(backdrop: PillBackdrop): Modifier = hazeSource(backdrop.state)

enum class PillMaterial { Solid, Frosted, Glass }

data class PillBarStyle(
    val material: PillMaterial = PillMaterial.Frosted,
    val containerColor: Color = Color(0xE6F4F4F6),
    val contentColor: Color = Color(0xFF202126),
    val selectedColor: Color = Color.White,
    val selectedContentColor: Color = Color(0xFF202126),
    val borderColor: Color = Color(0x33FFFFFF),
    val shape: Shape = CircleShape,
    val elevation: Dp = 4.dp,
    val blurRadius: Dp = 20.dp,
) {
    init {
        require(elevation.value.isFinite() && elevation.value >= 0f) { "Elevation must be finite and nonnegative" }
        require(blurRadius.value.isFinite() && blurRadius.value >= 0f) { "Blur radius must be finite and nonnegative" }
    }
}

object PillBarDefaults {
    fun frosted(): PillBarStyle = PillBarStyle()
    fun solid(): PillBarStyle = PillBarStyle(material = PillMaterial.Solid)
    fun glass(): PillBarStyle = PillBarStyle(material = PillMaterial.Glass)
}

@Composable
fun PillSurface(
    modifier: Modifier = Modifier,
    backdrop: PillBackdrop? = null,
    style: PillBarStyle = PillBarDefaults.frosted(),
    content: @Composable BoxScope.() -> Unit,
) {
    val shaped = modifier.shadow(style.elevation, style.shape, clip = false).clip(style.shape)
    val background = if (style.material == PillMaterial.Glass && backdrop != null && style.shape is RoundedCornerShape) {
        val glass = remember(style.containerColor, style.shape) {
            GlassStyle {
                tint(style.containerColor)
                shape(style.shape)
            }
        }
        shaped.hazeGlass(input = HazeInput.Sources(backdrop.state, retention = HazeSourceRetention.ClearWhenUnavailable), style = glass)
    } else if (style.material != PillMaterial.Solid && backdrop != null) {
        val blur = remember(style.containerColor, style.blurRadius) {
            HazeBlurStyle {
                backgroundColor(style.containerColor)
                colorEffects(listOf(HazeColorEffect.tint(style.containerColor)))
                blurRadius(style.blurRadius)
                fallbackColorEffect(HazeColorEffect.tint(style.containerColor.copy(alpha = 1f)))
            }
        }
        shaped.hazeBlur(input = HazeInput.Sources(backdrop.state, retention = HazeSourceRetention.ClearWhenUnavailable), style = blur)
    } else {
        shaped.background(style.containerColor.copy(alpha = 1f), style.shape)
    }
    Box(background.border(1.dp, style.borderColor, style.shape), content = content)
}

/** Applies a progressive blur across the caller-sized edge; no effect is drawn outside its bounds. */
@Composable
fun PillProgressiveBackdrop(
    backdrop: PillBackdrop,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 20.dp,
    startIntensity: Float = 0f,
    endIntensity: Float = 1f,
) {
    require(startIntensity in 0f..1f && endIntensity in 0f..1f)
    require(blurRadius.value.isFinite() && blurRadius.value >= 0f)
    val style = remember(blurRadius, startIntensity, endIntensity) {
        HazeBlurStyle {
            blurRadius(blurRadius)
            progressive(HazeProgressive.verticalGradient(startIntensity = startIntensity, endIntensity = endIntensity))
        }
    }
    Box(modifier.hazeBlur(input = HazeInput.Sources(backdrop.state, retention = HazeSourceRetention.ClearWhenUnavailable), style = style))
}
