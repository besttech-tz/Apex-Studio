package com.example.ui.components

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.ApexCoachMovementSpec
import com.example.domain.BiologicalSex
import com.example.domain.QuickStrikeWorkout
import com.example.domain.TargetBodyZone
import com.example.domain.TargetExerciseDirectory
import com.example.domain.TargetExerciseGroup
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider

private val DirInk = Color(0xFF0F172A)
private val DirSlate = Color(0xFF475569)
private val DirBlue = Color(0xFF1D4ED8)
private val DirBlueSoft = Color(0xFFEFF6FF)
private val DirBlueBorder = Color(0xFFBFDBFE)

/**
 * Head-to-Toe Target Exercises Directory Card (Displayed at Very Top of Workouts & on Dashboard):
 * - Lets users tap any specific body/skill target (Chest, Shoulders, Back, Arms, Grip, Six-Pack,
 *   Waist, Kegel, Quads, Booty/Hips, Hamstrings, Calves, Neck/Jaw/Tongue Mewing, Voice & Breath,
 *   Calisthenics Lab, Stamina, Flexibility, Tall Posture).
 * - Opens a dedicated 2nd-level Target Exercise Modal showing Expected Result Timeline,
 *   individual exercises with 0->Hero Tips, and 1-tap Start Focused Session.
 */
