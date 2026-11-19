package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.TextPrimarySlate
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.TimerAmber
import kotlin.math.roundToInt

private object MuscleMapBitmapCache {
    private val cache = object : LruCache<String, ImageBitmap>(2) {}
    private var sheetBitmap: Bitmap? = null

    fun getFrontAndBack(context: Context): Pair<ImageBitmap, ImageBitmap>? {
        val frontCached = cache.get("FRONT")
        val backCached = cache.get("BACK")
        if (frontCached != null && backCached != null) {
            return frontCached to backCached
        }

        val sheet = synchronized(this) {
            sheetBitmap ?: BitmapFactory.decodeResource(
                context.resources,
                R.drawable.img_muscle_map_1794517054643
            )?.also { sheetBitmap = it }
        } ?: return null

        val sw = sheet.width
        val sh = sheet.height

        val fx0 = (sw * 0.105f).roundToInt().coerceIn(0, sw - 2)
        val fx1 = (sw * 0.415f).roundToInt().coerceIn(fx0 + 1, sw)
        val fy0 = (sh * 0.025f).roundToInt().coerceIn(0, sh - 2)
        val fy1 = (sh * 0.975f).roundToInt().coerceIn(fy0 + 1, sh)

        val bx0 = (sw * 0.585f).roundToInt().coerceIn(0, sw - 2)
        val bx1 = (sw * 0.895f).roundToInt().coerceIn(bx0 + 1, sw)

        val frontBmp = Bitmap.createBitmap(sheet, fx0, fy0, fx1 - fx0, fy1 - fy0).asImageBitmap()
        val backBmp = Bitmap.createBitmap(sheet, bx0, fy0, bx1 - bx0, fy1 - fy0).asImageBitmap()

        cache.put("FRONT", frontBmp)
        cache.put("BACK", backBmp)
        return frontBmp to backBmp
    }
}

private data class MuscleGlowSpot(
    val nx: Float,
    val ny: Float,
    val radiusFraction: Float,
    val color: Color
)

/**
 * Studio-Illustrated Sculpted Front & Back Human Muscle Map Card for Workout Overview & Progress.
 * Uses real anatomical front/back kinesiology artwork with soft muscle-zone highlights—zero geometric polygons.
 */
@Composable
fun AnatomicalMuscleMapCard(
    primaryMuscles: List<String>,
    secondaryMuscles: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val maps = remember(context) { MuscleMapBitmapCache.getFrontAndBack(context) }

    val normalizedPrimary = primaryMuscles.joinToString(" ").lowercase()
    val normalizedSecondary = secondaryMuscles.joinToString(" ").lowercase()

    val primaryGlow = Color(0xFFEF4444)
    val supportGlow = TimerAmber

    fun matchTone(vararg keywords: String): Color? {
        return when {
            keywords.any { normalizedPrimary.contains(it) } -> primaryGlow
            keywords.any { normalizedSecondary.contains(it) } -> supportGlow
            else -> null
        }
    }

    val frontSpots = buildList {
        matchTone("chest", "pec", "push", "upper")?.let { c ->
            add(MuscleGlowSpot(0.39f, 0.25f, 0.16f, c))
            add(MuscleGlowSpot(0.61f, 0.25f, 0.16f, c))
        }
        matchTone("shoulder", "delt", "lateral", "press")?.let { c ->
            add(MuscleGlowSpot(0.24f, 0.22f, 0.13f, c))
            add(MuscleGlowSpot(0.76f, 0.22f, 0.13f, c))
        }
        matchTone("bicep", "curl", "arm", "forearm")?.let { c ->
            add(MuscleGlowSpot(0.19f, 0.33f, 0.13f, c))
            add(MuscleGlowSpot(0.81f, 0.33f, 0.13f, c))
        }
        matchTone("abs", "core", "plank", "stomach", "midsection", "waist")?.let { c ->
            add(MuscleGlowSpot(0.50f, 0.36f, 0.17f, c))
        }
        matchTone("quad", "leg", "squat", "lunge", "lower")?.let { c ->
            add(MuscleGlowSpot(0.37f, 0.61f, 0.17f, c))
            add(MuscleGlowSpot(0.63f, 0.61f, 0.17f, c))
        }
    }

    val backSpots = buildList {
        matchTone("upper back", "mid-back", "trap", "rear", "row")?.let { c ->
            add(MuscleGlowSpot(0.50f, 0.23f, 0.18f, c))
        }
        matchTone("lat", "back", "pull", "pulldown")?.let { c ->
            add(MuscleGlowSpot(0.37f, 0.33f, 0.16f, c))
            add(MuscleGlowSpot(0.63f, 0.33f, 0.16f, c))
        }
        matchTone("tricep", "press", "push", "dip")?.let { c ->
            add(MuscleGlowSpot(0.20f, 0.32f, 0.13f, c))
            add(MuscleGlowSpot(0.80f, 0.32f, 0.13f, c))
        }
        matchTone("glute", "hamstring", "hinge", "leg", "lower", "calves")?.let { c ->
            add(MuscleGlowSpot(0.38f, 0.61f, 0.17f, c))
            add(MuscleGlowSpot(0.62f, 0.61f, 0.17f, c))
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, CardBorderLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MUSCLES WORKED",
                    fontSize = 11.sp,
                    color = RoyalBlue,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LegendDot(color = primaryGlow, label = "Main")
                    LegendDot(color = supportGlow, label = "Support")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftSurfaceTint, RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FRONT ANATOMICAL FIGURE
                AnatomicalFigureFrame(
                    label = "FRONT",
                    bitmap = maps?.first,
                    spots = frontSpots
                )

                // CLEAN SUMMARY COLUMN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Main Focus",
                        fontSize = 10.sp,
                        color = TextSecondarySlate,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = primaryMuscles.distinct().take(3).joinToString(" • ").ifBlank { "Upper Body & Core" },
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        color = TextPrimarySlate,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (secondaryMuscles.isNotEmpty()) {
                        Text(
                            text = "Support: ${secondaryMuscles.distinct().take(2).joinToString(", ")}",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // BACK ANATOMICAL FIGURE
                AnatomicalFigureFrame(
                    label = "BACK",
                    bitmap = maps?.second,
                    spots = backSpots
                )
            }
        }
    }
}

@Composable
private fun AnatomicalFigureFrame(
    label: String,
    bitmap: ImageBitmap?,
    spots: List<MuscleGlowSpot>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(88.dp)
                .height(144.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, CardBorderLight, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "$label muscle anatomy",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 3.dp, horizontal = 2.dp)
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    spots.forEach { spot ->
                        val center = Offset(size.width * spot.nx, size.height * spot.ny)
                        val r = size.width * spot.radiusFraction * 1.45f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    spot.color.copy(alpha = 0.52f),
                                    spot.color.copy(alpha = 0.20f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = r
                            ),
                            radius = r,
                            center = center,
                            blendMode = BlendMode.Multiply
                        )
                    }
                }
            }
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondarySlate,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondarySlate,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1
        )
    }
}
