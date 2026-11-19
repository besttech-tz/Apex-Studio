package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.FoodCombinationBlueprint
import com.example.domain.FoodEncyclopediaEntry
import com.example.domain.FoodGuideGroup
import com.example.domain.FoodIntelligenceCatalog
import com.example.domain.IntakeManagementRule
import com.example.system.TacticalAudioHaptics
import com.example.ui.ApexUiState
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.FramedStatCapsule
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.TextSecondarySlate
import kotlin.math.max
import kotlin.math.roundToInt

private val FoodInk = Color(0xFF0F172A)
private val FoodHairline = CardBorderLight
private val FoodDivider = SubtleDivider

private enum class FoodHubSection(val label: String) {
    ALL_FOODS("Foods"),
    FAT_MASTERY("Fat Balance"),
    COMBINATIONS("Meal Plates")
}

private object FoodPhotoSliceCache {
    private val sheetCache = LruCache<Int, Bitmap>(4)
    private val cropCache = LruCache<String, ImageBitmap>(24)

    fun resolveDrawableRes(gridIndex: Int): Int = when (gridIndex) {
        0 -> R.drawable.img_food_proteins_grid_1794508267188
        1 -> R.drawable.img_food_fats_carbs_grid_1794508267872
        else -> R.drawable.img_food_combos_grid_1794508268712
    }

    fun getQuadrantBitmap(
        context: android.content.Context,
        gridIndex: Int,
        quadrant: Int
    ): ImageBitmap? {
        val safeQuadrant = quadrant.coerceIn(0, 3)
        val cacheKey = "${gridIndex}_$safeQuadrant"
        cropCache.get(cacheKey)?.let { return it }

        val resId = resolveDrawableRes(gridIndex)
        var sheet = sheetCache.get(resId)
        if (sheet == null) {
            val opts = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            sheet = BitmapFactory.decodeResource(context.resources, resId, opts)
            if (sheet != null) {
                sheetCache.put(resId, sheet)
            }
        }
        val validSheet = sheet ?: return null

        val col = safeQuadrant % 2
        val row = safeQuadrant / 2
        val startFracX = if (col == 0) 0.03f else 0.53f
        val endFracX = if (col == 0) 0.47f else 0.97f
        val startFracY = if (row == 0) 0.03f else 0.53f
        val endFracY = if (row == 0) 0.47f else 0.97f

        val cropX = (validSheet.width * startFracX).roundToInt().coerceIn(0, validSheet.width - 10)
        val cropY = (validSheet.height * startFracY).roundToInt().coerceIn(0, validSheet.height - 10)
        val cropW = (validSheet.width * (endFracX - startFracX)).roundToInt()
            .coerceIn(10, validSheet.width - cropX)
        val cropH = (validSheet.height * (endFracY - startFracY)).roundToInt()
            .coerceIn(10, validSheet.height - cropY)

        val cropped = Bitmap.createBitmap(validSheet, cropX, cropY, cropW, cropH).asImageBitmap()
        cropCache.put(cacheKey, cropped)
        return cropped
    }
}