@Composable
fun TargetExercisesTopDirectoryCard(
    sex: BiologicalSex,
    onStartTargetWorkout: (QuickStrikeWorkout) -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false
) {
    var selectedZone by rememberSaveable { mutableStateOf(TargetBodyZone.ALL) }
    var activeGroup by remember { mutableStateOf<TargetExerciseGroup?>(null) }
    var showAllModal by remember { mutableStateOf(false) }

    val zoneGroups = remember(selectedZone, sex) {
        TargetExerciseDirectory.groupsForZone(selectedZone, sex)
    }

    activeGroup?.let { group ->
        TargetGroupDetailDialog(
            group = group,
            sex = sex,
            onStartSession = {
                val workout = group.toQuickStrikeWorkout()
                activeGroup = null
                showAllModal = false
                onStartTargetWorkout(workout)
            },
            onDismiss = { activeGroup = null }
        )
    }

    if (showAllModal && activeGroup == null) {
        AllTargetsCatalogDialog(
            sex = sex,
            onSelectGroup = { grp -> activeGroup = grp },
            onDismiss = { showAllModal = false }
        )
    }

    val zoneScroll = rememberScrollState()
    val cardScroll = rememberScrollState()

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, CardBorderLight),
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            .testTag("card_target_exercises_directory")
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "HEAD-TO-TOE TARGET EXERCISES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DirBlue,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Browse by Body Part & Skill",
                        fontSize = if (compactMode) 15.sp else 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DirInk
                    )
                    Text(
                        text = "Chest, Back, Glutes, Kegel, Calisthenics, Neck/Tongue & Voice",
                        fontSize = 11.sp,
                        color = DirSlate,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    onClick = { showAllModal = true },
                    shape = RoundedCornerShape(999.dp),
                    color = DirInk,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .testTag("btn_open_all_18_targets")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "All 18 →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Zone Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(zoneScroll),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TargetBodyZone.entries.forEach { zone ->
                    val selected = zone == selectedZone
                    Surface(
                        onClick = { selectedZone = zone },
                        shape = RoundedCornerShape(999.dp),
                        color = if (selected) DirInk else InnerFrameSurface,
                        border = BorderStroke(1.dp, if (selected) DirInk else InnerFrameBorder),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 34.dp)
                            .testTag("target_zone_pill_${zone.id.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = zone.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else DirInk
                            )
                        }
                    }
                }
            }

            // Horizontal Visual Target Cards with Distinct Cover + Result Timeline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(cardScroll),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                zoneGroups.forEach { group ->
                    val workoutProxy = remember(group.id) { group.toQuickStrikeWorkout() }
                    val coverRes = WorkoutCoverResolver.coverForQuickWorkout(workoutProxy, sex)
                    Surface(
                        onClick = { activeGroup = group },
                        shape = RoundedCornerShape(14.dp),
                        color = InnerFrameSurface,
                        border = BorderStroke(1.dp, InnerFrameBorder),
                        modifier = Modifier
                            .width(154.dp)
                            .testTag("target_group_chip_${group.id}")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(78.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = coverRes),
                                    contentDescription = group.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color(0xCC090D16))
                                            )
                                        )
                                )
                                Text(
                                    text = group.shortChipLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(horizontal = 9.dp, vertical = 6.dp)
                                )
                            }
                            Column(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = "${group.exercises.size} moves • ${group.durationMinutes} min",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DirInk,
                                    maxLines = 1
                                )
                                Text(
                                    text = group.expectedResultsTimeline,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DirBlue,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllTargetsCatalogDialog(
    sex: BiologicalSex,
    onSelectGroup: (TargetExerciseGroup) -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, CardBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
                .heightIn(max = 670.dp)
                .testTag("dialog_all_target_exercises")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMPLETE BODY & SKILL DIRECTORY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DirBlue,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "All 18 Target Exercise Zones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DirInk
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DirSlate)
                    }
                }
                HorizontalDivider(color = SubtleDivider)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TargetBodyZone.entries.filter { it != TargetBodyZone.ALL }.forEach { zone ->
                        val groups = TargetExerciseDirectory.groupsForZone(zone, sex)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = zone.label.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DirSlate,
                                letterSpacing = 0.5.sp
                            )
                            groups.forEach { group ->
                                Surface(
                                    onClick = { onSelectGroup(group) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = InnerFrameSurface,
                                    border = BorderStroke(1.dp, InnerFrameBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("modal_target_row_${group.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = group.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DirInk
                                            )
                                            Text(
                                                text = "Results: ${group.expectedResultsTimeline}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = DirBlue
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = DirInk
                                        ) {
                                            Text(
                                                text = "Open →",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetGroupDetailDialog(
    group: TargetExerciseGroup,
    sex: BiologicalSex,
    onStartSession: () -> Unit,
    onDismiss: () -> Unit
) {
    var inspectedMove by remember { mutableStateOf<ApexCoachMovementSpec?>(null) }
    val scrollState = rememberScrollState()

    inspectedMove?.let { move ->
        ExerciseMasteryCoachDialog(
            exerciseName = move.name,
            primaryMuscle = move.targetMusclePill,
            animationType = move.animationType,
            sex = sex,
            existingCues = listOf(move.formCueSummary),
            initialTabZeroToHero = false,
            onDismiss = { inspectedMove = null }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, CardBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
                .heightIn(max = 670.dp)
                .testTag("dialog_target_group_detail")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "TARGET EXERCISES • ${group.shortChipLabel.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DirBlue,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = group.title,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DirInk
                        )
                        Text(
                            text = group.subtitle,
                            fontSize = 12.sp,
                            color = DirSlate
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_close_target_group_detail")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DirSlate)
                    }
                }

                // Expected Result Timeline Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DirBlueSoft,
                    border = BorderStroke(1.dp, DirBlueBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = DirBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EXPECTED RESULT TIMELINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DirBlue,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = group.expectedResultsTimeline,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DirInk
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = SubtleDivider)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TAP ANY EXERCISE FOR HOW-TO & 0→HERO TIPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DirSlate,
                        letterSpacing = 0.4.sp
                    )
                    group.exercises.forEachIndexed { idx, move ->
                        Surface(
                            onClick = { inspectedMove = move },
                            shape = RoundedCornerShape(12.dp),
                            color = InnerFrameSurface,
                            border = BorderStroke(1.dp, InnerFrameBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_detail_move_$idx")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnimatedWorkoutCharacter(
                                    animationType = move.animationType,
                                    exerciseName = move.name,
                                    containerColor = Color.White,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = move.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DirInk
                                    )
                                    Text(
                                        text = "${move.targetMusclePill}  •  0→Hero Tips",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DirBlue
                                    )
                                }
                                Text(
                                    text = "${move.totalSets} × ${move.setsAndRepsLabel}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DirInk
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = SubtleDivider)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = onStartSession,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DirInk,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_start_target_group_session")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start ${group.shortChipLabel} Session (${group.durationMinutes} min)",
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
