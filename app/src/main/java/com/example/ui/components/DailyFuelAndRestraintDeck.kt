package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalibrationProfileState
import com.example.domain.CalibrationGapReport
import com.example.domain.FuelAndRestraintCatalog
import com.example.domain.MetabolicDirection
import com.example.domain.ProteinFoodCategory
import com.example.domain.SupplementPreference
import com.example.system.ApexNotificationScheduler
import com.example.system.OperationalCheckpoint
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.NominalGreen
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSoft
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.TextPrimarySlate
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.TimerAmber
import kotlin.math.abs
import kotlin.math.roundToInt

private val DangerRed = Color(0xFFDC2626)
private val DangerRedSoft = Color(0xFFFEF2F2)
private val DangerRedBorder = Color(0xFFFECACA)
private val EmeraldSoft = Color(0xFFECFDF5)

/**
 * 1. LIVE MIRROR-TO-TARGET BODY DIRECTION & MOTIVATOR BANNER
 * Shows Current Silhouette -> Target Silhouette, Weight Loss vs Weight Gain math, and straight-truth coach voice.
 */
@Composable
fun TransformationMissionCoachCard(
    profile: CalibrationProfileState,
    report: CalibrationGapReport,
    onRecalibrateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val direction = report.metabolicDirection
    val deepInk = Color(0xFF0F172A)
    val hairlineSubtle = Color(0xFFEEF2F6)

    val weightTransitionText = when (direction) {
        MetabolicDirection.GAIN_LEAN_MASS ->
            "${report.currentWeightKg.roundToInt()} kg → ${report.targetBodyweightKg.roundToInt()} kg (+${String.format("%.1f", abs(report.weightDeltaKg))} kg lean mass)"
        MetabolicDirection.BODY_RECOMP ->
            "${report.currentWeightKg.roundToInt()} kg → ${report.targetBodyweightKg.roundToInt()} kg (Recomposition)"
        MetabolicDirection.CUT_FAT ->
            "${report.currentWeightKg.roundToInt()} kg → ${report.targetBodyweightKg.roundToInt()} kg (-${String.format("%.1f", report.fatMassToShedKg)} kg fat)"
    }

    val (_, coachMessage) = remember(
        direction,
        profile.consumedProteinGrams,
        report.dailyProteinFloorGrams,
        profile.cleanRestraintLockedCount,
        profile.cravingsDefeatedToday
    ) {
        FuelAndRestraintCatalog.buildLiveCoachMotivation(
            direction = direction,
            consumedProtein = profile.consumedProteinGrams,
            targetProtein = report.dailyProteinFloorGrams,
            cleanRulesLockedCount = profile.cleanRestraintLockedCount,
            cravingsDefeated = profile.cravingsDefeatedToday
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            .testTag("card_transformation_mission_coach"),
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current vs Target Physique",
                    fontSize = 15.sp,
                    color = deepInk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Edit Goal",
                    fontSize = 12.sp,
                    color = RoyalBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onRecalibrateClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            TransformationSilhouetteComparisonBanner(
                currentBracket = report.currentBracket,
                goalBracket = report.goalBracket,
                sex = profile.biologicalSex,
                startPresetOverride = PhysiqueSilhouettePreset.fromCardId(profile.selectedStartingShapeCardId),
                goalPresetOverride = PhysiqueSilhouettePreset.fromCardId(profile.selectedGoalCardId),
                startTitleOverride = "${report.currentWeightKg.roundToInt()} kg Now",
                goalTitleOverride = "${report.targetBodyweightKg.roundToInt()} kg Goal",
                compactMode = true
            )

            HorizontalDivider(color = hairlineSubtle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "WEIGHT TRAJECTORY",
                        fontSize = 10.sp,
                        color = TextSecondarySlate,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = weightTransitionText,
                        fontSize = 12.sp,
                        color = deepInk,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(hairlineSubtle)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "DAILY TARGET",
                        fontSize = 10.sp,
                        color = TextSecondarySlate,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${report.caloricDeficitCeilingKcal} kcal • ${report.dailyProteinFloorGrams}g P",
                        fontSize = 12.sp,
                        color = deepInk,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = hairlineSubtle)

            Text(
                text = coachMessage,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextSecondarySlate
            )
        }
    }
}

