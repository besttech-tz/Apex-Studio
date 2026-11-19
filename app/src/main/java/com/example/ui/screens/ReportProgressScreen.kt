package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.R
import com.example.data.local.TrainingEnvironment
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.UnitFormatter
import com.example.ui.ApexUiState
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.FramedStatCapsule
import com.example.ui.components.WorkoutCoverResolver
import com.example.ui.i18n.LocalAppStrings
import java.time.LocalDate
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import kotlin.math.roundToInt

private val ProgInk = Color(0xFF0F172A)
private val ProgSlate = Color(0xFF475569)
private val ProgHairline = CardBorderLight
private val ProgDivider = SubtleDivider

private val WEEKLY_FEEL_OPTIONS = listOf(
    "Clothes Fit Better",
    "Felt Stronger",
    "More Energy",
    "Stayed Consistent"
)

/**
 * REORGANIZED, FRICTIONLESS PROGRESS & CONSISTENCY SCREEN:
 * - Zero clipped header, zero fake 84.8 kg / 89.8 cm waist numbers, zero mandatory tape measurements.
 * - Syncs directly with the user's real weight (or hides scale kg cleanly if the user skipped weight/height).
 * - Replaces tedious waist measurements with a 1-tap Mirror / Feel Check-In + optional scale weight.
 * - Shows honest un-sugarcoated goal timeline alongside 4-week workout consistency.
 */
