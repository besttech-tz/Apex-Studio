package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.BiologicalSex
import com.example.domain.CurrentBfBracket
import com.example.domain.GoalBfBracket
import java.util.concurrent.ConcurrentHashMap

/**
 * Maps each Starting Body (5 clear mirror baselines) and each Target Body
 * (4 distinct, popular goal physiques) to its studio-lit figure using simple everyday language.
 */
enum class PhysiqueSilhouettePreset(
    val isStartingLineup: Boolean,
    val figureIndex: Int,
    val shortMetricBadge: String,
    val earnedMuscleSummaryMale: String,
    val earnedMuscleSummaryFemale: String,
    val horizontalScale: Float = 1.0f,
    val verticalScale: Float = 1.0f,
    val shortMetricBadgeFemale: String = shortMetricBadge
) {
    // REALISTIC STARTING BODY SHAPES (Left-to-Right indices 0..4 in img_*_start_v2 + distinct stocky/obese framing)
    START_HIGH_BF(
        isStartingLineup = true,
        figureIndex = 0,
        shortMetricBadge = "VERY OBESE",
        earnedMuscleSummaryMale = "Heavy Belly, Chest & Frame",
        earnedMuscleSummaryFemale = "Heavy Belly, Hips & Frame",
        horizontalScale = 1.06f,
        verticalScale = 0.98f
    ),
    START_SHORT_STOCKY(
        isStartingLineup = true,
        figureIndex = 1,
        shortMetricBadge = "SHORT • STOCKY",
        earnedMuscleSummaryMale = "Compact Frame • Round Belly",
        earnedMuscleSummaryFemale = "Petite / Compact • Full Belly",
        horizontalScale = 1.07f,
        verticalScale = 0.88f
    ),
    START_SOFT_MIDSECTION(
        isStartingLineup = true,
        figureIndex = 1,
        shortMetricBadge = "OVERWEIGHT",
        earnedMuscleSummaryMale = "Big Belly & Side Fat",
        earnedMuscleSummaryFemale = "Full Lower Belly & Hips",
        horizontalScale = 1.01f,
        verticalScale = 0.99f
    ),
    START_SKINNY_FAT(
        isStartingLineup = true,
        figureIndex = 2,
        shortMetricBadge = "SLIM + BELLY",
        earnedMuscleSummaryMale = "Thin Arms • Soft Belly",
        earnedMuscleSummaryFemale = "Slim Arms • Soft Waist"
    ),
    START_AVERAGE(
        isStartingLineup = true,
        figureIndex = 3,
        shortMetricBadge = "AVERAGE",
        earnedMuscleSummaryMale = "Normal Weight • Untrained",
        earnedMuscleSummaryFemale = "Normal Weight • Soft Tone"
    ),
    START_UNDERWEIGHT(
        isStartingLineup = true,
        figureIndex = 4,
        shortMetricBadge = "VERY THIN",
        earnedMuscleSummaryMale = "Skinny Frame • Low Mass",
        earnedMuscleSummaryFemale = "Very Slim • Needs Strength"
    ),

    // TARGET BODY GOALS (Left-to-Right unique indices 0..3 — ZERO duplicate figures)
    GOAL_LOSE_BELLY_FAT(
        isStartingLineup = false,
        figureIndex = 0,
        shortMetricBadge = "SLIM & LIGHT",
        shortMetricBadgeFemale = "SLIM & FLAT",
        earnedMuscleSummaryMale = "Smaller Waist • Lighter Frame",
        earnedMuscleSummaryFemale = "Smaller Waist • Slim Thighs",
        horizontalScale = 0.94f,
        verticalScale = 1.0f
    ),
    GOAL_SHREDDED_SIXPACK(
        isStartingLineup = false,
        figureIndex = 1,
        shortMetricBadge = "SIX-PACK & FIT",
        shortMetricBadgeFemale = "TONED WAIST",
        earnedMuscleSummaryMale = "Visible Abs • High Stamina",
        earnedMuscleSummaryFemale = "Flat Tummy • Sleek Tone",
        horizontalScale = 0.97f,
        verticalScale = 1.0f
    ),
    GOAL_VTAPER_MANHWA(
        isStartingLineup = false,
        figureIndex = 2,
        shortMetricBadge = "WIDE V-SHAPE",
        shortMetricBadgeFemale = "LIFTED BOOTY",
        earnedMuscleSummaryMale = "Wide Shoulders • V-Taper",
        earnedMuscleSummaryFemale = "Lifted Buttocks • Cinched Waist",
        horizontalScale = 1.04f,
        verticalScale = 1.0f
    ),
    GOAL_DENSE_ARMOR(
        isStartingLineup = false,
        figureIndex = 3,
        shortMetricBadge = "HEAVY MASS",
        shortMetricBadgeFemale = "HOURGLASS CURVES",
        earnedMuscleSummaryMale = "Thick Limbs • Raw Strength",
        earnedMuscleSummaryFemale = "Rounder Booty • Curvy Hips",
        horizontalScale = 1.05f,
        verticalScale = 1.0f
    ),
    GOAL_RECOMP_FIX(
        isStartingLineup = false,
        figureIndex = 2,
        shortMetricBadge = "WIDE V-SHAPE",
        shortMetricBadgeFemale = "LIFTED BOOTY",
        earnedMuscleSummaryMale = "Wide Shoulders • V-Taper",
        earnedMuscleSummaryFemale = "Lifted Buttocks • Hourglass",
        horizontalScale = 1.04f,
        verticalScale = 1.0f
    );

    fun badgeFor(sex: BiologicalSex): String =
        if (sex == BiologicalSex.FEMALE) shortMetricBadgeFemale else shortMetricBadge

    fun muscleCalloutFor(sex: BiologicalSex): String =
        if (sex == BiologicalSex.FEMALE) earnedMuscleSummaryFemale else earnedMuscleSummaryMale

    companion object {
        fun fromCardId(cardId: String?): PhysiqueSilhouettePreset = when (cardId) {
            "shape_1_high_bf", "shape_6_very_obese" -> START_HIGH_BF
            "shape_7_short_stocky" -> START_SHORT_STOCKY
            "shape_2_soft_midsection" -> START_SOFT_MIDSECTION
            "shape_3_skinny_fat" -> START_SKINNY_FAT
            "shape_4_average_untrained" -> START_AVERAGE
            "shape_5_underweight" -> START_UNDERWEIGHT
            "goal_1_fat_loss" -> GOAL_LOSE_BELLY_FAT
            "goal_3_athletic" -> GOAL_SHREDDED_SIXPACK
            "goal_5_recomp" -> GOAL_RECOMP_FIX
            "goal_2_vtaper" -> GOAL_VTAPER_MANHWA
            "goal_4_power_mass" -> GOAL_DENSE_ARMOR
            else -> START_SOFT_MIDSECTION
        }

        fun fromCurrentBracket(bracket: CurrentBfBracket): PhysiqueSilhouettePreset = when (bracket) {
            CurrentBfBracket.BF_30_PLUS,
            CurrentBfBracket.FEMALE_BF_32_PLUS -> START_HIGH_BF
            CurrentBfBracket.BF_24_28,
            CurrentBfBracket.FEMALE_BF_25_29 -> START_SOFT_MIDSECTION
            CurrentBfBracket.BF_18_22,
            CurrentBfBracket.FEMALE_BF_20_24 -> START_SKINNY_FAT
            CurrentBfBracket.BF_15,
            CurrentBfBracket.FEMALE_BF_LEAN_BASE -> START_UNDERWEIGHT
        }

        fun fromGoalBracket(bracket: GoalBfBracket): PhysiqueSilhouettePreset = when (bracket) {
            GoalBfBracket.GOAL_LEAN_FLAT,
            GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS -> GOAL_LOSE_BELLY_FAT
            GoalBfBracket.FEMALE_GOAL_TONED_LEAN -> GOAL_SHREDDED_SIXPACK
            GoalBfBracket.FEMALE_GOAL_HOURGLASS -> GOAL_DENSE_ARMOR
            GoalBfBracket.GOAL_MASS_SIZE -> GOAL_DENSE_ARMOR
            GoalBfBracket.GOAL_10_12 -> GOAL_VTAPER_MANHWA
            else -> GOAL_SHREDDED_SIXPACK
        }
    }
}

