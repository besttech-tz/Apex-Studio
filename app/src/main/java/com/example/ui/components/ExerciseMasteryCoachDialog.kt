package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.BiologicalSex
import com.example.domain.ExerciseAnimationType
import com.example.domain.ExerciseZeroToHeroGuide

private val CoachDialogCard = Color(0xFFFFFFFF)
private val CoachDialogInk = Color(0xFF0F172A)
private val CoachDialogSlate = Color(0xFF475569)
private val CoachDialogSoftBg = Color(0xFFF1F5F9)
private val CoachDialogBorder = Color(0xFFE2E8F0)
private val CoachAccentBlue = Color(0xFF1D63ED)
private val CoachRescueBg = Color(0xFFFFF7ED)
private val CoachRescueBorder = Color(0xFFFED7AA)
private val CoachRescueInk = Color(0xFF9A3412)

/**
 * Universal Exercise Mastery & "0 → Hero" Failure Rescue Dialog:
 * - Tab 1 ("How To Do It"): Live anatomical animation, crisp numbered setup steps, breathing rule, & mistake to avoid.
 * - Tab 2 ("Can't Do It? 0→Hero"): Honest why-it-feels-hard note, 4-stage Zero-to-Hero progression ladder
 *   (Step 0 -> Step 3), instant mid-set failure rescue tips, and optional 1-tap "Switch to Easier Variation ⇄" action.
 */
@Composable
fun ExerciseMasteryCoachDialog(
    exerciseName: String,
    primaryMuscle: String,
    animationType: ExerciseAnimationType,
    sex: BiologicalSex = BiologicalSex.MALE,
    existingCues: List<String> = emptyList(),
    existingBreathing: String = "",
    existingMistake: String = "",
    initialTabZeroToHero: Boolean = false,
    onOpenSwapExercise: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var showZeroToHeroTab by remember(exerciseName, initialTabZeroToHero) {
        mutableStateOf(initialTabZeroToHero)
    }

    val guide = remember(exerciseName, primaryMuscle, sex, existingCues, existingBreathing, existingMistake) {
        ExerciseZeroToHeroGuide.resolveGuide(
            exerciseName = exerciseName,
            primaryMuscle = primaryMuscle,
            sex = sex,
            existingCues = existingCues,
            existingBreathing = existingBreathing,
            existingMistake = existingMistake
        )
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = CoachDialogCard,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, CoachDialogBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .heightIn(max = 650.dp)
                .testTag("dialog_zero_to_hero_coach")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Header + Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 8.dp, top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = guide.movementFamilyTitle.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoachAccentBlue,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = exerciseName,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoachDialogInk,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (primaryMuscle.isNotBlank()) {
                            Text(
                                text = primaryMuscle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CoachDialogSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_close_zero_to_hero")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Exercise Coach",
                            tint = CoachDialogSlate
                        )
                    }
                }

                // 2. Clean 2-Tab Segmented Switcher: [ How To Do It | Can't Do It? 0→Hero ]
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CoachDialogSoftBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { showZeroToHeroTab = false },
                            shape = RoundedCornerShape(9.dp),
                            color = if (!showZeroToHeroTab) CoachDialogInk else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 40.dp)
                                .testTag("tab_coach_how_to")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(
                                    text = "How To Do It",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (!showZeroToHeroTab) Color.White else CoachDialogInk,
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            onClick = { showZeroToHeroTab = true },
                            shape = RoundedCornerShape(9.dp),
                            color = if (showZeroToHeroTab) CoachDialogInk else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 40.dp)
                                .testTag("tab_coach_zero_to_hero")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(
                                    text = "Can't Do It? 0→Hero",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (showZeroToHeroTab) Color.White else CoachDialogInk,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CoachDialogBorder)

                // 3. Scrollable Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (guide.plainPurpose.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_coach_plain_purpose")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "WHAT THIS EXERCISE IS FOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CoachAccentBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = guide.plainPurpose,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoachDialogInk
                                )
                            }
                        }
                    }

                    if (!showZeroToHeroTab) {
                        // TAB 1: HOW TO DO IT
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, CoachDialogBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedWorkoutCharacter(
                                animationType = animationType,
                                exerciseName = exerciseName,
                                containerColor = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(148.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "STEP-BY-STEP EXECUTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CoachDialogInk,
                                letterSpacing = 0.4.sp
                            )
                            guide.setupSteps.forEachIndexed { idx, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .background(CoachDialogSoftBg, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CoachDialogInk
                                        )
                                    }
                                    Text(
                                        text = step,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        color = CoachDialogInk,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CoachDialogSoftBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Breathing: ${guide.breathingRule}",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CoachAccentBlue
                                )
                                Text(
                                    text = "Avoid: ${guide.mistakeToAvoid}",
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CoachDialogSlate
                                )
                            }
                        }

                        Surface(
                            onClick = { showZeroToHeroTab = true },
                            shape = RoundedCornerShape(12.dp),
                            color = CoachRescueBg,
                            border = BorderStroke(1.dp, CoachRescueBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("btn_switch_to_zero_to_hero_tab")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Too hard or failed mid-set?",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CoachRescueInk
                                    )
                                    Text(
                                        text = "Open the 4-Step 0 → Hero progression ladder",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CoachRescueInk.copy(alpha = 0.85f)
                                    )
                                }
                                Text(
                                    text = "0→Hero →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CoachRescueInk
                                )
                            }
                        }
                    } else {
                        // TAB 2: CAN'T DO IT? 0 -> HERO PROGRESSION LADDER + FAILED-SET RESCUE
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CoachDialogSoftBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = "WHY IT FEELS HARD AT FIRST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CoachAccentBlue,
                                    letterSpacing = 0.4.sp
                                )
                                Text(
                                    text = guide.whyPeopleStruggle,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = CoachDialogInk,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "0 → HERO PROGRESSION PATH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoachDialogInk,
                            letterSpacing = 0.4.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            guide.zeroToHeroLadder.forEachIndexed { idx, step ->
                                val isHeroStage = idx == guide.zeroToHeroLadder.lastIndex
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isHeroStage) Color(0xFFEFF6FF) else CoachDialogCard,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isHeroStage) Color(0xFFBFDBFE) else CoachDialogBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ladder_step_card_$idx")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = step.stepBadge,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isHeroStage) CoachAccentBlue else CoachDialogSlate,
                                                letterSpacing = 0.4.sp
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isHeroStage) Color.White else CoachDialogSoftBg
                                            ) {
                                                Text(
                                                    text = step.prescription,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CoachDialogInk,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = step.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CoachDialogInk
                                        )

                                        Text(
                                            text = step.coachingCue,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = CoachDialogSlate
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CoachRescueBg,
                            border = BorderStroke(1.dp, CoachRescueBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "FAILED MID-SET? INSTANT RESCUE TIPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CoachRescueInk,
                                    letterSpacing = 0.4.sp
                                )
                                guide.failedSetRescueTips.forEach { tip ->
                                    Text(
                                        text = "• $tip",
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = CoachRescueInk
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Optional Bottom Action Bar (1-Tap Switch to Easier Variation when available!)
                if (onOpenSwapExercise != null) {
                    HorizontalDivider(color = CoachDialogBorder)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenSwapExercise()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CoachDialogInk,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_zero_to_hero_open_swap")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Switch to Easier Variation Now",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