@Composable
private fun FoodPhotoThumbnail(
    gridIndex: Int,
    quadrant: Int,
    contentDescription: String,
    size: Dp = 70.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageBitmap = remember(gridIndex, quadrant) {
        FoodPhotoSliceCache.getQuadrantBitmap(context, gridIndex, quadrant)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(SoftSurfaceTint),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * CLEAN EDITORIAL NUTRITION & FOOD GUIDE
 * Pure white sheets, studio food photography, crisp monochrome typography, zero neon macro boxes.
 */
@Composable
fun FoodIntelligenceScreen(
    uiState: ApexUiState,
    onAddFoodItemProtein: (String, Int) -> Unit,
    onAddWaterMl: (Int) -> Unit = {},
    onResetWater: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val report = uiState.gapReport
    val profile = uiState.profile

    var activeSection by rememberSaveable { mutableStateOf(FoodHubSection.ALL_FOODS) }
    var selectedGroup by rememberSaveable { mutableStateOf(FoodGuideGroup.ALL) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFatScenarioIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastLoggedToast by remember { mutableStateOf<String?>(null) }

    val dailyKcal = report.caloricDeficitCeilingKcal
    val proteinGrams = report.dailyProteinFloorGrams
    val healthyFatFloorGrams = (report.currentWeightKg * 0.85f).roundToInt().coerceIn(48, 95)
    val proteinKcal = proteinGrams * 4
    val fatKcal = healthyFatFloorGrams * 9
    val smartCarbGrams = (max(240, dailyKcal - proteinKcal - fatKcal) / 4f).roundToInt()
    val waterGoalMl = 3000

    FitnessAtmosphericContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .testTag("screen_food_intelligence"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Studio Nutrition & Fuel Cover Banner (Dark Scrimmed High-Contrast Framing)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FoodInk,
                border = BorderStroke(1.dp, FoodHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x140F172A))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_nutrition_fuel_banner_1794761668466),
                        contentDescription = "Food Guide & Fuel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xEE090D16),
                                        Color(0xCC0F172A),
                                        Color(0x660F172A)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "WHOLE-FOOD ATHLETIC FUEL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.6.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Food Guide",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Daily targets, whole foods, hydration & meal plates",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // 2. Crisp Framed Daily Nutrition & Hydration Sheet
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, FoodHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
                    .testTag("card_personal_macro_balance")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Nutrition Target",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FoodInk
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InnerFrameSurface,
                            border = BorderStroke(1.dp, InnerFrameBorder)
                        ) {
                            Text(
                                text = "$dailyKcal kcal / day",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FoodInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FramedStatCapsule(
                            label = "PROTEIN",
                            value = "${proteinGrams}g",
                            subTag = "${profile.consumedProteinGrams}g logged",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "HEALTHY FAT",
                            value = "${healthyFatFloorGrams}g",
                            subTag = "daily floor",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "CARBS",
                            value = "${smartCarbGrams}g",
                            subTag = "training fuel",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Framed Water Hydration Bar inside Nutrition Sheet
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = InnerFrameSurface,
                        border = BorderStroke(1.dp, InnerFrameBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalDrink,
                                    contentDescription = null,
                                    tint = FoodInk,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "Daily Water",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FoodInk
                                    )
                                    Text(
                                        text = "${profile.waterMilliliters} / $waterGoalMl ml",
                                        fontSize = 11.sp,
                                        color = TextSecondarySlate
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = { onAddWaterMl(250) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = PureWhiteCard,
                                    border = BorderStroke(1.dp, InnerFrameBorder),
                                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "+250ml",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = FoodInk
                                        )
                                    }
                                }
                                Surface(
                                    onClick = { onAddWaterMl(500) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = FoodInk,
                                    modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "+500ml",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                                if (profile.waterMilliliters > 0) {
                                    Surface(
                                        onClick = onResetWater,
                                        shape = RoundedCornerShape(8.dp),
                                        color = PureWhiteCard,
                                        border = BorderStroke(1.dp, InnerFrameBorder),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset Water",
                                                tint = TextSecondarySlate,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "Aim for ${healthyFatFloorGrams}g whole-food fats daily (eggs, olive oil, avocado, nuts).",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = TextSecondarySlate
                    )
                }
            }

            // 3. Clean Framed 3-Tab Segmented Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE5ECF4),
                border = BorderStroke(1.dp, InnerFrameBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FoodHubSection.entries.forEach { section ->
                    val isSelected = activeSection == section
                    Surface(
                        onClick = { activeSection = section },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isSelected) PureWhiteCard else Color.Transparent,
                        shadowElevation = if (isSelected) 1.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 40.dp)
                            .testTag("tab_food_${section.name.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = section.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) FoodInk else TextSecondarySlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        if (lastLoggedToast != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PureWhiteCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(12.dp), spotColor = Color(0x0A0F172A))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = FoodInk,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = lastLoggedToast.orEmpty(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FoodInk
                    )
                }
            }
        }

        when (activeSection) {
            FoodHubSection.ALL_FOODS -> {
                AllFoodsEncyclopediaSection(
                    selectedGroup = selectedGroup,
                    onSelectGroup = { selectedGroup = it },
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onLogProtein = { item ->
                        if (item.proteinGrams > 0) {
                            onAddFoodItemProtein(item.name, item.proteinGrams)
                            TacticalAudioHaptics.triggerClickPulse(context)
                            lastLoggedToast = "Added +${item.proteinGrams}g protein (${item.name})"
                        }
                    }
                )
            }

            FoodHubSection.FAT_MASTERY -> {
                DietaryFatMasteryAndSimulatorSection(
                    healthyFatFloorGrams = healthyFatFloorGrams,
                    proteinFloorGrams = proteinGrams,
                    smartCarbBudgetGrams = smartCarbGrams,
                    selectedScenarioIndex = selectedFatScenarioIndex,
                    onSelectScenario = { selectedFatScenarioIndex = it }
                )
            }

            FoodHubSection.COMBINATIONS -> {
                FoodCombinationsSection()
            }
        }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CleanMacroColumn(
    label: String,
    gramsText: String,
    subText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondarySlate,
            letterSpacing = 0.5.sp
        )
        Text(
            text = gramsText,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = FoodInk
        )
        Text(
            text = subText,
            fontSize = 11.sp,
            color = TextSecondarySlate,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AllFoodsEncyclopediaSection(
    selectedGroup: FoodGuideGroup,
    onSelectGroup: (FoodGuideGroup) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onLogProtein: (FoodEncyclopediaEntry) -> Unit
) {
    val chipScroll = rememberScrollState()
    var expandedCardId by rememberSaveable { mutableStateOf<String?>(null) }

    val filteredEntries = remember(selectedGroup, searchQuery) {
        FoodIntelligenceCatalog.FOOD_ENCYCLOPEDIA.filter { entry ->
            val groupMatch = selectedGroup == FoodGuideGroup.ALL || entry.group == selectedGroup
            val queryMatch = searchQuery.isBlank() ||
                entry.name.contains(searchQuery, ignoreCase = true) ||
                entry.biologicalBenefit.contains(searchQuery, ignoreCase = true) ||
                entry.bestFoodCombination.contains(searchQuery, ignoreCase = true)
            groupMatch && queryMatch
        }
    }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
            Text(
                text = "Search chicken, eggs, avocado, rice, oats...",
                fontSize = 13.sp,
                color = TextSecondarySlate
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FoodInk,
            unfocusedBorderColor = CardBorderLight,
            focusedContainerColor = PureWhiteCard,
            unfocusedContainerColor = PureWhiteCard
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("input_food_search")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(chipScroll),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FoodGuideGroup.entries.forEach { group ->
            val active = selectedGroup == group
            Surface(
                onClick = { onSelectGroup(group) },
                shape = RoundedCornerShape(999.dp),
                color = if (active) FoodInk else PureWhiteCard,
                border = if (active) null else BorderStroke(1.dp, FoodHairline),
                modifier = Modifier.defaultMinSize(minHeight = 38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = group.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active) Color.White else FoodInk
                    )
                }
            }
        }
    }

    // Single Unified Framed White Sheet of Foods
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, FoodHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            filteredEntries.forEachIndexed { idx, item ->
                if (idx > 0) {
                    HorizontalDivider(color = FoodDivider)
                }
                val isExpanded = expandedCardId == item.id
                FoodEncyclopediaRow(
                    entry = item,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedCardId = if (isExpanded) null else item.id
                    },
                    onLogProtein = { onLogProtein(item) }
                )
            }
        }
    }
}