internal object PhysiqueSheetSlicer {
    private val sheetCache = ConcurrentHashMap<Int, List<ImageBitmap>>()
    private val fullStandingCache = ConcurrentHashMap<Int, List<ImageBitmap>>()

    @DrawableRes
    fun resolveSheetResId(sex: BiologicalSex, isStartingLineup: Boolean): Int {
        return if (sex == BiologicalSex.FEMALE) {
            if (isStartingLineup) {
                R.drawable.img_female_start_v2_1794487786584
            } else {
                R.drawable.img_female_target_shape_v4_1794909697916
            }
        } else {
            if (isStartingLineup) {
                R.drawable.img_male_start_v2_1794487758496
            } else {
                R.drawable.img_male_target_v2_1794487756295
            }
        }
    }

    fun getFigureBitmap(
        context: Context,
        sex: BiologicalSex,
        preset: PhysiqueSilhouettePreset
    ): ImageBitmap? {
        val resId = resolveSheetResId(sex, preset.isStartingLineup)
        val expectedCount = if (preset.isStartingLineup) 5 else 4
        val figures = sheetCache.getOrPut(resId) {
            val isFullStandingFemaleTarget = (resId == R.drawable.img_female_target_shape_v4_1794909697916)
            sliceFiguresFromSheet(
                context = context,
                resId = resId,
                expectedCount = expectedCount,
                isFullStandingFemaleTarget = isFullStandingFemaleTarget,
                fullStandingBody = false
            )
        }
        return figures.getOrNull(preset.figureIndex.coerceIn(0, (figures.size - 1).coerceAtLeast(0)))
    }

