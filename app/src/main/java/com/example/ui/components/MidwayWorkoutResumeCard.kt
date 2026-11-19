package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MidwayWorkoutSnapshot
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard

private val MidwayInk = Color(0xFF0F172A)
private val MidwaySlate = Color(0xFF475569)
private val MidwayBlue = Color(0xFF1D63ED)
private val MidwayBlueTint = Color(0xFFEFF6FF)
private val MidwayBlueBorder = Color(0xFFBFDBFE)
private val MidwayEmerald = Color(0xFF059669)
private val MidwayEmeraldDark = Color(0xFF047857)
private val MidwayEmeraldTint = Color(0xFFECFDF5)
private val MidwayEmeraldBorder = Color(0xFFA7F3D0)

/**
 * Displayed on both Dashboard and Workout Hub whenever a user leaves a workout midway.
 * Shows the exact exercise and set left off, and offers:
 * 1. Continue (resumes at the exact exercise & set)
 * 2. Start New (restarts cleanly from Move 1, Set 1)
 * 3. Mark Complete (especially highlighted when Main Workout is done and user left before Post-Gym Cool-Down)
 */
@Composable
fun MidwayWorkoutResumeCard(
    snapshot: MidwayWorkoutSnapshot,
    onContinueWorkout: () -> Unit,
    onStartNewWorkout: () -> Unit,
    onMarkWorkoutComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mainDone = snapshot.mainWorkoutFinished
    val outerBorderColor = if (mainDone) {
        MidwayEmerald.copy(alpha = 0.55f)
    } else {
        MidwayBlue.copy(alpha = 0.45f)
    }

    val badgeBg = if (mainDone) MidwayEmeraldTint else MidwayBlueTint
    val badgeBorder = if (mainDone) MidwayEmeraldBorder else MidwayBlueBorder
    val badgeText = if (mainDone) MidwayEmeraldDark else MidwayBlue
    val badgeLabel = if (mainDone) {
        "✓ MAIN WORKOUT DONE • COOL-DOWN LEFT"
    } else {
        "IN PROGRESS • ${snapshot.stageType.shortBadge}"
    }

    val pauseDetailText = if (mainDone) {
        "Main exercises done • Left before Post-Gym Cool-Down (${snapshot.stageLocalIndex + 1}/${snapshot.stageTotalCount})"
    } else {
        "Set ${snapshot.currentSetNumber} of ${snapshot.totalSetsForCurrent}  •  ${snapshot.stageType.shortBadge} ${snapshot.stageLocalIndex + 1}/${snapshot.stageTotalCount}"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.5.dp, outerBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x140F172A))
            .testTag("card_midway_workout_session")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            // 1. Top Status Pill + Overall Move Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeBorder),
                    modifier = Modifier.testTag("badge_midway_workout_status")
                ) {
                    Text(
                        text = badgeLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeText,
                        letterSpacing = 0.4.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Move ${snapshot.playlistIndex + 1} of ${snapshot.totalPlaylistCount}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MidwaySlate
                )
            }

            // 2. Left-Off Exercise Preview + Workout & Exercise Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PureWhiteCard,
                    border = BorderStroke(1.dp, CardBorderLight),
                    modifier = Modifier.size(64.dp)
                ) {
                    AnimatedWorkoutCharacter(
                        animationType = snapshot.currentExerciseAnim,
                        exerciseName = snapshot.currentExerciseName,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = snapshot.workoutTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MidwayInk,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Left at: ${snapshot.currentExerciseName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mainDone) MidwayEmeraldDark else MidwayBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("txt_midway_exercise_name")
                    )
                    Text(
                        text = pauseDetailText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MidwaySlate,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 3. Session Progress Bar
            LinearProgressIndicator(
                progress = { snapshot.progressFraction },
                color = if (mainDone) MidwayEmerald else MidwayBlue,
                trackColor = Color(0xFFE2E8F0),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
            )

            // 4. Action Buttons: Continue, Start New, Mark Complete (48dp touch targets)
            if (mainDone) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onMarkWorkoutComplete,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MidwayEmerald,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("btn_complete_midway_workout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Complete",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onContinueWorkout,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MidwayInk,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("btn_resume_midway_workout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Continue",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    onClick = onStartNewWorkout,
                    shape = RoundedCornerShape(12.dp),
                    color = InnerFrameSurface,
                    border = BorderStroke(1.dp, CardBorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_restart_midway_workout")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MidwayInk,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start New Workout",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MidwayInk
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onContinueWorkout,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MidwayBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1.25f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("btn_resume_midway_workout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Continue",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }

                    Surface(
                        onClick = onStartNewWorkout,
                        shape = RoundedCornerShape(12.dp),
                        color = InnerFrameSurface,
                        border = BorderStroke(1.dp, CardBorderLight),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("btn_restart_midway_workout")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MidwayInk,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Start New",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MidwayInk,
                                maxLines = 1
                            )
                        }
                    }
                }

                Surface(
                    onClick = onMarkWorkoutComplete,
                    shape = RoundedCornerShape(12.dp),
                    color = MidwayEmeraldTint,
                    border = BorderStroke(1.dp, MidwayEmeraldBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_complete_midway_workout")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MidwayEmeraldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Workout Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MidwayEmeraldDark
                        )
                    }
                }
            }
        }
    }
}
