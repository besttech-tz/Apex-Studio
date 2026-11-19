package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.BiologicalSex
import kotlin.math.roundToInt

/**
 * Computes human-readable BMI & honest body-frame classification so users immediately see
 * how their exact Height (cm) and Weight (kg) translate into a real body build — e.g.,
 * 200 cm & 59 kg -> BMI 14.8 ("Ultra-Slim Frame"), while 165 cm & 67 kg -> BMI 24.6 ("Healthy Normal Build").
 */
data class BiometricFrameSnapshot(
    val bmi: Float,
    val frameBadge: String,
    val frameHint: String,
    val badgeBgColor: Color,
    val badgeTextColor: Color,
    val matchedStartingShapeId: String,
    val figureHeightRatio: Float,
    val waistWidthFactor: Float
)

object BiometricSilhouetteMath {

    fun analyze(
        weightKg: Float,
        heightCm: Float,
        sex: BiologicalSex = BiologicalSex.MALE
    ): BiometricFrameSnapshot {
        val safeH = (heightCm / 100f).coerceIn(1.35f, 2.25f)
        val bmi = (weightKg / (safeH * safeH)).coerceIn(11f, 55f)
        val bmiRounded = (bmi * 10f).roundToInt() / 10f

        val matchedShapeId = when {
            bmi < 18.8f -> "shape_5_underweight"
            bmi < 22.0f -> "shape_3_skinny_fat"
            bmi < 25.4f -> "shape_4_average_untrained"
            bmi < 30.5f -> if (heightCm < 168f) "shape_7_short_stocky" else "shape_2_soft_midsection"
            else -> "shape_1_high_bf"
        }

        val heightFrac = ((heightCm.coerceIn(140f, 215f) - 140f) / (215f - 140f)).coerceIn(0f, 1f)
        // Standing height proportion inside the 140..215cm viewport (0.52 at 140cm -> 0.86 at 215cm)
        val figureHeightRatio = 0.52f + heightFrac * 0.34f

        val massT = ((bmi - 14f) / 25f).coerceIn(0f, 1.08f)
        val obeseT = ((bmi - 25.5f) / 13.5f).coerceIn(0f, 1.1f)
        val waistWidthFactor = if (sex == BiologicalSex.FEMALE) {
            0.056f + massT * 0.058f + obeseT * 0.048f
        } else {
            0.064f + massT * 0.066f + obeseT * 0.058f
        }

        return when {
            bmi < 16.5f -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Ultra-Slim Frame • BMI $bmiRounded",
                frameHint = "Very low mass for your height — focus on clean calories & progressive strength.",
                badgeBgColor = Color(0xFFEFF6FF),
                badgeTextColor = Color(0xFF1D4ED8),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
            bmi < 18.8f -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Slim / Underweight • BMI $bmiRounded",
                frameHint = "Slender frame — ideal starting point to pack on lean muscle & posture.",
                badgeBgColor = Color(0xFFEFF6FF),
                badgeTextColor = Color(0xFF1E40AF),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
            bmi < 22.0f -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Lean / Light Frame • BMI $bmiRounded",
                frameHint = "Light bodyweight — tighten core & build athletic muscle definition.",
                badgeBgColor = Color(0xFFECFDF5),
                badgeTextColor = Color(0xFF047857),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
            bmi < 25.4f -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Healthy Normal Build • BMI $bmiRounded",
                frameHint = "Balanced everyday weight (not overweight) — ready to tone or add solid muscle.",
                badgeBgColor = Color(0xFFDCFCE7),
                badgeTextColor = Color(0xFF166534),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
            bmi < 30.5f -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Overweight / Belly Range • BMI $bmiRounded",
                frameHint = "Extra midsection weight — Phase 1 prioritizes fat burn & waist reduction first.",
                badgeBgColor = Color(0xFFFFF7ED),
                badgeTextColor = Color(0xFFC2410C),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
            else -> BiometricFrameSnapshot(
                bmi = bmiRounded,
                frameBadge = "Heavy / Obese Range • BMI $bmiRounded",
                frameHint = "Phase 1 auto-tunes to joint-safe fat loss & high-calorie compounds first.",
                badgeBgColor = Color(0xFFFEF2F2),
                badgeTextColor = Color(0xFFB91C1C),
                matchedStartingShapeId = matchedShapeId,
                figureHeightRatio = figureHeightRatio,
                waistWidthFactor = waistWidthFactor
            )
        }
    }