    /**
     * Full head-to-shoes studio human photograph used by Step 1's Live Height & Weight Stadiometer
     * so Step 1, Step 2, and Step 3 share the exact same photorealistic human models.
     */
    fun getFullStandingBitmap(
        context: Context,
        sex: BiologicalSex,
        preset: PhysiqueSilhouettePreset
    ): ImageBitmap? {
        val resId = resolveSheetResId(sex, preset.isStartingLineup)
        val expectedCount = if (preset.isStartingLineup) 5 else 4
        val figures = fullStandingCache.getOrPut(resId) {
            val isFullStandingFemaleTarget = (resId == R.drawable.img_female_target_shape_v4_1794909697916)
            sliceFiguresFromSheet(
                context = context,
                resId = resId,
                expectedCount = expectedCount,
                isFullStandingFemaleTarget = isFullStandingFemaleTarget,
                fullStandingBody = true
            )
        }
        return figures.getOrNull(preset.figureIndex.coerceIn(0, (figures.size - 1).coerceAtLeast(0)))
    }

    private fun sliceFiguresFromSheet(
        context: Context,
        @DrawableRes resId: Int,
        expectedCount: Int,
        isFullStandingFemaleTarget: Boolean,
        fullStandingBody: Boolean = false
    ): List<ImageBitmap> {
        val options = BitmapFactory.Options().apply {
            inScaled = false
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val sheet = BitmapFactory.decodeResource(context.resources, resId, options)
            ?: return emptyList()

        val w = sheet.width
        val h = sheet.height
        if (w < 10 || h < 10) {
            val single = sheet.asImageBitmap()
            return List(expectedCount) { single }
        }

        val pixels = IntArray(w * h)
        sheet.getPixels(pixels, 0, w, 0, 0, w, h)

        val colHasBody = BooleanArray(w)
        var globalTopY = h
        var globalBottomY = 0

        for (x in 0 until w) {
            var nonWhiteCount = 0
            var y = 0
            while (y < h) {
                val px = pixels[y * w + x]
                val r = (px shr 16) and 0xFF
                val g = (px shr 8) and 0xFF
                val b = px and 0xFF
                if (r < 242 || g < 242 || b < 242) {
                    nonWhiteCount++
                    if (y < globalTopY) globalTopY = y
                    if (y > globalBottomY) globalBottomY = y
                }
                y += 2
            }
            if (nonWhiteCount >= 4) {
                colHasBody[x] = true
            }
        }

        val maxBridge = (w / 180).coerceAtLeast(3)
        var xIdx = 0
        while (xIdx < w) {
            if (!colHasBody[xIdx]) {
                var gapEnd = xIdx
                while (gapEnd < w && !colHasBody[gapEnd]) gapEnd++
                if (xIdx > 0 && gapEnd < w && (gapEnd - xIdx) <= maxBridge) {
                    for (k in xIdx until gapEnd) colHasBody[k] = true
                }
                xIdx = gapEnd
            } else {
                xIdx++
            }
        }

        val rawSegments = mutableListOf<Pair<Int, Int>>()
        var segStart = -1
        for (x in 0 until w) {
            if (colHasBody[x]) {
                if (segStart == -1) segStart = x
            } else if (segStart != -1) {
                if ((x - segStart) >= (w * 0.06f).toInt()) {
                    rawSegments.add(segStart to (x - 1))
                }
                segStart = -1
            }
        }
        if (segStart != -1 && (w - segStart) >= (w * 0.06f).toInt()) {
            rawSegments.add(segStart to (w - 1))
        }

        val segments: List<Pair<Int, Int>> = if (rawSegments.size == expectedCount) {
            rawSegments
        } else {
            (0 until expectedCount).map { idx ->
                val startX = (idx * w) / expectedCount
                val endX = ((idx + 1) * w) / expectedCount - 1
                startX to endX
            }
        }

        if (globalTopY >= globalBottomY) {
            globalTopY = 0
            globalBottomY = h
        }

        val fullFigHeight = (globalBottomY - globalTopY).coerceAtLeast(1)
        val cropTopY = if (fullStandingBody) {
            (globalTopY - (fullFigHeight * 0.015f).toInt()).coerceAtLeast(0)
        } else {
            (globalTopY - (fullFigHeight * 0.03f).toInt()).coerceAtLeast(0)
        }
        val cropBottomY = if (fullStandingBody) {
            (globalBottomY + (fullFigHeight * 0.01f).toInt()).coerceAtMost(h)
        } else {
            val verticalRatio = if (isFullStandingFemaleTarget) 0.78f else 0.74f
            (globalTopY + (fullFigHeight * verticalRatio).toInt()).coerceAtMost(h)
        }
        val targetHeight = (cropBottomY - cropTopY).coerceAtLeast(1)

        val maxSegWidth = segments.maxOf { (s, e) -> e - s + 1 }
        val targetWidth = (maxSegWidth * (if (fullStandingBody) 1.08f else 1.12f)).toInt().coerceAtLeast(1)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val sliced = segments.map { (sX, eX) ->
            val centerX = (sX + eX) / 2
            val desiredLeft = centerX - targetWidth / 2
            val desiredRight = desiredLeft + targetWidth

            val srcLeft = desiredLeft.coerceIn(0, w)
            val srcRight = desiredRight.coerceIn(0, w)
            val dstLeft = srcLeft - desiredLeft
            val dstRight = dstLeft + (srcRight - srcLeft)

            val outBmp = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(outBmp)
            canvas.drawColor(android.graphics.Color.WHITE)

            if (srcRight > srcLeft) {
                val srcRect = Rect(srcLeft, cropTopY, srcRight, cropBottomY)
                val dstRect = Rect(dstLeft, 0, dstRight, targetHeight)
                canvas.drawBitmap(sheet, srcRect, dstRect, paint)
            }
            outBmp.asImageBitmap()
        }

        sheet.recycle()
        return sliced
    }
}

/**
 * Properly fitted, clean studio physique portrait for Starting Body & Target Body cards.
 */
@Composable
fun AnatomicalSilhouetteBadge(
    preset: PhysiqueSilhouettePreset,
    sex: BiologicalSex,
    accentColor: Color,
    isSelected: Boolean,
    largeShowcase: Boolean = !preset.isStartingLineup,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val figureBitmap = remember(sex, preset) {
        PhysiqueSheetSlicer.getFigureBitmap(context, sex, preset)
    }

    val cardWidth = if (largeShowcase) 104.dp else 94.dp
    val cardHeight = if (largeShowcase) 136.dp else 124.dp

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (figureBitmap != null) {
            Image(
                bitmap = figureBitmap,
                contentDescription = preset.badgeFor(sex),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .graphicsLayer(
                        scaleX = preset.horizontalScale,
                        scaleY = preset.verticalScale
                    )
            )
        }
    }
}