@Composable
private fun FoodEncyclopediaRow(
    entry: FoodEncyclopediaEntry,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onLogProtein: () -> Unit
) {
    val isTrap = entry.group == FoodGuideGroup.CAUTION_TRAPS

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .padding(14.dp)
            .testTag("card_food_${entry.id}"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FoodPhotoThumbnail(
                gridIndex = entry.photoGridIndex,
                quadrant = entry.photoQuadrant,
                contentDescription = entry.name,
                size = 68.dp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = entry.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FoodInk,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = entry.servingLabel,
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                )

                Text(
                    text = "${entry.proteinGrams}g P  •  ${entry.carbsGrams}g C  •  ${entry.fatGrams}g F  •  ${entry.caloriesKcal} kcal",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FoodInk.copy(alpha = 0.78f)
                )
            }

            if (!isTrap && entry.proteinGrams >= 5) {
                Surface(
                    onClick = onLogProtein,
                    shape = RoundedCornerShape(10.dp),
                    color = SoftSurfaceTint,
                    modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Protein",
                            tint = FoodInk,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${entry.proteinGrams}g",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FoodInk
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextSecondarySlate,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftSurfaceTint, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = entry.biologicalBenefit,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = FoodInk,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${if (isTrap) "Better choice" else "Preparation"}: ${entry.howToEatAndPrepare}",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextSecondarySlate
                )
                Text(
                    text = "Pair with: ${entry.bestFoodCombination}",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FoodInk
                )
            }
        }
    }
}