    /**
     * Resolves the photorealistic studio body preset + fine-grained within-bracket horizontal proportion
     * so every Weight +/- adjustment visibly responds while keeping 100% photorealistic human consistency
     * across Step 1, Step 2, and Step 3.
     */
    internal fun resolvePhotorealisticStudioVisual(bmi: Float): Pair<PhysiqueSilhouettePreset, Float> {
        return when {
            bmi < 18.8f -> {
                val t = ((bmi - 13.5f) / (18.8f - 13.5f)).coerceIn(0f, 1f)
                PhysiqueSilhouettePreset.START_UNDERWEIGHT to (0.91f + t * 0.09f)
            }
            bmi < 22.0f -> {
                val t = ((bmi - 18.8f) / (22.0f - 18.8f)).coerceIn(0f, 1f)
                PhysiqueSilhouettePreset.START_SKINNY_FAT to (0.95f + t * 0.06f)
            }
            bmi < 25.4f -> {
                val t = ((bmi - 22.0f) / (25.4f - 22.0f)).coerceIn(0f, 1f)
                PhysiqueSilhouettePreset.START_AVERAGE to (0.96f + t * 0.06f)
            }
            bmi < 30.5f -> {
                val t = ((bmi - 25.4f) / (30.5f - 25.4f)).coerceIn(0f, 1f)
                PhysiqueSilhouettePreset.START_SOFT_MIDSECTION to (0.97f + t * 0.08f)
            }
            else -> {
                val t = ((bmi - 30.5f) / 14.0f).coerceIn(0f, 1f)
                PhysiqueSilhouettePreset.START_HIGH_BF to (1.00f + t * 0.12f)
            }
        }
    }
}

/**
 * Interactive Step-1 Side-by-Side Photorealistic Body Studio:
 * - Left: Height & Weight +/- controls passed via [controlsContent]
 * - Right: Unified Photorealistic Studio Human Figure standing on a calibrated 140–215 cm stadiometer
 *   that physically grows taller/shorter with [heightCm] and transitions smoothly across real human builds
 *   with [weightKg] — 100% consistent with Step 2 and Step 3!
 */