/**
 * Clean Side-by-Side Current Body -> Target Body Visual Comparison Banner (Zero nested box borders).
 */
@Composable
fun TransformationSilhouetteComparisonBanner(
    currentBracket: CurrentBfBracket,
    goalBracket: GoalBfBracket,
    sex: BiologicalSex,
    startPresetOverride: PhysiqueSilhouettePreset? = null,
    goalPresetOverride: PhysiqueSilhouettePreset? = null,
    startTitleOverride: String? = null,
    goalTitleOverride: String? = null,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val startPreset = startPresetOverride ?: PhysiqueSilhouettePreset.fromCurrentBracket(currentBracket)
    val goalPreset = goalPresetOverride ?: PhysiqueSilhouettePreset.fromGoalBracket(goalBracket)

    val startBitmap = remember(sex, startPreset) {
        PhysiqueSheetSlicer.getFigureBitmap(context, sex, startPreset)
    }
    val goalBitmap = remember(sex, goalPreset) {
        PhysiqueSheetSlicer.getFigureBitmap(context, sex, goalPreset)
    }

    val portraitWidth = if (compactMode) 104.dp else 118.dp
    val portraitHeight = if (compactMode) 132.dp else 148.dp

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // NOW
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "NOW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.6.sp
                )
                Box(
                    modifier = Modifier
                        .width(portraitWidth)
                        .height(portraitHeight)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (startBitmap != null) {
                        Image(
                            bitmap = startBitmap,
                            contentDescription = "Current Body",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = startPreset.horizontalScale,
                                    scaleY = startPreset.verticalScale
                                )
                        )
                    }
                }
                Text(
                    text = startTitleOverride ?: startPreset.badgeFor(sex),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "→",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // GOAL
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "GOAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.6.sp
                )
                Box(
                    modifier = Modifier
                        .width(portraitWidth)
                        .height(portraitHeight)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (goalBitmap != null) {
                        Image(
                            bitmap = goalBitmap,
                            contentDescription = "Target Body",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = goalPreset.horizontalScale,
                                    scaleY = goalPreset.verticalScale
                                )
                        )
                    }
                }
                Text(
                    text = goalTitleOverride ?: goalPreset.badgeFor(sex),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