@Composable
private fun DietaryFatMasteryAndSimulatorSection(
    healthyFatFloorGrams: Int,
    proteinFloorGrams: Int,
    smartCarbBudgetGrams: Int,
    selectedScenarioIndex: Int,
    onSelectScenario: (Int) -> Unit
) {
    val scenarios = FoodIntelligenceCatalog.HIGH_FAT_MANAGEMENT_SCENARIOS
    val activeScenario = scenarios[selectedScenarioIndex.coerceIn(0, scenarios.lastIndex)]
    val adjustedCarbTarget = (smartCarbBudgetGrams - activeScenario.carbGramsToTrim).coerceAtLeast(50)

    // 1. Clean Whole-Food Fat Overview Sheet
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, FoodHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FoodPhotoThumbnail(
                    gridIndex = 1,
                    quadrant = 0,
                    contentDescription = "Avocado, Olive Oil and Nuts",
                    size = 64.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Healthy Fat Floor (${healthyFatFloorGrams}g / day)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodInk
                    )
                    Text(
                        text = "Dietary fat does not cause fat gain on its own—excess total calories do.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondarySlate
                    )
                }
            }

            HorizontalDivider(color = FoodDivider)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "PRIORITIZE DAILY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodInk,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Whole eggs, olive oil, avocado, salmon, almonds & walnuts.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondarySlate
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(52.dp)
                        .background(FoodDivider)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "KEEP MINIMAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodInk,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Deep-fried fast food, pastries, and heavy unmeasured sauces.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondarySlate
                    )
                }
            }
        }
    }

    // 2. Clean Same-Day Balance Adjuster
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, FoodHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            .testTag("card_fat_intake_simulator")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Ate a Richer Meal Today?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FoodInk
                )
                Text(
                    text = "Select the closest meal to adjust the rest of your day cleanly:",
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scenarios.forEachIndexed { idx, sc ->
                    val selected = idx == selectedScenarioIndex
                    Surface(
                        onClick = { onSelectScenario(idx) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) FoodInk else InnerFrameSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodPhotoThumbnail(
                                gridIndex = sc.photoGridIndex,
                                quadrant = sc.photoQuadrant,
                                contentDescription = sc.title,
                                size = 46.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sc.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else FoodInk
                                )
                                Text(
                                    text = sc.exampleFoods,
                                    fontSize = 11.sp,
                                    color = if (selected) Color.White.copy(alpha = 0.82f) else TextSecondarySlate
                                )
                            }
                            Text(
                                text = "+${sc.extraFatGrams}g fat",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else FoodInk
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SoftSurfaceTint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CleanMacroColumn(
                            label = "EXTRA KCAL",
                            gramsText = "+${activeScenario.extraCaloriesKcal}",
                            subText = "${activeScenario.extraFatGrams}g fat",
                            modifier = Modifier.weight(1f)
                        )
                        CleanMacroColumn(
                            label = "TRIM CARBS",
                            gramsText = "-${activeScenario.carbGramsToTrim}g",
                            subText = "${adjustedCarbTarget}g left",
                            modifier = Modifier.weight(1f)
                        )
                        CleanMacroColumn(
                            label = "PROTEIN",
                            gramsText = "${proteinFloorGrams}g",
                            subText = "unchanged",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = activeScenario.immediateActionStep,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = FoodInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Next meal: ${activeScenario.nextMealPrescription}",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = RoyalBlue,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // 3. Clean Unified Intake Principles Sheet
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, FoodHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            FoodIntelligenceCatalog.INTAKE_MANAGEMENT_RULES.forEachIndexed { idx, rule ->
                if (idx > 0) {
                    HorizontalDivider(color = FoodDivider)
                }
                IntakeRuleRow(index = idx + 1, rule = rule)
            }
        }
    }
}

@Composable
private fun IntakeRuleRow(index: Int, rule: IntakeManagementRule) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "$index. ${rule.title}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FoodInk
        )
        Text(
            text = rule.coreRule,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = FoodInk.copy(alpha = 0.85f)
        )
        Text(
            text = rule.practicalHowTo,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = TextSecondarySlate,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FoodCombinationsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FoodIntelligenceCatalog.FOOD_COMBINATIONS.forEach { combo ->
            FoodCombinationCard(combo)
        }
    }
}

@Composable
private fun FoodCombinationCard(combo: FoodCombinationBlueprint) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, FoodHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FoodPhotoThumbnail(
                    gridIndex = combo.photoGridIndex,
                    quadrant = combo.photoQuadrant,
                    contentDescription = combo.title,
                    size = 74.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = combo.timingBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondarySlate,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = combo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FoodInk
                    )
                    Text(
                        text = combo.formula,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FoodInk.copy(alpha = 0.78f)
                    )
                }
            }

            HorizontalDivider(color = FoodDivider)

            Text(
                text = combo.examplePlate,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = FoodInk
            )

            Text(
                text = combo.whyItWorksBiologically,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = TextSecondarySlate,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
