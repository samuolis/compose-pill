package io.github.samuolis.pill.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.samuolis.pill.sample.resources.Res
import io.github.samuolis.pill.sample.resources.inter_regular
import io.github.samuolis.pill.sample.resources.inter_semibold
import org.jetbrains.compose.resources.Font

@Composable
internal actual fun sampleFont(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
)