@Composable
fun ReportProgressScreen(
    uiState: ApexUiState,
    onAddProteinGrams: (Int) -> Unit = {},
    onResetProteinGrams: () -> Unit = {},
    onToggleSteps10k: () -> Unit = {},
    onToggleZeroLiquidSugar: () -> Unit = {},
    onToggleZeroCandy: () -> Unit = {},
    onToggleZeroFriedJunk: () -> Unit = {},
    onToggleKitchenClosed8pm: () -> Unit = {},
    onDefeatCraving: (Int) -> Unit = {},
    onSubmitWeeklyAudit: (Float, Float) -> Unit = { _, _ -> },
    onSubmitWeeklyCheckIn: (Float, Float, String, Boolean) -> Unit = { w, wst, _, _ ->
        onSubmitWeeklyAudit(w, wst)
    },
    onSimulateStagnation: () -> Unit = {},
    onLogProgressiveDrop: () -> Unit = {},
    onOpenPlanSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val profile = uiState.profile
    val report = uiState.gapReport
    val compliance = uiState.todayCompliance
    val fourWeekTrack = uiState.fourWeekTrack
    val scrollState = rememberScrollState()

    val legacyFakeTags = remember { setOf("WEEK 01 BASELINE", "WEEK 02 AUDIT", "WEEK 03 AUDIT") }
    val cleanAudits = remember(uiState.weeklyAudits) {
        uiState.weeklyAudits.filterNot { it.auditTag in legacyFakeTags }
    }

    // Keyed to profile.bodyweightKg so it ALWAYS matches the user's actual weight (never stale 84.8 kg!)
    var draftWeightKg by rememberSaveable(profile.bodyweightKg) {
        mutableFloatStateOf(profile.bodyweightKg)
    }
    var logScaleWeight by rememberSaveable(profile.measurementsKnown) {
        mutableStateOf(profile.measurementsKnown)
    }
    var selectedFeelTag by rememberSaveable {
        mutableStateOf(WEEKLY_FEEL_OPTIONS.first())
    }
    var showOptionalWaist by rememberSaveable { mutableStateOf(false) }
    var draftWaistCm by rememberSaveable {
        mutableFloatStateOf(cleanAudits.firstOrNull()?.navelCircumferenceCm ?: 84.0f)
    }

    val completedSetsCount = uiState.completedSetKeys.size
    val totalWorkoutsDone = profile.completedWorkoutsCount
    val estSessionKcal = ProgressiveLoadEngine.estimateSessionCalories(
        intensityMode = profile.workoutIntensity,
        isHomeMode = profile.trainingEnvironment == TrainingEnvironment.HOME,
        bodyweightKg = profile.bodyweightKg
    )
    val totalEstimatedKcalBurned = totalWorkoutsDone * estSessionKcal

    FitnessAtmosphericContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .testTag("screen_progress_ledger"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Dark-Scrimmed Studio Progress Header Banner + My Plan Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ProgInk,
                border = BorderStroke(1.dp, ProgHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x140F172A))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = WorkoutCoverResolver.bannerForProgress(profile.biologicalSex)),
                        contentDescription = strings.progressTitle,
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
                                        Color(0x880F172A)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "CONSISTENCY & RESULTS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.6.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = strings.progressTitle,
                                fontSize = 24.sp,
                                lineHeight = 28.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = strings.progressSubtitle,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color.White.copy(alpha = 0.84f)
                            )
                        }

                        Surface(
                            onClick = onOpenPlanSettings,
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.94f),
                            modifier = Modifier.testTag("btn_progress_open_my_plan")
                        ) {
                            Text(
                                text = "${strings.tabMyPlan} →",
                                fontSize = 12.sp,
                                color = ProgInk,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 2. Crisp Framed Stat Capsules (Adapts cleanly whether Weight/Height are entered or skipped!)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, ProgHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profile.measurementsKnown) {
                        FramedStatCapsule(
                            label = "CURRENT",
                            value = UnitFormatter.formatWeightRounded(profile.bodyweightKg, profile.useMetricUnits),
                            subTag = "bodyweight",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "GOAL",
                            value = UnitFormatter.formatWeightRounded(report.targetBodyweightKg, profile.useMetricUnits),
                            subTag = report.honestTimeline.monthsRangeLabel,
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "WORKOUTS",
                            value = "$totalWorkoutsDone",
                            subTag = "~$totalEstimatedKcalBurned kcal",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        FramedStatCapsule(
                            label = "WORKOUTS",
                            value = "$totalWorkoutsDone",
                            subTag = "$completedSetsCount sets",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "KCAL BURNED",
                            value = "~$totalEstimatedKcalBurned",
                            subTag = "~$estSessionKcal/workout",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "GOAL TIME",
                            value = report.honestTimeline.monthsRangeLabel,
                            subTag = "honest pace",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2B. 14-Day Streak & Daily Activity Strip
            val activeHistoryDates = remember(profile.workoutHistory) {
                profile.workoutHistory.map { it.dateIso }.toSet()
            }
            val last14Dates = remember {
                val today = LocalDate.now()
                (13 downTo 0).map { offset -> today.minusDays(offset.toLong()) }
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, ProgHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                    .testTag("card_streak_activity_strip")
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
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Active Streak & 14-Day Log",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProgInk
                            )
                            Text(
                                text = if (uiState.currentStreakDays > 0) {
                                    "You're on a ${uiState.currentStreakDays}-day streak — keep the chain alive"
                                } else {
                                    "Finish a workout today to ignite your Day 1 streak"
                                },
                                fontSize = 12.sp,
                                color = ProgSlate
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (uiState.currentStreakDays > 0) Color(0xFFFFF7ED) else SoftSurfaceTint,
                            border = BorderStroke(
                                1.dp,
                                if (uiState.currentStreakDays > 0) Color(0xFFFED7AA) else ProgHairline
                            )
                        ) {
                            Text(
                                text = "🔥 ${uiState.currentStreakDays}d",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.currentStreakDays > 0) Color(0xFFC2410C) else ProgInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        last14Dates.forEachIndexed { idx, date ->
                            val iso = date.toString()
                            val trained = iso in activeHistoryDates
                            val isToday = idx == 13
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                trained -> Color(0xFF16A34A)
                                                isToday -> ProgInk.copy(alpha = 0.12f)
                                                else -> SoftSurfaceTint
                                            }
                                        )
                                        .border(
                                            width = if (isToday && !trained) 1.5.dp else 0.dp,
                                            color = if (isToday && !trained) ProgInk else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (trained) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Trained on $iso",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = date.dayOfWeek.name.first().toString(),
                                    fontSize = 9.sp,
                                    fontWeight = if (isToday || trained) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isToday || trained) ProgInk else ProgSlate
                                )
                            }
                        }
                    }
                }
            }

        // 3. 4-Week Consistency & Honest Goal Horizon Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, ProgHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                .testTag("card_progress_honest_horizon")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "4-Week Consistency Block",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProgInk
                        )
                        Text(
                            text = "Week ${fourWeekTrack.activeViewedWeek} of 4  •  ${fourWeekTrack.completedDaysInCycle}/${fourWeekTrack.totalDaysInCycle} workouts completed",
                            fontSize = 12.sp,
                            color = ProgSlate
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SoftSurfaceTint
                    ) {
                        Text(
                            text = report.honestTimeline.monthsRangeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ProgInk,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                val cycleFraction = if (fourWeekTrack.totalDaysInCycle > 0) {
                    (fourWeekTrack.completedDaysInCycle.toFloat() / fourWeekTrack.totalDaysInCycle.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }

                LinearProgressIndicator(
                    progress = { cycleFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = ProgInk,
                    trackColor = SoftSurfaceTint,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = "${report.honestTimeline.honestTruthHeadline} — First visible milestone: ${report.honestTimeline.firstVisibleMilestone}.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = ProgSlate
                )
            }
        }

        // 3B. Completed Workouts History Log
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, ProgHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                .testTag("card_workout_history_log")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed Workouts Log",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProgInk
                    )
                    Text(
                        text = "${profile.workoutHistory.size} saved",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProgSlate
                    )
                }

                if (profile.workoutHistory.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftSurfaceTint,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No completed sessions yet — finish Day 1 or any pack to record your workout receipt here.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = ProgSlate,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    profile.workoutHistory.take(8).forEachIndexed { idx, rec ->
                        if (idx > 0) {
                            HorizontalDivider(color = ProgDivider)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = rec.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProgInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${rec.dateIso}  •  ${rec.durationMinutes} min  •  ${rec.setsCompleted} sets",
                                    fontSize = 11.sp,
                                    color = ProgSlate
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SoftSurfaceTint
                            ) {
                                Text(
                                    text = "~${rec.estimatedKcal} kcal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ProgInk,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Frictionless Weekly Check-In (1-Tap Mirror/Feel + Optional Scale Weight — NO mandatory waist tape!)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, ProgHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = strings.weeklyCheckInTitle,
                        fontSize = 16.sp,
                        color = ProgInk,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "No tape measure needed — tap how your body feels this week",
                        fontSize = 12.sp,
                        color = ProgSlate
                    )
                }

                // 2x2 Grid of 1-Tap Feel / Mirror Wins
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WEEKLY_FEEL_OPTIONS.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { option ->
                                val selected = selectedFeelTag == option
                                Surface(
                                    onClick = { selectedFeelTag = option },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) ProgInk else SoftSurfaceTint,
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 42.dp)
                                        .testTag("feel_chip_${option.lowercase().replace(" ", "_")}")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else ProgInk,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = ProgDivider)

                // Optional Scale Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Include Scale Weight",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProgInk
                        )
                        Text(
                            text = if (logScaleWeight) {
                                "Updates your current weight across the app"
                            } else {
                                "Off — tracking mirror & strength wins only"
                            },
                            fontSize = 11.sp,
                            color = ProgSlate
                        )
                    }
                    Switch(
                        checked = logScaleWeight,
                        onCheckedChange = { logScaleWeight = it },
                        modifier = Modifier.testTag("switch_log_scale_weight"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhiteCard,
                            checkedTrackColor = ProgInk,
                            uncheckedThumbColor = ProgSlate,
                            uncheckedTrackColor = SoftSurfaceTint
                        )
                    )
                }

                if (logScaleWeight) {
                    SingleRowWeightStepper(
                        label = "CURRENT WEIGHT",
                        valueText = UnitFormatter.formatWeightOneDecimal(draftWeightKg, profile.useMetricUnits),
                        onMinus = { draftWeightKg = (draftWeightKg - 0.5f).coerceAtLeast(40f) },
                        onPlus = { draftWeightKg = (draftWeightKg + 0.5f).coerceAtMost(180f) }
                    )
                }

                // Tucked Optional Waist Link (Hidden by default so users aren't asked for waist cm!)
                if (showOptionalWaist) {
                    SingleRowWeightStepper(
                        label = "WAIST TAPE (OPTIONAL)",
                        valueText = "${"%.1f".format(draftWaistCm)} cm",
                        onMinus = { draftWaistCm = (draftWaistCm - 0.5f).coerceAtLeast(50f) },
                        onPlus = { draftWaistCm = (draftWaistCm + 0.5f).coerceAtMost(160f) }
                    )
                } else {
                    Text(
                        text = "+ Add optional waist cm",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProgSlate,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showOptionalWaist = true }
                            .padding(vertical = 2.dp)
                    )
                }

                Button(
                    onClick = {
                        val savedTag = if (logScaleWeight) {
                            selectedFeelTag
                        } else {
                            "NO-SCALE • $selectedFeelTag"
                        }
                        onSubmitWeeklyCheckIn(
                            draftWeightKg,
                            draftWaistCm,
                            savedTag,
                            logScaleWeight
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_submit_weekly_audit"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProgInk,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = strings.saveCheckInCta,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cleanAudits.isNotEmpty()) {
                    HorizontalDivider(color = ProgDivider)
                    Text(
                        text = "YOUR SAVED CHECK-INS",
                        fontSize = 10.sp,
                        color = ProgSlate,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    cleanAudits.take(4).forEach { log ->
                        val isNoScale = log.auditTag.startsWith("NO-SCALE • ")
                        val cleanFeelLabel = log.auditTag.removePrefix("NO-SCALE • ")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = log.recordedDateIso,
                                fontSize = 12.sp,
                                color = ProgSlate
                            )
                            Text(
                                text = if (isNoScale) {
                                    cleanFeelLabel
                                } else {
                                    "${UnitFormatter.formatWeightOneDecimal(log.morningDryWeightKg, profile.useMetricUnits)}  •  $cleanFeelLabel"
                                },
                                fontSize = 12.sp,
                                color = ProgInk,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. Monochrome Today's Consistency Checklist
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, ProgHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Core Habits",
                            fontSize = 16.sp,
                            color = ProgInk,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${compliance.compliancePercentage}%",
                            fontSize = 14.sp,
                            color = ProgInk,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { compliance.compliancePercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = ProgInk,
                        trackColor = SoftSurfaceTint,
                        strokeCap = StrokeCap.Round
                    )
                }

                HorizontalDivider(color = ProgDivider)
                HabitCheckRow(
                    title = "Daily Protein Target (${report.dailyProteinFloorGrams}g)",
                    subtitle = "${profile.consumedProteinGrams}g logged today",
                    checked = compliance.proteinFloorLocked || profile.consumedProteinGrams >= report.dailyProteinFloorGrams,
                    onClick = { onAddProteinGrams(30) }
                )

                HorizontalDivider(color = ProgDivider)
                HabitCheckRow(
                    title = "8,000 – 10,000 Daily Steps",
                    subtitle = "Steady fat-burning movement",
                    checked = compliance.steps10kReached,
                    onClick = onToggleSteps10k
                )

                HorizontalDivider(color = ProgDivider)
                HabitCheckRow(
                    title = "Zero Liquid Sugar & Soda",
                    subtitle = "Water, black coffee, or unsweetened tea",
                    checked = compliance.zeroSugarZeroFried,
                    onClick = onToggleZeroLiquidSugar
                )

                HorizontalDivider(color = ProgDivider)
                HabitCheckRow(
                    title = "Kitchen Closed 2h Before Sleep",
                    subtitle = "Protects deep recovery & digestion",
                    checked = compliance.kitchenClosedBy8pm,
                    onClick = onToggleKitchenClosed8pm
                )
            }
        }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VerticalHairlineDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(34.dp)
            .background(ProgDivider)
    )
}

@Composable
private fun ProgressMetricCol(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = ProgSlate,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = ProgInk,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun SingleRowWeightStepper(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SoftSurfaceTint,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = ProgSlate,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = valueText,
                    fontSize = 18.sp,
                    color = ProgInk,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = onMinus,
                    shape = CircleShape,
                    color = PureWhiteCard,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease $label",
                            tint = ProgInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Surface(
                    onClick = onPlus,
                    shape = CircleShape,
                    color = PureWhiteCard,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase $label",
                            tint = ProgInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitCheckRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = ProgInk,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = ProgSlate
            )
        }

        if (checked) {
            Surface(
                shape = CircleShape,
                color = ProgInk,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
            )
        }
    }
}