@Composable
fun LiveBiometricSilhouetteStudio(
    sex: BiologicalSex,
    heightCm: Float,
    weightKg: Float,
    modifier: Modifier = Modifier,
    controlsContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val snapshot = BiometricSilhouetteMath.analyze(weightKg = weightKg, heightCm = heightCm, sex = sex)
    val (studioPreset, targetWidthScale) = BiometricSilhouetteMath.resolvePhotorealisticStudioVisual(snapshot.bmi)

    val clampedHeight = heightCm.coerceIn(140f, 215f)
    val heightFrac = ((clampedHeight - 140f) / (215f - 140f)).coerceIn(0f, 1f)

    // Calibrated viewport: 228.dp total box height, floor line at 12.dp from bottom,
    // standing figure height ranges from 112.dp (140 cm) up to 184.dp (215 cm) — unmistakable real-time height scale!
    val targetFigureHeightDp = (112f + heightFrac * 72f).dp
    val animatedFigureHeightDp by animateDpAsState(
        targetValue = targetFigureHeightDp,
        animationSpec = tween(durationMillis = 150),
        label = "studio_standing_height_dp"
    )
    val animatedHeightFrac by animateFloatAsState(
        targetValue = heightFrac,
        animationSpec = tween(durationMillis = 150),
        label = "studio_height_frac"
    )
    val animatedWidthScale by animateFloatAsState(
        targetValue = targetWidthScale,
        animationSpec = tween(durationMillis = 150),
        label = "studio_width_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("live_biometric_silhouette_studio"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column: Height & Weight controls
            Box(modifier = Modifier.weight(1.08f)) {
                controlsContent()
            }

            // Right column: Photorealistic Human Figure + Calibrated 140–215 cm Stadiometer
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .weight(0.92f)
                    .height(228.dp)
                    .testTag("live_silhouette_preview_box")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // 1. Photorealistic Full-Standing Human Figure anchored to the stadiometer floor
                    Crossfade(
                        targetState = sex to studioPreset,
                        animationSpec = tween(durationMillis = 180),
                        label = "studio_human_crossfade",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, end = 4.dp, bottom = 12.dp)
                            .fillMaxWidth()
                            .height(animatedFigureHeightDp)
                    ) { (activeSex, activePreset) ->
                        val standingBitmap = remember(activeSex, activePreset) {
                            PhysiqueSheetSlicer.getFullStandingBitmap(context, activeSex, activePreset)
                        }
                        if (standingBitmap != null) {
                            Image(
                                bitmap = standingBitmap,
                                contentDescription = "${activePreset.badgeFor(activeSex)} ${clampedHeight.roundToInt()} cm",
                                contentScale = ContentScale.Fit,
                                alignment = Alignment.BottomCenter,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = animatedWidthScale,
                                        transformOrigin = TransformOrigin(0.5f, 1.0f)
                                    )
                            )
                        }
                    }

                    // 2. Calibrated Stadiometer Ruler + Live Crown-of-Head Height Crossbar Overlay
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        val w = size.width
                        val h = size.height
                        val floorY = h - 12.dp.toPx()
                        val minCrownY = floorY - 112.dp.toPx()
                        val maxCrownY = floorY - 184.dp.toPx()
                        val rulerX = 16.dp.toPx()

                        // Vertical stadiometer rod
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(rulerX, maxCrownY - 6.dp.toPx()),
                            end = Offset(rulerX, floorY),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        // Calibrated 140..215 cm ticks every 15 cm
                        listOf(140f, 155f, 170f, 185f, 200f, 215f).forEach { cm ->
                            val f = ((cm - 140f) / (215f - 140f)).coerceIn(0f, 1f)
                            val y = minCrownY - f * (minCrownY - maxCrownY)
                            drawLine(
                                color = Color(0xFF94A3B8),
                                start = Offset(rulerX - 4.dp.toPx(), y),
                                end = Offset(rulerX + 4.dp.toPx(), y),
                                strokeWidth = 1.2.dp.toPx()
                            )
                        }

                        // Studio floor platform line
                        drawLine(
                            color = Color(0xFF64748B),
                            start = Offset(rulerX, floorY),
                            end = Offset(w - 6.dp.toPx(), floorY),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Live Crown-of-Head Stadiometer Crossbar
                        val activeCrownY = minCrownY - animatedHeightFrac * (minCrownY - maxCrownY)
                        drawLine(
                            color = Color(0xFF2563EB),
                            start = Offset(rulerX - 4.dp.toPx(), activeCrownY),
                            end = Offset(w - 10.dp.toPx(), activeCrownY),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // 3. Stadiometer cm scale labels along left rail
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .padding(start = 3.dp, top = 30.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("215", "180", "140").forEach { cmTxt ->
                            Text(
                                text = cmTxt,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // 4. Live floating height + weight readout pill at top-right (above max head height)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "${clampedHeight.roundToInt()}cm • ${weightKg.roundToInt()}kg",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Honest Live Frame Readout Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = snapshot.badgeBgColor,
            border = BorderStroke(1.dp, snapshot.badgeTextColor.copy(alpha = 0.24f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_biometric_frame_badge")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = snapshot.frameBadge,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = snapshot.badgeTextColor
                )
                Text(
                    text = snapshot.frameHint,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF334155)
                )
            }
        }
    }
}