/**
 * 2. DAILY PROTEIN FLOOR (e.g. 200g), 4-MEAL PROPOSITION & REAL FOOD / SUPPLEMENT TABLE
 */
@Composable
fun DailyProteinAndMealStationCard(
    profile: CalibrationProfileState,
    report: CalibrationGapReport,
    onSelectSupplementPreference: (SupplementPreference) -> Unit,
    onToggleCreatine5g: () -> Unit,
    onLogMealProtein: (String, Int) -> Unit,
    onAddFoodItemProtein: (String, Int) -> Unit,
    onAddWater: () -> Unit,
    onResetNutrition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetProteinGrams = report.dailyProteinFloorGrams
    val consumedProteinGrams = profile.consumedProteinGrams
    val proteinProgress = (consumedProteinGrams.toFloat() / targetProteinGrams.coerceAtLeast(1).toFloat())
        .coerceIn(0f, 1f)
    val targetWaterMl = if (report.metabolicDirection == MetabolicDirection.GAIN_LEAN_MASS) 3200 else 3000
    val waterProgress = (profile.waterMilliliters.toFloat() / targetWaterMl.toFloat()).coerceIn(0f, 1f)

    val mealProposition = remember(
        targetProteinGrams,
        report.caloricDeficitCeilingKcal,
        report.metabolicDirection,
        profile.supplementPreference
    ) {
        FuelAndRestraintCatalog.buildDailyMealProposition(
            targetProteinGrams = targetProteinGrams,
            targetCaloriesKcal = report.caloricDeficitCeilingKcal,
            direction = report.metabolicDirection,
            supplementPref = profile.supplementPreference
        )
    }

    var showFoodTable by rememberSaveable { mutableStateOf(true) }
    var selectedFoodCategory by rememberSaveable { mutableStateOf(ProteinFoodCategory.ALL) }

    val filteredFoods = remember(selectedFoodCategory) {
        if (selectedFoodCategory == ProteinFoodCategory.ALL) {
            FuelAndRestraintCatalog.PROTEIN_FOOD_TABLE
        } else {
            FuelAndRestraintCatalog.PROTEIN_FOOD_TABLE.filter { it.category == selectedFoodCategory }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x120F172A))
            .testTag("daily_nutrition_anchor_card"),
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, CardBorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "DAILY PROTEIN PROPOSITION & FOOD TABLE",
                        fontSize = 11.sp,
                        color = TextPrimarySlate,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    onClick = onResetNutrition,
                    shape = RoundedCornerShape(6.dp),
                    color = SoftSurfaceTint,
                    border = BorderStroke(1.dp, CardBorderLight)
                ) {
                    Text(
                        text = "RESET DAY",
                        fontSize = 10.sp,
                        color = TextSecondarySlate,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Protein Floor Progress Bar (Uncapped — supports 180g, 200g, 220g+)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Calibrated Daily Protein Floor",
                            fontSize = 13.sp,
                            color = TextPrimarySlate,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Budget: ${report.caloricDeficitCeilingKcal} kcal/day • ${(targetProteinGrams - consumedProteinGrams).coerceAtLeast(0)}g left today",
                            fontSize = 11.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "$consumedProteinGrams / ${targetProteinGrams}g",
                        fontSize = 19.sp,
                        color = if (consumedProteinGrams >= targetProteinGrams) NominalGreen else RoyalBlue,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                LinearProgressIndicator(
                    progress = { proteinProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (consumedProteinGrams >= targetProteinGrams) NominalGreen else RoyalBlue,
                    trackColor = SoftSurfaceTint
                )
            }

            // Supplement Mode Selector + Creatine 5g Lock
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SupplementPreference.entries.forEach { pref ->
                        val isSelected = profile.supplementPreference == pref
                        Surface(
                            onClick = { onSelectSupplementPreference(pref) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) RoyalBlue else SoftSurfaceTint,
                            border = BorderStroke(1.dp, if (isSelected) RoyalBlue else CardBorderLight),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 40.dp)
                                .testTag("supp_mode_${pref.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = pref.label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else TextPrimarySlate,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                if (profile.supplementPreference == SupplementPreference.WITH_SUPPLEMENTS) {
                    Surface(
                        onClick = onToggleCreatine5g,
                        shape = RoundedCornerShape(10.dp),
                        color = if (profile.creatine5gChecked) EmeraldSoft else SoftSurfaceTint,
                        border = BorderStroke(
                            1.dp,
                            if (profile.creatine5gChecked) NominalGreen else CardBorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_toggle_creatine_5g")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Creatine Monohydrate (5g in water — ATP & muscle fullness)",
                                fontSize = 11.sp,
                                color = TextPrimarySlate,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (profile.creatine5gChecked) "✓ LOGGED" else "TAP TO LOG",
                                fontSize = 11.sp,
                                color = if (profile.creatine5gChecked) NominalGreen else RoyalBlue,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // TODAY'S 4-MEAL PROPOSITION BLUEPRINT (Auto-scaled to hit exact targetProteinGrams)
            Text(
                text = "TODAY'S ${targetProteinGrams}G MEAL PROPOSITION (TAP ANCHOR AS YOU EAT)",
                fontSize = 10.sp,
                color = RoyalBlue,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                mealProposition.forEach { slot ->
                    val isLogged = when (slot.slotKey) {
                        "breakfast" -> profile.breakfastProteinGrams > 0
                        "lunch" -> profile.lunchProteinGrams > 0
                        "snack" -> profile.snackProteinGrams > 0
                        else -> profile.dinnerProteinGrams > 0
                    }

                    Surface(
                        onClick = { onLogMealProtein(slot.slotKey, slot.proteinGrams) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isLogged) EmeraldSoft else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            width = if (isLogged) 1.5.dp else 1.dp,
                            color = if (isLogged) NominalGreen else CardBorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_log_meal_${slot.slotKey}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${slot.timeWindow} • ${slot.title}",
                                    fontSize = 10.sp,
                                    color = if (isLogged) NominalGreen else RoyalBlue,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = slot.menuDescription,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = TextPrimarySlate,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "~${slot.approxCalories} kcal",
                                    fontSize = 10.sp,
                                    color = TextSecondarySlate
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isLogged) NominalGreen else RoyalBlue
                            ) {
                                Text(
                                    text = if (isLogged) "✓ ${slot.proteinGrams}g" else "+${slot.proteinGrams}g",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = CardBorderLight)

            // INTERACTIVE PROTEIN FOOD & SUPPLEMENT REFERENCE TABLE
            Surface(
                onClick = { showFoodTable = !showFoodTable },
                shape = RoundedCornerShape(10.dp),
                color = RoyalBlueSoft,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_toggle_protein_food_table")
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "PROTEIN FOOD & SUPPLEMENT TABLE (GRAMS PER FOOD)",
                            fontSize = 11.sp,
                            color = RoyalBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Icon(
                        imageVector = if (showFoodTable) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Food Table",
                        tint = RoyalBlue
                    )
                }
            }

            AnimatedVisibility(visible = showFoodTable) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Category Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProteinFoodCategory.entries.forEach { cat ->
                            val active = selectedFoodCategory == cat
                            Surface(
                                onClick = { selectedFoodCategory = cat },
                                shape = RoundedCornerShape(999.dp),
                                color = if (active) RoyalBlue else SoftSurfaceTint,
                                border = BorderStroke(1.dp, if (active) RoyalBlue else CardBorderLight)
                            ) {
                                Text(
                                    text = cat.label,
                                    fontSize = 11.sp,
                                    color = if (active) Color.White else TextPrimarySlate,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Table Column Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftSurfaceTint, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FOOD / SUPPLEMENT & PORTION",
                            fontSize = 10.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "PROTEIN • TAP TO ADD",
                            fontSize = 10.sp,
                            color = RoyalBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Food Rows
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        filteredFoods.forEach { food ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PureWhiteCard,
                                border = BorderStroke(1.dp, CardBorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = food.name,
                                                fontSize = 13.sp,
                                                color = TextPrimarySlate,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SoftSurfaceTint
                                            ) {
                                                Text(
                                                    text = food.qualityBadge,
                                                    fontSize = 9.sp,
                                                    color = TextSecondarySlate,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${food.portionDescription} • ${food.caloriesKcal} kcal",
                                            fontSize = 11.sp,
                                            color = TextSecondarySlate
                                        )
                                    }

                                    Surface(
                                        onClick = { onAddFoodItemProtein(food.name, food.proteinGrams) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = RoyalBlueSoft,
                                        border = BorderStroke(1.dp, RoyalBlue),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 36.dp)
                                            .testTag("btn_add_food_${food.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add ${food.name}",
                                                tint = RoyalBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "${food.proteinGrams}g P",
                                                fontSize = 12.sp,
                                                color = RoyalBlue,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = CardBorderLight)

            // Hydration Floor
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Daily Cold Water Floor (Kills Fake Hunger)",
                            fontSize = 12.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${profile.waterMilliliters} / ${targetWaterMl}ml",
                        fontSize = 14.sp,
                        color = Color(0xFF0284C7),
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                LinearProgressIndicator(
                    progress = { waterProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF0284C7),
                    trackColor = SoftSurfaceTint
                )

                Surface(
                    onClick = onAddWater,
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0F2FE),
                    border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_add_water_500ml")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOG +500 ML WATER",
                            fontSize = 12.sp,
                            color = Color(0xFF0369A1),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. ANTI-JUNK RESTRAINT SHIELD & CRAVING INTERCEPTOR
 * Restrains the user from candy, sugary drinks, biscuits, and fried fast food with mathematical reality + high-protein swaps.
 */
@Composable
fun AntiJunkRestraintShieldCard(
    profile: CalibrationProfileState,
    zeroSugarLocked: Boolean,
    kitchenClosedLocked: Boolean,
    onToggleZeroCandy: () -> Unit,
    onToggleZeroLiquidSugar: () -> Unit,
    onToggleZeroFriedJunk: () -> Unit,
    onToggleKitchenClosed8pm: () -> Unit,
    onDefeatCraving: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedJunkId by rememberSaveable { mutableStateOf(FuelAndRestraintCatalog.JUNK_SABOTAGE_ITEMS.first().id) }
    val activeJunk = remember(selectedJunkId) {
        FuelAndRestraintCatalog.JUNK_SABOTAGE_ITEMS.firstOrNull { it.id == selectedJunkId }
            ?: FuelAndRestraintCatalog.JUNK_SABOTAGE_ITEMS.first()
    }

    val lockedCount = listOf(
        profile.zeroCandySweetsChecked,
        zeroSugarLocked,
        profile.zeroFriedJunkChecked,
        kitchenClosedLocked
    ).count { it }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x120F172A))
            .testTag("card_anti_junk_restraint_shield"),
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.5.dp, if (lockedCount == 4) NominalGreen else DangerRedBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (lockedCount == 4) NominalGreen else DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "ANTI-JUNK & CANDY RESTRAINT SHIELD",
                        fontSize = 11.sp,
                        color = TextPrimarySlate,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (profile.cravingsDefeatedToday > 0) EmeraldSoft else SoftSurfaceTint
                ) {
                    Text(
                        text = "${profile.cravingsDefeatedToday} Cravings Crushed",
                        fontSize = 10.sp,
                        color = if (profile.cravingsDefeatedToday > 0) NominalGreen else TextSecondarySlate,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Candy, soda, biscuits, and fried snacks are engineered to keep you soft. Lock these 4 daily barriers:",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = TextSecondarySlate
            )

            // 2x2 Daily Restraint Locks
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RestraintCheckPill(
                        label = "Zero Candy & Sweets",
                        checked = profile.zeroCandySweetsChecked,
                        onClick = onToggleZeroCandy,
                        modifier = Modifier.weight(1f)
                    )
                    RestraintCheckPill(
                        label = "Zero Sugary Drinks",
                        checked = zeroSugarLocked,
                        onClick = onToggleZeroLiquidSugar,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RestraintCheckPill(
                        label = "Zero Fried / Fast Junk",
                        checked = profile.zeroFriedJunkChecked,
                        onClick = onToggleZeroFriedJunk,
                        modifier = Modifier.weight(1f)
                    )
                    RestraintCheckPill(
                        label = "Kitchen Locked 8:30 PM",
                        checked = kitchenClosedLocked,
                        onClick = onToggleKitchenClosed8pm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = CardBorderLight)

            // CRAVING INTERCEPTOR: "TEMPTED RIGHT NOW? TAP WHAT YOU ARE ABOUT TO EAT"
            Text(
                text = "TEMPTED BY JUNK RIGHT NOW? TAP BEFORE EATING:",
                fontSize = 10.sp,
                color = DangerRed,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FuelAndRestraintCatalog.JUNK_SABOTAGE_ITEMS.forEach { item ->
                    val selected = item.id == selectedJunkId
                    Surface(
                        onClick = { selectedJunkId = item.id },
                        shape = RoundedCornerShape(999.dp),
                        color = if (selected) DangerRed else SoftSurfaceTint,
                        border = BorderStroke(1.dp, if (selected) DangerRed else CardBorderLight)
                    ) {
                        Text(
                            text = item.name.substringBefore(" /"),
                            fontSize = 11.sp,
                            color = if (selected) Color.White else TextPrimarySlate,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Sabotage Reality Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DangerRedSoft,
                border = BorderStroke(1.dp, DangerRedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = activeJunk.name,
                                fontSize = 13.sp,
                                color = DangerRed,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${activeJunk.emptyCalories} Empty kcal",
                            fontSize = 12.sp,
                            color = DangerRed,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Text(
                        text = "DAMAGE: Wipes out ${String.format("%.1f", activeJunk.fatLossDaysWiped)} FULL DAYS of fat loss • Requires ${activeJunk.extraStepsToBurn} extra steps (${activeJunk.nonStopCardioMinutes} mins non-stop cardio) just to erase!",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = activeJunk.brutalTruth,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = TextPrimarySlate
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "DO THIS INSTEAD (CRAVING DIES IN 10 MINS):",
                                fontSize = 10.sp,
                                color = RoyalBlue,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = activeJunk.instantHighProteinSwap,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = TextPrimarySlate,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = { onDefeatCraving(activeJunk.swapProteinGrams) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_defeat_junk_craving"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NominalGreen,
                            contentColor = Color.White
                        )
                    ) {
                        val ctaText = if (activeJunk.swapProteinGrams > 0) {
                            "✓ REJECT JUNK & LOG CLEAN SWAP (+${activeJunk.swapProteinGrams}g P)"
                        } else {
                            "✓ I REJECTED THIS JUNK & DRANK WATER"
                        }
                        Text(
                            text = ctaText,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestraintCheckPill(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (checked) EmeraldSoft else SoftSurfaceTint,
        border = BorderStroke(1.dp, if (checked) NominalGreen else CardBorderLight),
        modifier = modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Check,
                contentDescription = null,
                tint = if (checked) NominalGreen else TextSecondarySlate,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = if (checked) Color(0xFF065F46) else TextPrimarySlate,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 4. ALL-DAY COACH GUARDIAN REMINDERS CARD (Native Android AlarmManager)
 */
@Composable
fun AllDayCoachGuardianCard(
    alarmsArmed: Boolean,
    lastStatusMessage: String?,
    onToggleAlarms: (Boolean) -> Unit,
    onTestCheckpoint: (OperationalCheckpoint) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0x100F172A))
            .testTag("card_coach_guardian_reminders"),
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, CardBorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = if (alarmsArmed) RoyalBlue else TextSecondarySlate,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "24/7 COACH GUARDIAN REMINDERS",
                            fontSize = 11.sp,
                            color = TextPrimarySlate,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Fires protein, workout & anti-junk alerts even when closed",
                            fontSize = 11.sp,
                            color = TextSecondarySlate
                        )
                    }
                }

                Switch(
                    checked = alarmsArmed,
                    onCheckedChange = onToggleAlarms,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PureWhiteCard,
                        checkedTrackColor = RoyalBlue,
                        uncheckedThumbColor = TextSecondarySlate,
                        uncheckedTrackColor = SoftSurfaceTint
                    )
                )
            }

            if (!lastStatusMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalBlueSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $lastStatusMessage",
                        fontSize = 11.sp,
                        color = RoyalBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ApexNotificationScheduler.DAILY_CHECKPOINTS.forEach { cp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftSurfaceTint, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${cp.timeLabel} • ${cp.title}",
                                fontSize = 11.sp,
                                color = RoyalBlue,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = cp.body,
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = TextPrimarySlate,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { onTestCheckpoint(cp) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RoyalBlue),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "PING",
                                fontSize = 10.sp,
                                color = RoyalBlue,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
