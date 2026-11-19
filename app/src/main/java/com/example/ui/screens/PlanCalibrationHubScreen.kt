package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TrainingEnvironment
import com.example.domain.BiologicalSex
import com.example.domain.CoachTruthAndSplitAdvisor
import com.example.domain.CurrentBfBracket
import com.example.domain.DailyExerciseDose
import com.example.domain.ExperienceLevel
import com.example.domain.GoalBfBracket
import com.example.domain.PlanStartOption
import com.example.domain.UnitFormatter
import com.example.domain.WeeklyFrequency
import com.example.domain.WeeklySchedulePlanner
import androidx.compose.material.icons.filled.Headphones
import com.example.R
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutIntensityMode
import com.example.system.ApexNotificationScheduler
import com.example.system.OperationalCheckpoint
import com.example.ui.ApexUiState
import com.example.ui.components.ExactReminderTimePickerDialog
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.FramedStatCapsule
import com.example.ui.components.PhysiqueSilhouettePreset
import com.example.ui.components.PlanStartDatePickerDialog
import com.example.ui.components.StudioPageBannerHeader
import com.example.ui.components.TransformationSilhouetteComparisonBanner
import com.example.ui.components.WorkoutAudioCoachModeSelector
import com.example.ui.components.WorkoutCoverResolver
import java.time.LocalDate
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.TextSecondarySlate

private val PlanInk = Color(0xFF0F172A)
private val PlanHairline = CardBorderLight
private val PlanDivider = SubtleDivider
private val DestructiveRed = Color(0xFFDC2626)

private data class FreeTimePreset(
    val id: String,
    val label: String,
    val timeLabel: String,
    val hour: Int,
    val minute: Int
)

private val FREE_TIME_ALARM_PRESETS = listOf(
    FreeTimePreset("morning", "Morning", "8:00 AM", 8, 0),
    FreeTimePreset("lunch", "Midday", "12:30 PM", 12, 30),
    FreeTimePreset("after_work", "Evening", "5:30 PM", 17, 30),
    FreeTimePreset("evening", "Night", "9:00 PM", 21, 0)
)

/**
 * APPLICATION SETTINGS SCREEN (Root) + DEDICATED "MY PLAN" SUB-SCREEN
 * - Root View (activeTab == 0): 100% Application Configuration ONLY:
 *   1. My Plan navigation row (drills into the My Plan sub-screen)
 *   2. General: App Language, Measurement Units (kg/cm vs lb/ft), Keep Screen On
 *   3. Notifications & Reminders: Master toggle, Free-Time Reminder time, Morning/Evening alerts
 *   4. Sound & Haptics: App Sound & Voice, Vibration Feedback
 *   5. Data & Storage: Clear Today's Logs, Restart Initial Setup, Reset All Progress Data, App Version
 *
 * - Sub-Screen (activeTab == 1): Dedicated "My Plan" screen with `<- Settings` back navigation
 *   for Body, Goal, Honest Timeline, Weekly Schedule, Rest Days, and Workout Session Volume.
 */
