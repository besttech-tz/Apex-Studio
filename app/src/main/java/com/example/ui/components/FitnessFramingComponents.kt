package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutIntensityMode
import com.example.system.TacticalAudioHaptics
import com.example.system.WorkoutMusicEngine
import com.example.system.WorkoutMusicTrack
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.TextPrimarySlate
import com.example.ui.theme.TextSecondarySlate

/**
 * Atmospheric Fitness Canvas Backdrop:
 * Adds a subtle, structured top studio gradient + fine architectural dot-matrix pattern
 * so screens never look flat, empty, or "naked" while keeping the clean monochrome palette intact.
 */
@Composable
fun FitnessAtmosphericContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackground)
            .drawBehind {
                val topBandHeight = 240.dp.toPx().coerceAtMost(size.height)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEAF0F7),
                            HomeBackground
                        ),
                        startY = 0f,
                        endY = topBandHeight
                    )
                )
            },
        content = content
    )
}

/**
 * Studio Photography Banner Header Card:
 * Brings cohesive athletic gym atmosphere to Onboarding steps, Settings, My Plan, and inside pages.
 */
@Composable
fun StudioPageBannerHeader(
    @DrawableRes imageResId: Int,
    eyebrowBadge: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    bannerHeightDp: Int = 136,
    trailingBadgeText: String? = null
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x120F172A))
            .clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeightDp.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x66090D16),
                                Color(0xB5090D16),
                                Color(0xEC090D16)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.Black.copy(alpha = 0.42f)
                    ) {
                        Text(
                            text = eyebrowBadge.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.92f),
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (!trailingBadgeText.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.Black.copy(alpha = 0.48f)
                        ) {
                            Text(
                                text = trailingBadgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.86f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 1-Tap Exercise Intensity Selector: [ Simple | Moderate | Extensive ]
 */
@Composable
fun WorkoutIntensitySelectorBar(
    selectedMode: WorkoutIntensityMode,
    estimatedKcal: Int,
    onSelectMode: (WorkoutIntensityMode) -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "intensity_mode"
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = InnerFrameSurface,
        border = BorderStroke(1.dp, InnerFrameBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORKOUT INTENSITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSecondarySlate,
                    letterSpacing = 0.6.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PureWhiteCard,
                    border = BorderStroke(1.dp, InnerFrameBorder)
                ) {
                    Text(
                        text = "~${selectedMode.estMinutes} min  •  ~$estimatedKcal kcal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimarySlate,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE5ECF4),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WorkoutIntensityMode.entries.forEach { mode ->
                        val selected = selectedMode == mode
                        Surface(
                            onClick = { onSelectMode(mode) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selected) TextPrimarySlate else Color.Transparent,
                            shadowElevation = if (selected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 38.dp)
                                .testTag("${testTagPrefix}_${mode.id.lowercase()}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = mode.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (selected) Color.White else TextPrimarySlate,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${mode.exercisesCount} moves",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White.copy(alpha = 0.82f) else TextSecondarySlate,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4-Way Motivation Audio & Coach Selector Row:
 * [ Both On (Music + Voice) | Music Only | Voice Only | Muted ]
 */
@Composable
fun WorkoutAudioCoachModeSelector(
    selectedMode: WorkoutAudioCoachMode,
    onSelectMode: (WorkoutAudioCoachMode) -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "audio_coach_mode"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COACH & MUSIC MODE",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextSecondarySlate,
                letterSpacing = 0.5.sp
            )
            Text(
                text = selectedMode.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimarySlate
            )
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE5ECF4),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WorkoutAudioCoachMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Surface(
                        onClick = { onSelectMode(mode) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) TextPrimarySlate else Color.Transparent,
                        shadowElevation = if (isSelected) 1.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 36.dp)
                            .testTag("${testTagPrefix}_${mode.id.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mode.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimarySlate,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Framed Workout Motivation Music & Voice Coach Studio Card:
 * - Zero technical "128 BPM" jargon — clean motivating track names ("Drive & Power", "In The Zone", "Calm Flow")
 * - Direct 4-way Coach & Music selector (Both On | Music Only | Voice Only | Mute All)
 * - 1-tap shortcut to open the user's phone music app ("My Music")
 */
@Composable
fun WorkoutMusicStudioCard(
    modifier: Modifier = Modifier,
    audioCoachMode: WorkoutAudioCoachMode? = null,
    onSelectAudioCoachMode: ((WorkoutAudioCoachMode) -> Unit)? = null,
    testTag: String = "card_workout_music_studio"
) {
    val context = LocalContext.current
    val musicState by WorkoutMusicEngine.state.collectAsStateWithLifecycle()
    var localFallbackMode by remember {
        mutableStateOf(
            when {
                musicState.musicAllowed && !TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.BOTH_ACTIVE
                musicState.musicAllowed && TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.MUSIC_ONLY
                !musicState.musicAllowed && !TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.VOICE_ONLY
                else -> WorkoutAudioCoachMode.MUTED
            }
        )
    }
    val effectiveMode = audioCoachMode ?: localFallbackMode

    fun applyMode(mode: WorkoutAudioCoachMode) {
        localFallbackMode = mode
        TacticalAudioHaptics.isVoiceCoachMuted = !mode.voiceEnabled
        WorkoutMusicEngine.setMusicAllowed(mode.musicEnabled)
        if (!mode.musicEnabled) {
            WorkoutMusicEngine.stop()
        }
        onSelectAudioCoachMode?.invoke(mode)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, CardBorderLight),
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x100F172A))
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Top Dark Studio Music & Voice Coach Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_fitness_music_studio_1794761666737),
                    contentDescription = "Workout Motivation Sound & Voice Coach",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xEB090D16),
                                    Color(0xD10F172A),
                                    Color(0x940F172A)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.16f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (musicState.isPlaying) {
                                    AnimatedEqualizerBars(barColor = Color.White)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "MOTIVATION MUSIC & VOICE COACH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White.copy(alpha = 0.80f),
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = if (musicState.isPlaying) {
                                    "Playing: ${musicState.selectedTrack.title}"
                                } else {
                                    "${musicState.selectedTrack.title}  •  ${effectiveMode.label}"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = musicState.selectedTrack.vibeSubtitle,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.84f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = {
                            if (!effectiveMode.musicEnabled) {
                                applyMode(
                                    if (effectiveMode.voiceEnabled) WorkoutAudioCoachMode.BOTH_ACTIVE
                                    else WorkoutAudioCoachMode.MUSIC_ONLY
                                )
                            }
                            WorkoutMusicEngine.togglePlayPause()
                        },
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_music_play_pause")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (musicState.isPlaying) "Pause Workout Music" else "Play Workout Music",
                                tint = TextPrimarySlate,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // 2. Framed Controls: 4-Mode Coach/Music Switcher + Clean Track Names + My Music
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InnerFrameSurface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                WorkoutAudioCoachModeSelector(
                    selectedMode = effectiveMode,
                    onSelectMode = { mode ->
                        applyMode(mode)
                        if (mode.musicEnabled && musicState.isPlaying) {
                            WorkoutMusicEngine.start(musicState.selectedTrack)
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkoutMusicTrack.entries.forEach { track ->
                        val isSelected = musicState.selectedTrack == track
                        Surface(
                            onClick = {
                                if (!effectiveMode.musicEnabled) {
                                    applyMode(
                                        if (effectiveMode.voiceEnabled) WorkoutAudioCoachMode.BOTH_ACTIVE
                                        else WorkoutAudioCoachMode.MUSIC_ONLY
                                    )
                                }
                                WorkoutMusicEngine.selectTrack(track, autoPlay = true)
                            },
                            shape = RoundedCornerShape(9.dp),
                            color = if (isSelected) TextPrimarySlate else PureWhiteCard,
                            border = BorderStroke(1.dp, if (isSelected) TextPrimarySlate else InnerFrameBorder),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 38.dp)
                                .testTag("music_track_${track.id.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = track.shortLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else TextPrimarySlate,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = { WorkoutMusicEngine.openPhoneMusicApp(context) },
                        shape = RoundedCornerShape(9.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, InnerFrameBorder),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 38.dp)
                            .testTag("btn_open_phone_music_app")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "My Music",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimarySlate
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Phone Music App",
                                tint = TextSecondarySlate,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact Audio & Coach Motivation Pill for Active Workout & Rest Screens:
 * Shows current motivation mode ("Both On" / "Music" / "Voice" / "Muted") and opens a 1-tap
 * Audio & Coach Motivation Sheet so the user can choose Music + Voice, Music Only, Voice Only,
 * or Mute All mid-workout without interrupting their set!
 */
@Composable
fun CompactMusicPill(
    modifier: Modifier = Modifier,
    audioCoachMode: WorkoutAudioCoachMode? = null,
    onSelectAudioCoachMode: ((WorkoutAudioCoachMode) -> Unit)? = null
) {
    val context = LocalContext.current
    val musicState by WorkoutMusicEngine.state.collectAsStateWithLifecycle()
    var showAudioSheet by remember { mutableStateOf(false) }
    var localFallbackMode by remember {
        mutableStateOf(
            when {
                musicState.musicAllowed && !TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.BOTH_ACTIVE
                musicState.musicAllowed && TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.MUSIC_ONLY
                !musicState.musicAllowed && !TacticalAudioHaptics.isVoiceCoachMuted -> WorkoutAudioCoachMode.VOICE_ONLY
                else -> WorkoutAudioCoachMode.MUTED
            }
        )
    }
    val effectiveMode = audioCoachMode ?: localFallbackMode

    fun updateMode(next: WorkoutAudioCoachMode, startMusicIfAllowed: Boolean = true) {
        localFallbackMode = next
        TacticalAudioHaptics.isVoiceCoachMuted = !next.voiceEnabled
        WorkoutMusicEngine.setMusicAllowed(next.musicEnabled)
        if (next.musicEnabled && startMusicIfAllowed) {
            WorkoutMusicEngine.start(musicState.selectedTrack)
        } else if (!next.musicEnabled) {
            WorkoutMusicEngine.stop()
        }
        onSelectAudioCoachMode?.invoke(next)
    }

    if (showAudioSheet) {
        Dialog(onDismissRequest = { showAudioSheet = false }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, CardBorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dialog_workout_audio_coach_mode")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Motivation Audio & Coach",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimarySlate
                            )
                            Text(
                                text = effectiveMode.description,
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                        IconButton(
                            onClick = { showAudioSheet = false },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Audio Controls",
                                tint = TextSecondarySlate
                            )
                        }
                    }

                    // 4 Explicit Mode Cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WorkoutAudioCoachMode.entries.forEach { mode ->
                            val selected = effectiveMode == mode
                            val icon = when (mode) {
                                WorkoutAudioCoachMode.BOTH_ACTIVE -> Icons.AutoMirrored.Filled.VolumeUp
                                WorkoutAudioCoachMode.MUSIC_ONLY -> Icons.Default.Headphones
                                WorkoutAudioCoachMode.VOICE_ONLY -> Icons.Default.RecordVoiceOver
                                WorkoutAudioCoachMode.MUTED -> Icons.AutoMirrored.Filled.VolumeOff
                            }
                            Surface(
                                onClick = { updateMode(mode, startMusicIfAllowed = true) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) TextPrimarySlate else InnerFrameSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) TextPrimarySlate else InnerFrameBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("sheet_audio_mode_${mode.id.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (selected) Color.White else TextPrimarySlate,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mode.label,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selected) Color.White else TextPrimarySlate
                                        )
                                        Text(
                                            text = mode.description,
                                            fontSize = 11.sp,
                                            color = if (selected) Color.White.copy(alpha = 0.82f) else TextSecondarySlate,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = InnerFrameBorder)

                    // Music Groove Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "WORKOUT MUSIC SOUNDTRACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondarySlate,
                            letterSpacing = 0.6.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            WorkoutMusicTrack.entries.forEach { track ->
                                val isSelected = musicState.selectedTrack == track
                                Surface(
                                    onClick = {
                                        if (!effectiveMode.musicEnabled) {
                                            updateMode(
                                                if (effectiveMode.voiceEnabled) WorkoutAudioCoachMode.BOTH_ACTIVE
                                                else WorkoutAudioCoachMode.MUSIC_ONLY,
                                                startMusicIfAllowed = false
                                            )
                                        }
                                        WorkoutMusicEngine.selectTrack(track, autoPlay = true)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) TextPrimarySlate else InnerFrameSurface,
                                    border = BorderStroke(1.dp, if (isSelected) TextPrimarySlate else InnerFrameBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 42.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = track.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else TextPrimarySlate,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            onClick = { WorkoutMusicEngine.openPhoneMusicApp(context) },
                            shape = RoundedCornerShape(10.dp),
                            color = InnerFrameSurface,
                            border = BorderStroke(1.dp, InnerFrameBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 42.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Open My Phone Music App",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimarySlate
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = TextSecondarySlate,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val isAnyActive = effectiveMode != WorkoutAudioCoachMode.MUTED
    Surface(
        onClick = { showAudioSheet = true },
        shape = RoundedCornerShape(999.dp),
        color = if (isAnyActive) TextPrimarySlate else InnerFrameSurface,
        border = BorderStroke(1.dp, if (isAnyActive) TextPrimarySlate else InnerFrameBorder),
        modifier = modifier
            .defaultMinSize(minHeight = 38.dp)
            .testTag("pill_active_workout_music")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (musicState.isPlaying && effectiveMode.musicEnabled) {
                AnimatedEqualizerBars(barColor = Color.White, compact = true)
            } else {
                val pillIcon = when (effectiveMode) {
                    WorkoutAudioCoachMode.BOTH_ACTIVE -> Icons.AutoMirrored.Filled.VolumeUp
                    WorkoutAudioCoachMode.MUSIC_ONLY -> Icons.Default.Headphones
                    WorkoutAudioCoachMode.VOICE_ONLY -> Icons.Default.RecordVoiceOver
                    WorkoutAudioCoachMode.MUTED -> Icons.AutoMirrored.Filled.VolumeOff
                }
                Icon(
                    imageVector = pillIcon,
                    contentDescription = "Motivation Audio & Coach Mode",
                    tint = if (isAnyActive) Color.White else TextPrimarySlate,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = effectiveMode.shortLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isAnyActive) Color.White else TextPrimarySlate
            )
        }
    }
}

@Composable
private fun AnimatedEqualizerBars(
    barColor: Color,
    compact: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "eq_bars")
    val b1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(320, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "eq1"
    )
    val b2 by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(260, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "eq2"
    )
    val b3 by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "eq3"
    )

    val maxBarHeight = if (compact) 12.dp else 16.dp
    val barWidth = if (compact) 2.2.dp else 2.8.dp

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(maxBarHeight)
    ) {
        listOf(b1, b2, b3).forEach { frac ->
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxBarHeight * frac)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}

/**
 * Framed Stat Capsule — Eliminates bare floating numbers by giving every metric a crisp recessed container!
 */
@Composable
fun FramedStatCapsule(
    label: String,
    value: String,
    subTag: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = InnerFrameSurface,
        border = BorderStroke(1.dp, InnerFrameBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextSecondarySlate,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimarySlate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subTag.isNullOrBlank()) {
                Text(
                    text = subTag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondarySlate,
                    maxLines = 1
                )
            }
        }
    }
}