@Composable
fun PlanCalibrationHubScreen(
    uiState: ApexUiState,
    initialTab: Int = 0,
    onTabChanged: (Int) -> Unit = {},
    onSelectAppLanguage: (AppLanguage) -> Unit = {},
    onSetUseMetricUnits: (Boolean) -> Unit = {},
    onSetKeepScreenAwake: (Boolean) -> Unit = {},
    onSetVibrationEnabled: (Boolean) -> Unit = {},
    onClearTodayDailyLogs: () -> Unit = {},
    onResetAllAppProgress: () -> Unit = {},
    onSetMeasurementsKnown: (Boolean) -> Unit = {},
    onSelectVisualStartingBody: (String, CurrentBfBracket, String?) -> Unit = { _, _, _ -> },
    onSelectExercisesPerDay: (Int) -> Unit = {},
    onSelectWorkoutIntensity: (WorkoutIntensityMode) -> Unit = {},
    onSelectRestDurationSeconds: (Int) -> Unit = {},
    onSetIncludeWarmupAndStretch: (Boolean) -> Unit = {},
    onSetVoiceCoachEnabled: (Boolean) -> Unit = {},
    onSetAutoPlayWorkoutMusic: (Boolean) -> Unit = {},
    onSelectAudioCoachMode: (WorkoutAudioCoachMode) -> Unit = {},
    onUpdateBiometrics: (Float, Float, Int) -> Unit,
    onSelectBiologicalSex: (BiologicalSex) -> Unit,
    onSelectCurrentBracket: (CurrentBfBracket) -> Unit,
    onSelectGoalBracket: (GoalBfBracket) -> Unit,
    onUpdateSelectedShapeCardIds: (String?, String?) -> Unit,
    onSelectWeeklyFrequency: (WeeklyFrequency) -> Unit,
    onToggleRestDay: (Int) -> Unit = {},
    onSelectPlanStartOption: (PlanStartOption) -> Unit = {},
    onSelectCustomStartDate: (LocalDate) -> Unit = {},
    onSelectTrainingEnvironment: (TrainingEnvironment) -> Unit,
    onSelectExperienceLevel: (ExperienceLevel) -> Unit,
    onSelectRotationWeek: (Int) -> Unit = {},
    onToggleBackgroundAlarms: (Boolean) -> Unit,
    onUpdateCustomReminder: (Int, Int, Int, Boolean) -> Unit = { _, _, _, _ -> },
    onTestCheckpointAlarm: (OperationalCheckpoint) -> Unit,
    onRerunFullOnboardingWizard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTimePickerCheckpointId by rememberSaveable { mutableIntStateOf(0) }
    var showMyPlanStartDatePicker by rememberSaveable { mutableStateOf(false) }

    if (activeTimePickerCheckpointId != 0) {
        val (pickerTitle, curH, curM) = when (activeTimePickerCheckpointId) {
            ApexNotificationScheduler.ID_MORNING_PROTEIN ->
                Triple("Morning Check-In", uiState.profile.morningReminderHour, uiState.profile.morningReminderMinute)
            ApexNotificationScheduler.ID_EVENING_RESET ->
                Triple("Evening Reminder", uiState.profile.eveningReminderHour, uiState.profile.eveningReminderMinute)
            else ->
                Triple("Main Workout Reminder", uiState.profile.workoutReminderHour, uiState.profile.workoutReminderMinute)
        }
        ExactReminderTimePickerDialog(
            title = pickerTitle,
            initialHour = curH,
            initialMinute = curM,
            onTimeConfirmed = { newHour, newMinute ->
                val targetId = activeTimePickerCheckpointId
                activeTimePickerCheckpointId = 0
                onUpdateCustomReminder(targetId, newHour, newMinute, true)
            },
            onDismiss = { activeTimePickerCheckpointId = 0 }
        )
    }

    if (showMyPlanStartDatePicker) {
        PlanStartDatePickerDialog(
            initialDateIso = uiState.profile.planStartDateIso,
            onDateSelected = { pickedDate ->
                showMyPlanStartDatePicker = false
                onSelectCustomStartDate(pickedDate)
            },
            onDismiss = { showMyPlanStartDatePicker = false }
        )
    }
    val strings = LocalAppStrings.current
    val scrollState = rememberScrollState()
    val profile = uiState.profile
    val report = uiState.gapReport
    val isFemale = profile.biologicalSex == BiologicalSex.FEMALE

    var activeTab by rememberSaveable { mutableIntStateOf(initialTab.coerceIn(0, 1)) }
    LaunchedEffect(initialTab) {
        activeTab = initialTab.coerceIn(0, 1)
    }

    // System Back button inside My Plan returns cleanly to Application Settings
    BackHandler(enabled = activeTab == 1) {
        activeTab = 0
        onTabChanged(0)
    }

    var showBodyPicker by rememberSaveable { mutableStateOf(false) }
    var confirmFullResetArmed by rememberSaveable { mutableStateOf(false) }
    var showHealthDisclaimerDialog by rememberSaveable { mutableStateOf(false) }
    var showPrivacyPolicyDialog by rememberSaveable { mutableStateOf(false) }

    if (showHealthDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showHealthDisclaimerDialog = false },
            containerColor = PureWhiteCard,
            title = {
                Text(
                    text = "Health & Exercise Disclaimer",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlanInk
                )
            },
            text = {
                Text(
                    text = "Apex Protocol provides general fitness, movement, and whole-food educational guidance only and is not a substitute for professional medical advice, diagnosis, or treatment.\n\nConsult a qualified physician before beginning any exercise or nutrition routine—especially if you are pregnant, postpartum, managing joint/cardiac conditions, or recovering from injury. Stop exercising immediately if you feel sharp pain, dizziness, or shortness of breath.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextSecondarySlate
                )
            },
            confirmButton = {
                TextButton(onClick = { showHealthDisclaimerDialog = false }) {
                    Text("Understood", fontWeight = FontWeight.ExtraBold, color = PlanInk)
                }
            }
        )
    }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            containerColor = PureWhiteCard,
            title = {
                Text(
                    text = "Privacy & On-Device Data Policy",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlanInk
                )
            },
            text = {
                Text(
                    text = "Your privacy is 100% protected by offline-first design:\n\n• Zero Account Required: Your name, body goals, check-ins, photos, and workout logs are stored strictly on your device using Android Jetpack DataStore & Room.\n• Zero Cloud Tracking: Personal health metrics and photos are never uploaded to external ad networks or third-party servers.\n• Full User Control: You can erase all local logs and profile data anytime using 'Reset All Progress Data' in Settings.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextSecondarySlate
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("Close", fontWeight = FontWeight.ExtraBold, color = PlanInk)
                }
            }
        )
    }

    val startOptions = SmartBodyMatcher.ALL_STARTING_BODIES
    val currentStartObj = startOptions.firstOrNull { it.id == profile.selectedStartingShapeCardId }
        ?: startOptions[2]
    val targetOptions = SmartBodyMatcher.ALL_TARGET_GOALS
    val currentTargetObj = targetOptions.firstOrNull { it.id == profile.selectedGoalCardId }
        ?: targetOptions.first()

    FitnessAtmosphericContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .testTag("screen_plan_calibration_hub"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeTab == 0) {
                // =========================================================================
                // ROOT SCREEN: PURE APPLICATION CONFIGURATION SETTINGS
                // =========================================================================
                StudioPageBannerHeader(
                    imageResId = R.drawable.img_settings_studio_banner_1794766376203,
                    eyebrowBadge = "STUDIO PREFERENCES",
                    title = strings.settingsTitle,
                    subtitle = strings.settingsSubtitle,
                    bannerHeightDp = 128,
                    trailingBadgeText = AppLanguage.fromCode(profile.appLanguageCode).nativeLabel
                )

            // Optional status toast strip when an action completes (e.g. test notification / cleared logs)
            uiState.lastDispatchStatusMessage?.let { statusMsg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftSurfaceTint,
                    border = BorderStroke(1.dp, PlanHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusMsg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PlanInk,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            // 1. MY PLAN NAVIGATION ROW (Opens dedicated My Plan sub-screen)
            Surface(
                onClick = {
                    activeTab = 1
                    onTabChanged(1)
                },
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                    .testTag("card_settings_open_my_plan")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        SettingsLeadingIcon(Icons.Default.Tune)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = strings.tabMyPlan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PlanInk
                            )
                            Text(
                                text = "${currentTargetObj.titleFor(profile.biologicalSex)}  •  ${profile.weeklyFrequency.daysCount}d/wk",
                                fontSize = 12.sp,
                                color = TextSecondarySlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.testTag("btn_open_my_plan_subview")
                    ) {
                        Text(
                            text = strings.configureMyPlanCta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondarySlate
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondarySlate,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. GENERAL APPLICATION SETTINGS (Language, Units, Keep Screen On)
            SettingsSectionGroup(
                sectionLabel = strings.generalSectionTitle,
                testTag = "card_settings_general"
            ) {
                // 2A. App Language
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_settings_language"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsLeadingIcon(Icons.Default.Language)
                        Column {
                            Text(
                                text = strings.languageSectionTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                            Text(
                                text = AppLanguage.fromCode(profile.appLanguageCode).nativeLabel,
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val selected = AppLanguage.fromCode(profile.appLanguageCode) == lang
                            Surface(
                                onClick = { onSelectAppLanguage(lang) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) PlanInk else SoftSurfaceTint,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 42.dp)
                                    .testTag("lang_chip_${lang.code}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = lang.nativeLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else PlanInk,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = PlanDivider)

                // 2B. Measurement Units (Metric kg/cm vs Imperial lb/ft)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        SettingsLeadingIcon(Icons.Default.Straighten)
                        Column {
                            Text(
                                text = strings.unitsLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                            Text(
                                text = if (profile.useMetricUnits) "Kilograms & centimeters" else "Pounds & feet/inches",
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SoftSurfaceTint,
                        border = BorderStroke(1.dp, PlanHairline)
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            listOf(
                                true to "kg / cm",
                                false to "lb / ft"
                            ).forEach { (isMetric, unitTag) ->
                                val selected = profile.useMetricUnits == isMetric
                                Surface(
                                    onClick = { onSetUseMetricUnits(isMetric) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) PlanInk else Color.Transparent,
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 34.dp)
                                        .testTag(if (isMetric) "unit_chip_metric" else "unit_chip_imperial")
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = unitTag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else PlanInk
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = PlanDivider)

                // 2C. Keep Screen On
                SettingsToggleRow(
                    icon = Icons.Default.ScreenLockPortrait,
                    title = strings.keepScreenOnLabel,
                    subtitle = if (profile.keepScreenAwake) {
                        "Display stays awake while app is open"
                    } else {
                        "Follows standard phone screen timeout"
                    },
                    checked = profile.keepScreenAwake,
                    onCheckedChange = onSetKeepScreenAwake,
                    switchTag = "switch_keep_screen_on"
                )
            }

            // 3. NOTIFICATIONS & REMINDERS
            SettingsSectionGroup(
                sectionLabel = strings.freeTimeAlarmTitle,
                testTag = "card_daily_reminders_custom"
            ) {
                SettingsToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Allow Daily Reminders",
                    subtitle = if (profile.backgroundAlarmsArmed) {
                        "Active • Main reminder at ${ApexNotificationScheduler.formatTime12h(profile.workoutReminderHour, profile.workoutReminderMinute)}"
                    } else {
                        strings.freeTimeAlarmSubtitle
                    },
                    checked = profile.backgroundAlarmsArmed,
                    onCheckedChange = onToggleBackgroundAlarms,
                    switchTag = "switch_daily_reminders_master"
                )

                HorizontalDivider(color = PlanDivider)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "FREE-TIME REMINDER PRESETS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondarySlate,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FREE_TIME_ALARM_PRESETS.forEach { preset ->
                            val isPresetActive = profile.backgroundAlarmsArmed &&
                                profile.workoutReminderEnabled &&
                                profile.workoutReminderHour == preset.hour &&
                                profile.workoutReminderMinute == preset.minute
                            Surface(
                                onClick = {
                                    onUpdateCustomReminder(
                                        ApexNotificationScheduler.ID_WORKOUT_TIME,
                                        preset.hour,
                                        preset.minute,
                                        true
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isPresetActive) PlanInk else SoftSurfaceTint,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 46.dp)
                                    .testTag("preset_alarm_${preset.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 3.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = preset.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPresetActive) Color.White else PlanInk,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = preset.timeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isPresetActive) Color.White.copy(alpha = 0.85f) else TextSecondarySlate,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = PlanDivider)

                CustomReminderTimeRow(
                    title = "Main Free-Time Alarm",
                    subtitle = "Tap time to pick any exact hour & minute",
                    hourOfDay = profile.workoutReminderHour,
                    minute = profile.workoutReminderMinute,
                    enabled = profile.backgroundAlarmsArmed && profile.workoutReminderEnabled,
                    timeButtonTag = "btn_pick_time_workout",
                    onOpenExactTimePicker = {
                        activeTimePickerCheckpointId = ApexNotificationScheduler.ID_WORKOUT_TIME
                    },
                    onToggleEnabled = { next ->
                        onUpdateCustomReminder(
                            ApexNotificationScheduler.ID_WORKOUT_TIME,
                            profile.workoutReminderHour,
                            profile.workoutReminderMinute,
                            next
                        )
                    }
                )

                HorizontalDivider(color = PlanDivider)

                CustomReminderTimeRow(
                    title = "Morning Check-In",
                    subtitle = "Morning hydration & daily target reminder",
                    hourOfDay = profile.morningReminderHour,
                    minute = profile.morningReminderMinute,
                    enabled = profile.backgroundAlarmsArmed && profile.morningReminderEnabled,
                    timeButtonTag = "btn_pick_time_morning",
                    onOpenExactTimePicker = {
                        activeTimePickerCheckpointId = ApexNotificationScheduler.ID_MORNING_PROTEIN
                    },
                    onToggleEnabled = { next ->
                        onUpdateCustomReminder(
                            ApexNotificationScheduler.ID_MORNING_PROTEIN,
                            profile.morningReminderHour,
                            profile.morningReminderMinute,
                            next
                        )
                    }
                )

                HorizontalDivider(color = PlanDivider)

                CustomReminderTimeRow(
                    title = "Evening Reminder",
                    subtitle = "End-of-day check-in alert",
                    hourOfDay = profile.eveningReminderHour,
                    minute = profile.eveningReminderMinute,
                    enabled = profile.backgroundAlarmsArmed && profile.eveningReminderEnabled,
                    timeButtonTag = "btn_pick_time_evening",
                    onOpenExactTimePicker = {
                        activeTimePickerCheckpointId = ApexNotificationScheduler.ID_EVENING_RESET
                    },
                    onToggleEnabled = { next ->
                        onUpdateCustomReminder(
                            ApexNotificationScheduler.ID_EVENING_RESET,
                            profile.eveningReminderHour,
                            profile.eveningReminderMinute,
                            next
                        )
                    }
                )

                Surface(
                    onClick = {
                        val previewCheckpoint = ApexNotificationScheduler.DAILY_CHECKPOINTS[1].copy(
                            hourOfDay = profile.workoutReminderHour,
                            minute = profile.workoutReminderMinute,
                            timeLabel = ApexNotificationScheduler.formatTime12h(
                                profile.workoutReminderHour,
                                profile.workoutReminderMinute
                            )
                        )
                        onTestCheckpointAlarm(previewCheckpoint)
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = SoftSurfaceTint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 42.dp)
                        .testTag("btn_send_test_notification")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Send Test Notification",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PlanInk
                        )
                    }
                }
            }

            // 4. SOUND & HAPTICS (App Audio, Voice Coach Mode & Vibration)
            SettingsSectionGroup(
                sectionLabel = strings.soundAndHapticsTitle,
                testTag = "card_settings_sound_haptics"
            ) {
                WorkoutAudioCoachModeSelector(
                    selectedMode = profile.audioCoachMode,
                    onSelectMode = onSelectAudioCoachMode,
                    testTagPrefix = "settings_audio_mode"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsToggleRow(
                    icon = Icons.Default.VolumeUp,
                    title = strings.soundEffectsLabel,
                    subtitle = if (profile.voiceCoachEnabled) {
                        "Motivating rep counts, 30s encouragement & rest alerts on"
                    } else {
                        "Voice coach muted — music or silent mode"
                    },
                    checked = profile.voiceCoachEnabled,
                    onCheckedChange = onSetVoiceCoachEnabled,
                    switchTag = "switch_voice_coach"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    title = strings.vibrationLabel,
                    subtitle = if (profile.vibrationEnabled) {
                        "Tactile haptic pulses enabled"
                    } else {
                        "Off — no device vibration"
                    },
                    checked = profile.vibrationEnabled,
                    onCheckedChange = onSetVibrationEnabled,
                    switchTag = "switch_vibration_feedback"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsToggleRow(
                    icon = Icons.Default.Headphones,
                    title = "Auto-Start Workout Music",
                    subtitle = if (profile.autoPlayWorkoutMusic) {
                        "Starts motivating workout music automatically during sets"
                    } else {
                        "Manual — tap Play on the Music card anytime"
                    },
                    checked = profile.autoPlayWorkoutMusic,
                    onCheckedChange = onSetAutoPlayWorkoutMusic,
                    switchTag = "switch_auto_play_workout_music"
                )
            }

            // 5. LEGAL, MEDICAL SAFETY & PRIVACY (Play Store Consumer Transparency)
            SettingsSectionGroup(
                sectionLabel = "Legal & Safety",
                testTag = "card_settings_legal_safety"
            ) {
                SettingsActionRow(
                    icon = Icons.Default.Info,
                    title = "Health & Medical Disclaimer",
                    subtitle = "Safe training guidelines & physician consultation notice",
                    onClick = { showHealthDisclaimerDialog = true },
                    testTag = "btn_health_disclaimer"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsActionRow(
                    icon = Icons.Default.Lock,
                    title = "Privacy & On-Device Data Policy",
                    subtitle = "100% local storage • Zero cloud selling of personal stats",
                    onClick = { showPrivacyPolicyDialog = true },
                    testTag = "btn_privacy_policy"
                )
            }

            // 6. DATA & STORAGE (Clear Today's Logs, Restart Setup, Full Reset, App Info)
            SettingsSectionGroup(
                sectionLabel = strings.dataAndResetTitle,
                testTag = "card_settings_data_storage"
            ) {
                SettingsActionRow(
                    icon = Icons.Default.Refresh,
                    title = strings.clearTodayLogsLabel,
                    subtitle = "Reset today's logged protein & daily checkmarks",
                    onClick = onClearTodayDailyLogs,
                    testTag = "btn_clear_today_logs"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsActionRow(
                    icon = Icons.Default.Tune,
                    title = strings.restartSetupWizard,
                    subtitle = "Re-open the initial onboarding setup flow",
                    onClick = onRerunFullOnboardingWizard,
                    testTag = "btn_rerun_setup_wizard"
                )

                HorizontalDivider(color = PlanDivider)

                SettingsActionRow(
                    icon = Icons.Default.DeleteOutline,
                    title = if (confirmFullResetArmed) {
                        "Tap Again to Confirm Full Reset"
                    } else {
                        strings.resetAllProgressLabel
                    },
                    subtitle = if (confirmFullResetArmed) {
                        "Clears all saved check-ins and completed history"
                    } else {
                        "Erase local history and start fresh from Day 1"
                    },
                    tintColor = if (confirmFullResetArmed) DestructiveRed else PlanInk,
                    onClick = {
                        if (confirmFullResetArmed) {
                            confirmFullResetArmed = false
                            onResetAllAppProgress()
                        } else {
                            confirmFullResetArmed = true
                        }
                    },
                    testTag = "btn_reset_all_progress"
                )
            }

            // Clean App Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Apex Protocol v1.0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondarySlate
                )
                Text(
                    text = "Offline-First Local Storage • No Account Required",
                    fontSize = 11.sp,
                    color = TextSecondarySlate.copy(alpha = 0.8f)
                )
            }
        } else {
            // =========================================================================
            // SUB-SCREEN: DEDICATED "MY PLAN" CONFIGURATION
            // =========================================================================

            // Clean Back Bar: [ <- Settings ]   My Plan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = {
                        activeTab = 0
                        onTabChanged(0)
                    },
                    shape = CircleShape,
                    color = PureWhiteCard,
                    border = BorderStroke(1.dp, PlanHairline),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_back_to_settings")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.settingsTitle,
                            tint = PlanInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "Back to ${strings.settingsTitle}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlanInk
                    )
                    Text(
                        text = "Body goal, honest timeline & session setup",
                        fontSize = 11.sp,
                        color = TextSecondarySlate
                    )
                }
            }

            StudioPageBannerHeader(
                imageResId = WorkoutCoverResolver.bannerForOnboardingStep(5, profile.biologicalSex),
                eyebrowBadge = "PERSONAL BLUEPRINT",
                title = strings.tabMyPlan,
                subtitle = "${currentTargetObj.titleFor(profile.biologicalSex)}  •  ${profile.workoutIntensity.label} Mode",
                bannerHeightDp = 124,
                trailingBadgeText = "${profile.weeklyFrequency.daysCount}d / wk"
            )

            // 1. Active Body Target & Honest Un-Sugarcoated Timeline
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Body & Target Goal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanInk
                        )
                        Text(
                            text = if (showBodyPicker) "Done" else "Edit Body / Goal",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanInk,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showBodyPicker = !showBodyPicker }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("btn_toggle_body_goal_picker")
                        )
                    }

                    // Quick Biological Sex Switcher (syncs brackets + split immediately)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            BiologicalSex.MALE to "Male Blueprint",
                            BiologicalSex.FEMALE to "Female Blueprint"
                        ).forEach { (sexOpt, label) ->
                            val isSelectedSex = profile.biologicalSex == sexOpt
                            Surface(
                                onClick = {
                                    onSelectBiologicalSex(sexOpt)
                                    val targetFemale = sexOpt == BiologicalSex.FEMALE
                                    val syncedStartBracket = if (targetFemale) {
                                        currentStartObj.currentBracketFemale
                                    } else {
                                        currentStartObj.currentBracketMale
                                    }
                                    val syncedGoalBracket = if (targetFemale) {
                                        currentTargetObj.goalBracketFemale
                                    } else {
                                        currentTargetObj.goalBracketMale
                                    }
                                    onSelectCurrentBracket(syncedStartBracket)
                                    onSelectGoalBracket(syncedGoalBracket)
                                    onUpdateSelectedShapeCardIds(currentStartObj.id, currentTargetObj.id)
                                    onSelectVisualStartingBody(currentStartObj.id, syncedStartBracket, currentTargetObj.id)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelectedSex) PlanInk else InnerFrameSurface,
                                border = BorderStroke(1.dp, if (isSelectedSex) PlanInk else InnerFrameBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag(
                                        if (sexOpt == BiologicalSex.MALE) "chip_my_plan_sex_male"
                                        else "chip_my_plan_sex_female"
                                    )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelectedSex) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSelectedSex) Color.White else PlanInk
                                    )
                                }
                            }
                        }
                    }

                    val activeBlueprint = com.example.domain.CombatSplitCatalog.describeActiveBlueprint(
                        sex = profile.biologicalSex,
                        startingShapeId = currentStartObj.id,
                        goalCardId = currentTargetObj.id,
                        currentBracket = report.currentBracket,
                        goalBracket = report.goalBracket,
                        environment = profile.trainingEnvironment
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = InnerFrameSurface,
                        border = BorderStroke(1.dp, InnerFrameBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = activeBlueprint.repSchemeBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PlanInk
                            )
                            Text(
                                text = activeBlueprint.coachingSummary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }

                    val startLabel = if (profile.measurementsKnown) {
                        "${UnitFormatter.formatWeightRounded(report.currentWeightKg, profile.useMetricUnits)} • ${currentStartObj.simpleTitle}"
                    } else {
                        currentStartObj.simpleTitle
                    }
                    val goalLabel = if (profile.measurementsKnown) {
                        "${UnitFormatter.formatWeightRounded(report.targetBodyweightKg, profile.useMetricUnits)} • ${currentTargetObj.titleFor(profile.biologicalSex)}"
                    } else {
                        currentTargetObj.titleFor(profile.biologicalSex)
                    }

                    TransformationSilhouetteComparisonBanner(
                        currentBracket = report.currentBracket,
                        goalBracket = report.goalBracket,
                        sex = profile.biologicalSex,
                        startPresetOverride = PhysiqueSilhouettePreset.fromCardId(currentStartObj.id),
                        goalPresetOverride = PhysiqueSilhouettePreset.fromCardId(currentTargetObj.id),
                        startTitleOverride = startLabel,
                        goalTitleOverride = goalLabel,
                        compactMode = true
                    )

                    HorizontalDivider(color = PlanDivider)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FramedStatCapsule(
                            label = "CALORIES",
                            value = "${report.caloricDeficitCeilingKcal}",
                            subTag = "kcal / day",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "PROTEIN",
                            value = "${report.dailyProteinFloorGrams}g",
                            subTag = "daily floor",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "HONEST TIME",
                            value = report.honestTimeline.monthsRangeLabel,
                            subTag = "real pace",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftSurfaceTint,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = report.honestTimeline.honestTruthHeadline,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PlanInk
                            )
                            Text(
                                text = report.honestTimeline.honestTruthBody,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }

                    if (showBodyPicker) {
                        HorizontalDivider(color = PlanDivider)
                        Text(
                            text = "Current Body (All 6 Realistic Types)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanInk
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            startOptions.forEach { startOpt ->
                                val selected = currentStartObj.id == startOpt.id
                                Surface(
                                    onClick = {
                                        val bracket = if (isFemale) startOpt.currentBracketFemale else startOpt.currentBracketMale
                                        onSelectCurrentBracket(bracket)
                                        onUpdateSelectedShapeCardIds(startOpt.id, currentTargetObj.id)
                                        onSelectVisualStartingBody(startOpt.id, bracket, currentTargetObj.id)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) PlanInk else InnerFrameSurface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                        Text(
                                            text = startOpt.simpleTitle,
                                            fontSize = 13.sp,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (selected) Color.White else PlanInk
                                        )
                                        Text(
                                            text = startOpt.plainCheck,
                                            fontSize = 11.sp,
                                            color = if (selected) Color.White.copy(alpha = 0.85f) else TextSecondarySlate
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Target Goal (Choose Any Goal Directly)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanInk
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            targetOptions.forEach { goalOpt ->
                                val selected = currentTargetObj.id == goalOpt.id
                                Surface(
                                    onClick = {
                                        onSelectGoalBracket(if (isFemale) goalOpt.goalBracketFemale else goalOpt.goalBracketMale)
                                        onUpdateSelectedShapeCardIds(currentStartObj.id, goalOpt.id)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) PlanInk else InnerFrameSurface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                                        Text(
                                            text = goalOpt.titleFor(profile.biologicalSex),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else PlanInk
                                        )
                                        Text(
                                            text = goalOpt.musclesFor(profile.biologicalSex),
                                            fontSize = 11.sp,
                                            color = if (selected) Color.White.copy(alpha = 0.85f) else TextSecondarySlate
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Optional Body Stats (Weight & Height)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Scale Weight & Height (Optional)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                            Text(
                                text = if (profile.measurementsKnown) {
                                    "Using your entered body stats"
                                } else {
                                    "Skipped — calibrated safely from your body type"
                                },
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                        Switch(
                            checked = profile.measurementsKnown,
                            onCheckedChange = onSetMeasurementsKnown,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhiteCard,
                                checkedTrackColor = PlanInk,
                                uncheckedThumbColor = TextSecondarySlate,
                                uncheckedTrackColor = SoftSurfaceTint
                            )
                        )
                    }

                    if (profile.measurementsKnown) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatStepperCard(
                                label = "WEIGHT",
                                valueText = UnitFormatter.formatWeightRounded(profile.bodyweightKg, profile.useMetricUnits),
                                onMinus = { onUpdateBiometrics((profile.bodyweightKg - 1f).coerceAtLeast(40f), profile.heightCm, profile.age) },
                                onPlus = { onUpdateBiometrics((profile.bodyweightKg + 1f).coerceAtMost(180f), profile.heightCm, profile.age) },
                                modifier = Modifier.weight(1f)
                            )
                            StatStepperCard(
                                label = "HEIGHT",
                                valueText = UnitFormatter.formatHeight(profile.heightCm, profile.useMetricUnits),
                                onMinus = { onUpdateBiometrics(profile.bodyweightKg, (profile.heightCm - 1f).coerceAtLeast(140f), profile.age) },
                                onPlus = { onUpdateBiometrics(profile.bodyweightKg, (profile.heightCm + 1f).coerceAtMost(215f), profile.age) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Weekly Schedule & Rest Days
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Weekly Schedule & Rest Days",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlanInk
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8EDF3),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                TrainingEnvironment.GYM to Pair("Full Gym", Icons.Default.FitnessCenter),
                                TrainingEnvironment.HOME to Pair("At Home", Icons.Default.Home)
                            ).forEach { (env, pair) ->
                                val selected = profile.trainingEnvironment == env
                                Surface(
                                    onClick = { onSelectTrainingEnvironment(env) },
                                    shape = RoundedCornerShape(9.dp),
                                    color = if (selected) PureWhiteCard else Color.Transparent,
                                    shadowElevation = if (selected) 1.dp else 0.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = pair.second,
                                            contentDescription = null,
                                            tint = if (selected) PlanInk else TextSecondarySlate,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = pair.first,
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) PlanInk else TextSecondarySlate
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val frequencyAdvice = CoachTruthAndSplitAdvisor.adviseBestFrequency(
                        direction = report.metabolicDirection,
                        experience = profile.experienceLevel,
                        selectedFrequency = profile.weeklyFrequency
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WeeklyFrequency.entries.forEach { freq ->
                            val selected = profile.weeklyFrequency == freq
                            val isBest = freq == frequencyAdvice.recommendedFrequency
                            Surface(
                                onClick = { onSelectWeeklyFrequency(freq) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) PlanInk else SoftSurfaceTint,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isBest) "${freq.daysCount}d • Ideal" else "${freq.daysCount} Days",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else PlanInk
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PlanDivider)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val todayDate = LocalDate.now()
                        val matchedOpt = PlanStartOption.matchOptionOrNull(profile.planStartDateIso, todayDate)
                        val isCustomPickedDate = matchedOpt == null || matchedOpt == PlanStartOption.START_MONDAY
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PLAN START POINT (DAY 1)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondarySlate,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = PlanStartOption.formatPlanStartSummary(profile.planStartDateIso, todayDate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D63ED)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isTodaySel = matchedOpt == PlanStartOption.START_TODAY
                            Surface(
                                onClick = { onSelectPlanStartOption(PlanStartOption.START_TODAY) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isTodaySel) PlanInk else SoftSurfaceTint,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("my_plan_start_start_today")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Today",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTodaySel) Color.White else PlanInk,
                                        maxLines = 1
                                    )
                                }
                            }

                            val isTomSel = matchedOpt == PlanStartOption.START_TOMORROW
                            Surface(
                                onClick = { onSelectPlanStartOption(PlanStartOption.START_TOMORROW) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isTomSel) PlanInk else SoftSurfaceTint,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("my_plan_start_start_tomorrow")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Tomorrow",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTomSel) Color.White else PlanInk,
                                        maxLines = 1
                                    )
                                }
                            }

                            Surface(
                                onClick = { showMyPlanStartDatePicker = true },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCustomPickedDate) PlanInk else SoftSurfaceTint,
                                border = if (isCustomPickedDate) null else BorderStroke(1.dp, PlanHairline),
                                modifier = Modifier
                                    .weight(1.25f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("btn_my_plan_pick_start_date")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pick start date",
                                        tint = if (isCustomPickedDate) Color.White else PlanInk,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isCustomPickedDate) {
                                            PlanStartOption.formatShortCustomDateLabel(profile.planStartDateIso, todayDate)
                                        } else {
                                            "Pick Date"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCustomPickedDate) Color.White else PlanInk,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PlanDivider)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FREE TRAINING DAYS (TAP TO TOGGLE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondarySlate,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = WeeklySchedulePlanner.formatTrainDaysSummary(profile.restDaysIso),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            uiState.weekSchedule.forEach { slot ->
                                val isTrainDay = !slot.isRestDay && !slot.isBeforePlanStart
                                Surface(
                                    onClick = { onToggleRestDay(slot.dayOfWeekIso) },
                                    shape = RoundedCornerShape(9.dp),
                                    color = when {
                                        slot.isBeforePlanStart -> SoftSurfaceTint.copy(alpha = 0.6f)
                                        isTrainDay -> PlanInk
                                        else -> SoftSurfaceTint
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 46.dp)
                                        .testTag("my_plan_rest_chip_${slot.dayOfWeekIso}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = slot.shortLabel.lowercase().replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTrainDay) Color.White else PlanInk
                                        )
                                        Text(
                                            text = slot.shortPillTag,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isTrainDay) Color.White.copy(alpha = 0.85f) else TextSecondarySlate,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PlanDivider)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "HAVE YOU TRAINED BEFORE?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondarySlate,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ExperienceLevel.entries.forEach { lvl ->
                                val selected = profile.experienceLevel == lvl
                                Surface(
                                    onClick = { onSelectExperienceLevel(lvl) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) PlanInk else SoftSurfaceTint,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                                        Text(
                                            text = lvl.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else PlanInk,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = profile.experienceLevel.shortTag,
                            fontSize = 11.sp,
                            color = TextSecondarySlate
                        )
                    }

                    HorizontalDivider(color = PlanDivider)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "4-WEEK PROGRESSIVE TRACK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondarySlate,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (isFemale) "Profile: Female ↻" else "Profile: Male ↻",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        onSelectBiologicalSex(if (isFemale) BiologicalSex.MALE else BiologicalSex.FEMALE)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (1..4).forEach { wk ->
                                val wkSelected = uiState.fourWeekTrack.activeViewedWeek == wk
                                Surface(
                                    onClick = { onSelectRotationWeek(wk) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (wkSelected) PlanInk else SoftSurfaceTint,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "Week $wk",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (wkSelected) Color.White else PlanInk
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = uiState.fourWeekTrack.weekRuleSubtitle,
                            fontSize = 11.sp,
                            color = TextSecondarySlate
                        )
                    }
                }
            }

            // 4. Workout Session Volume & Timing (Inside My Plan — NOT in App Settings!)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                    .testTag("card_my_plan_session_volume")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = strings.dailyExerciseDoseTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlanInk
                        )
                        Text(
                            text = "Customize session length, rest between sets & warm-up",
                            fontSize = 12.sp,
                            color = TextSecondarySlate
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WorkoutIntensityMode.entries.forEach { mode ->
                            val selected = profile.workoutIntensity == mode
                            Surface(
                                onClick = {
                                    onSelectWorkoutIntensity(mode)
                                    onSelectExercisesPerDay(mode.exercisesCount)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) PlanInk else InnerFrameSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 50.dp)
                                    .testTag("dose_option_${mode.exercisesCount}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(1.dp)
                                    ) {
                                        Text(
                                            text = "${mode.label}  •  ~${mode.estMinutes} min  •  ~${mode.baseKcalBurn} kcal",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selected) Color.White else PlanInk
                                        )
                                        Text(
                                            text = mode.summaryLine,
                                            fontSize = 11.sp,
                                            color = if (selected) Color.White.copy(alpha = 0.82f) else TextSecondarySlate
                                        )
                                    }
                                    Text(
                                        text = "${mode.exercisesCount}/day",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (selected) Color.White else PlanInk
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PlanDivider)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = strings.restBetweenSets.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondarySlate,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(45, 60, 90, 120).forEach { sec ->
                                val selected = profile.restDurationSeconds == sec
                                Surface(
                                    onClick = { onSelectRestDurationSeconds(sec) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) PlanInk else SoftSurfaceTint,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("rest_sec_$sec")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${sec}s",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else PlanInk
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PlanDivider)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = strings.warmupAndStretch,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                            Text(
                                text = if (profile.includeWarmupAndStretch) {
                                    "Includes warm-up & post-workout stretch"
                                } else {
                                    "Skipped — starts directly with main exercises"
                                },
                                fontSize = 12.sp,
                                color = TextSecondarySlate
                            )
                        }
                        Switch(
                            checked = profile.includeWarmupAndStretch,
                            onCheckedChange = onSetIncludeWarmupAndStretch,
                            modifier = Modifier.testTag("switch_include_warmup_stretch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhiteCard,
                                checkedTrackColor = PlanInk,
                                uncheckedThumbColor = TextSecondarySlate,
                                uncheckedTrackColor = SoftSurfaceTint
                            )
                        )
                    }
                }
            }

            // 5. Honest Training & Recovery Principles
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                    .testTag("card_my_plan_reality_truths")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Honest Training & Recovery Principles",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlanInk
                    )

                    CoachTruthAndSplitAdvisor.REALITY_TRUTHS.forEachIndexed { idx, truth ->
                        HorizontalDivider(color = PlanDivider)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${idx + 1}. ${truth.headline}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlanInk
                            )
                            Text(
                                text = truth.realityBody,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = TextSecondarySlate
                            )
                        }
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SettingsSectionGroup(
    sectionLabel: String,
    testTag: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = sectionLabel.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondarySlate,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, PlanHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                .testTag(testTag)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsLeadingIcon(
    icon: ImageVector,
    tint: Color = PlanInk
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SoftSurfaceTint,
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            SettingsLeadingIcon(icon)
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlanInk
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(switchTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhiteCard,
                checkedTrackColor = PlanInk,
                uncheckedThumbColor = TextSecondarySlate,
                uncheckedTrackColor = SoftSurfaceTint
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    tintColor: Color = PlanInk
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            SettingsLeadingIcon(icon = icon, tint = tintColor)
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = tintColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondarySlate,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun QuickPlanStatColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
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
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PlanInk,
            maxLines = 1
        )
    }
}

@Composable
private fun CustomReminderTimeRow(
    title: String,
    subtitle: String,
    hourOfDay: Int,
    minute: Int,
    enabled: Boolean,
    timeButtonTag: String,
    onOpenExactTimePicker: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .clickable { onOpenExactTimePicker() },
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) PlanInk else TextSecondarySlate
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondarySlate
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onOpenExactTimePicker,
                shape = RoundedCornerShape(10.dp),
                color = if (enabled) PlanInk else SoftSurfaceTint,
                border = if (enabled) null else BorderStroke(1.dp, PlanHairline),
                modifier = Modifier
                    .defaultMinSize(minWidth = 98.dp, minHeight = 42.dp)
                    .testTag(timeButtonTag)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Pick exact time",
                        tint = if (enabled) Color.White else PlanInk,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = ApexNotificationScheduler.formatTime12h(hourOfDay, minute),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (enabled) Color.White else PlanInk
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggleEnabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureWhiteCard,
                    checkedTrackColor = PlanInk,
                    uncheckedThumbColor = TextSecondarySlate,
                    uncheckedTrackColor = SoftSurfaceTint
                )
            )
        }
    }
}
