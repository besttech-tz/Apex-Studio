package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.system.WorkoutMusicEngine
import com.example.ui.ApexNavDestination
import com.example.ui.ApexRoutes
import com.example.ui.ApexViewModel
import com.example.ui.WorkoutOriginPage
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.stringsForLanguage
import com.example.ui.screens.CalibrationGapScreen
import com.example.ui.screens.CombatDeckScreen
import com.example.ui.screens.FoodIntelligenceScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.PlanCalibrationHubScreen
import com.example.ui.screens.ReportProgressScreen
import com.example.ui.theme.ApexProtocolTheme
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.TextSecondarySlate

private object ApexEngineRoutes {
    const val ROUTE_DASHBOARD = ApexRoutes.ROUTE_HOME_DASHBOARD
    const val ROUTE_ACTIVE_SESSION = ApexRoutes.ROUTE_ACTIVE_WORKOUT_COACH
    const val ROUTE_FOOD_GUIDE = "food_guide"
    const val ROUTE_PROGRESS_LEDGER = "progress_ledger"
    const val ROUTE_SETTINGS_HUB = ApexRoutes.ROUTE_BODY_CALIBRATION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApexProtocolTheme {
                val context = LocalContext.current
                val viewModel: ApexViewModel = viewModel(
                    factory = ApexViewModel.Factory(context)
                )
                ApexProtocolApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        WorkoutMusicEngine.stop()
        super.onDestroy()
    }
}

@Composable
fun ApexProtocolApp(
    viewModel: ApexViewModel
) {
    val context = LocalContext.current
    val onboardingPrefs = remember(context) {
        context.getSharedPreferences("apex_onboarding_studio_v12", Context.MODE_PRIVATE)
    }
    var hasAcceptedProtocol by remember {
        mutableStateOf(onboardingPrefs.getBoolean("profile_accepted", false))
    }

    LaunchedEffect(Unit) {
        com.example.system.ExerciseAnimationMediaResolver.prefetchAllExercises(context)
        if (!onboardingPrefs.getBoolean("fresh_slate_seeded_v12", false)) {
            onboardingPrefs.edit().putBoolean("fresh_slate_seeded_v12", true).apply()
            if (!hasAcceptedProtocol) {
                viewModel.resetForFreshBeginning()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val restTimerState by viewModel.restTimerState.collectAsStateWithLifecycle()
    val progressionOffsets by viewModel.exerciseProgressionOffsets.collectAsStateWithLifecycle()
    val settingsInitialTab by viewModel.settingsInitialTab.collectAsStateWithLifecycle()

    val appStrings = remember(uiState.profile.appLanguageCode) {
        stringsForLanguage(AppLanguage.fromCode(uiState.profile.appLanguageCode))
    }

    // Apply App Setting: Keep Screen On while using the application
    LaunchedEffect(uiState.profile.keepScreenAwake) {
        val window = (context as? Activity)?.window
        if (uiState.profile.keepScreenAwake) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    var pendingReminderAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
        val action = pendingReminderAction
        pendingReminderAction = null
        action?.invoke()
    }

    fun runWithNotificationPermissionIfNeeded(onProceed: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            pendingReminderAction = onProceed
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onProceed()
        }
    }

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        // =========================================================================
        // FIRST-LAUNCH INSTALLATION FLOW
        // =========================================================================
        if (!hasAcceptedProtocol) {
            CalibrationGapScreen(
                uiState = uiState,
                onSelectBiologicalSex = viewModel::selectBiologicalSex,
                onSelectAgeTier = viewModel::selectAgeTier,
                onSelectExperienceLevel = viewModel::selectExperienceLevel,
                onSetMeasurementsKnown = viewModel::setMeasurementsKnown,
                onSelectVisualStartingBody = viewModel::selectVisualStartingBody,
                onSelectWeeklyFrequency = viewModel::selectWeeklyFrequency,
                onToggleRestDay = viewModel::toggleRestDay,
                onSelectTrainingEnvironment = viewModel::selectTrainingEnvironment,
                onUpdateUserFirstName = viewModel::updateUserFirstName,
                onSelectPlanStartOption = viewModel::selectPlanStartOption,
                onSelectCustomStartDate = viewModel::selectCustomPlanStartDate,
                onSelectFreeTimeReminder = { hour, minute, enabled ->
                    if (enabled) {
                        runWithNotificationPermissionIfNeeded {
                            viewModel.updateCustomReminder(
                                com.example.system.ApexNotificationScheduler.ID_WORKOUT_TIME,
                                hour,
                                minute,
                                true
                            )
                        }
                    } else {
                        viewModel.updateCustomReminder(
                            com.example.system.ApexNotificationScheduler.ID_WORKOUT_TIME,
                            hour,
                            minute,
                            false
                        )
                        viewModel.toggleBackgroundAlarms(false)
                    }
                },
                onPickSpecimenPhoto = { uri -> viewModel.onPhotoPicked(1, uri) },
                onPickBenchmarkPhoto = { uri -> viewModel.onPhotoPicked(2, uri) },
                onClearPhotoSlot = viewModel::clearPickedPhoto,
                onSelectCurrentBracket = viewModel::selectCurrentBfBracket,
                onSelectGoalBracket = viewModel::selectGoalBfBracket,
                onUpdateSelectedShapeCardIds = viewModel::updateSelectedShapeCardIds,
                onUpdateBiometrics = viewModel::updateBiometrics,
                onToggleWeakpoint = viewModel::toggleWeakpoint,
                onLockInProtocolAndViewWorkout = {
                    viewModel.ensurePlanStartInitializedOnOnboardingLock()
                    onboardingPrefs.edit().putBoolean("profile_accepted", true).apply()
                    hasAcceptedProtocol = true
                    viewModel.selectCombatDay(0)
                    viewModel.selectDestination(ApexNavDestination.HOME_DASHBOARD)
                }
            )
            return@CompositionLocalProvider
        }

        // =========================================================================
        // MAIN 5-TAB APPLICATION SHELL: [ Dashboard | Workout | Food Guide | Progress | Settings ]
        // =========================================================================
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: ApexEngineRoutes.ROUTE_DASHBOARD

        fun navigateToRoute(route: String) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBackground),
            containerColor = HomeBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                ApexEngineBottomNavigationDock(
                    currentRoute = currentRoute,
                    onSelectRoute = { route ->
                        when (route) {
                            ApexEngineRoutes.ROUTE_DASHBOARD -> {
                                WorkoutMusicEngine.stop()
                                viewModel.selectDestination(ApexNavDestination.HOME_DASHBOARD)
                            }
                            ApexEngineRoutes.ROUTE_ACTIVE_SESSION ->
                                viewModel.openWorkoutHub()
                            ApexEngineRoutes.ROUTE_SETTINGS_HUB -> {
                                viewModel.setSettingsInitialTab(0)
                                viewModel.selectDestination(ApexNavDestination.CALIBRATION)
                            }
                        }
                        navigateToRoute(route)
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ApexEngineRoutes.ROUTE_DASHBOARD,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .background(HomeBackground)
            ) {
                // 1. DASHBOARD
                composable(ApexEngineRoutes.ROUTE_DASHBOARD) {
                    HomeDashboardScreen(
                        uiState = uiState,
                        restTimerState = restTimerState,
                        progressionOffsets = progressionOffsets,
                        onSelectTrainingEnvironment = viewModel::selectTrainingEnvironment,
                        onSelectWorkoutIntensity = viewModel::selectWorkoutIntensity,
                        onToggleRestDay = viewModel::toggleRestDay,
                        onSelectRotationWeek = viewModel::selectRotationWeek,
                        onOpenWorkoutHubClick = {
                            viewModel.openWorkoutHub()
                            navigateToRoute(ApexEngineRoutes.ROUTE_ACTIVE_SESSION)
                        },
                        onTogglePreWaterElectrolyte = viewModel::togglePreWaterElectrolyte,
                        onTogglePreProteinCarb = viewModel::togglePreProteinCarb,
                        onTogglePreEquipmentScouted = viewModel::togglePreEquipmentScouted,
                        onToggleSetCompleted = viewModel::onSetClicked,
                        onStartNinetySecRest = { exerciseName, setNumber ->
                            viewModel.startNinetySecondRestEngine(
                                exerciseName = exerciseName,
                                setNumber = setNumber
                            )
                        },
                        onAdjustRestMillis = viewModel::adjustRestTimerMillis,
                        onForceZeroRestAlarm = viewModel::forceCompleteRestTimerToZero,
                        onDismissRestTimer = viewModel::dismissRestTimer,
                        onTogglePostWorkoutProtein40g = viewModel::togglePostWorkoutProtein40g,
                        onStartWorkoutClick = {
                            viewModel.openScheduledWorkoutBriefing(origin = WorkoutOriginPage.DASHBOARD)
                            navigateToRoute(ApexEngineRoutes.ROUTE_ACTIVE_SESSION)
                        },
                        onStartQuickStrikeWorkout = { qs ->
                            viewModel.startQuickStrikeWorkout(qs, origin = WorkoutOriginPage.DASHBOARD)
                            navigateToRoute(ApexEngineRoutes.ROUTE_ACTIVE_SESSION)
                        },
                        onResumeMidwayWorkout = {
                            viewModel.resumeMidwayWorkout(origin = WorkoutOriginPage.DASHBOARD)
                            navigateToRoute(ApexEngineRoutes.ROUTE_ACTIVE_SESSION)
                        },
                        onRestartMidwayWorkout = {
                            viewModel.restartMidwayWorkoutFresh(origin = WorkoutOriginPage.DASHBOARD)
                            navigateToRoute(ApexEngineRoutes.ROUTE_ACTIVE_SESSION)
                        },
                        onCompleteMidwayWorkout = {
                            viewModel.completeMidwayWorkoutNow()
                        },
                        onOpenFoodGuideClick = {
                            navigateToRoute(ApexEngineRoutes.ROUTE_FOOD_GUIDE)
                        },
                        onRecalibrateClick = {
                            viewModel.setSettingsInitialTab(0)
                            viewModel.selectDestination(ApexNavDestination.CALIBRATION)
                            navigateToRoute(ApexEngineRoutes.ROUTE_SETTINGS_HUB)
                        },
                        onSelectCombatDay = viewModel::selectCombatDay,
                        onAddProteinGrams = viewModel::addProteinGrams,
                        onLogMealProtein = viewModel::logMealProtein,
                        onAddFoodItemProtein = viewModel::addFoodItemProtein,
                        onSelectSupplementPreference = viewModel::selectSupplementPreference,
                        onToggleCreatine5g = viewModel::toggleCreatine5g,
                        onAddWater = viewModel::addWater500ml,
                        onAddWaterMl = viewModel::addWaterMl,
                        onResetWater = viewModel::resetWater,
                        onResetProteinGrams = viewModel::resetDailyNutrition,
                        onToggleSteps10k = viewModel::toggleSteps10k,
                        onToggleZeroLiquidSugar = viewModel::toggleZeroSugarZeroFried,
                        onToggleZeroCandy = viewModel::toggleZeroCandySweets,
                        onToggleZeroFriedJunk = viewModel::toggleZeroFriedJunk,
                        onToggleKitchenClosed8pm = viewModel::toggleKitchenClosedBy8pm,
                        onDefeatCraving = viewModel::recordCravingDefeated,
                        onToggleBackgroundAlarms = viewModel::toggleBackgroundAlarms,
                        onTestCheckpointAlarm = viewModel::testFireCheckpointDispatch,
                        onSubmitWeeklyAudit = viewModel::submitWeeklyAudit,
                        onSimulateStagnation = viewModel::simulateStagnationScenario,
                        onLogProgressiveDrop = viewModel::logProgressiveDropScenario
                    )
                }

                // 2. WORKOUT (BRIEFING -> ACTIVE COACH -> RECOVERY)
                composable(ApexEngineRoutes.ROUTE_ACTIVE_SESSION) {
                    CombatDeckScreen(
                        uiState = uiState,
                        restTimerState = restTimerState,
                        progressionOffsets = progressionOffsets,
                        onSelectDay = viewModel::selectCombatDay,
                        onSelectTrainingEnvironment = viewModel::selectTrainingEnvironment,
                        onSelectWorkoutIntensity = viewModel::selectWorkoutIntensity,
                        onSelectAudioCoachMode = viewModel::selectAudioCoachMode,
                        onToggleRestDay = viewModel::toggleRestDay,
                        onSelectRotationWeek = viewModel::selectRotationWeek,
                        onStartQuickStrikeFromHub = { track ->
                            viewModel.startQuickStrikeWorkout(track, origin = WorkoutOriginPage.WORKOUT_HUB)
                        },
                        onOpenScheduledBriefingFromHub = { idx ->
                            viewModel.openScheduledWorkoutBriefing(idx, origin = WorkoutOriginPage.WORKOUT_HUB)
                        },
                        onReturnToWorkoutHub = viewModel::returnToWorkoutHub,
                        onSelectExperienceLevel = viewModel::selectExperienceLevel,
                        onToggleSet = viewModel::onSetClicked,
                        onLogSetAndRest = viewModel::logNextSetAndStartRest,
                        onStartManualRest = {
                            viewModel.startNinetySecondRestEngine(
                                exerciseName = uiState.activeCombatDay.exercises.first().name,
                                setNumber = 1
                            )
                        },
                        onRecordSetEffortAndProgression = viewModel::recordSetEffortAndProgression,
                        onAdjustRestMillis = viewModel::adjustRestTimerMillis,
                        onForceZeroRestAlarm = viewModel::forceCompleteRestTimerToZero,
                        onDismissRestTimer = viewModel::dismissRestTimer,
                        onResetDay = viewModel::resetActiveCombatDay,
                        onScheduledWorkoutCompleted = viewModel::completeScheduledDayAndAdvance,
                        onRescueWorkoutCompleted = viewModel::recordQuickRescueCompleted,
                        onQuickPackOrChallengeCompleted = viewModel::recordQuickPackOrChallengeCompleted,
                        onSaveMidwayWorkout = viewModel::saveMidwayWorkout,
                        onClearMidwayWorkout = viewModel::clearMidwayWorkout,
                        onResumeMidwayFromHub = {
                            viewModel.resumeMidwayWorkout(origin = WorkoutOriginPage.WORKOUT_HUB)
                        },
                        onRestartMidwayFromHub = {
                            viewModel.restartMidwayWorkoutFresh(origin = WorkoutOriginPage.WORKOUT_HUB)
                        },
                        onCompleteMidwayFromHub = {
                            viewModel.completeMidwayWorkoutNow()
                        },
                        onNavigateBackHome = {
                            viewModel.clearQuickStrikeWorkout()
                            viewModel.selectDestination(ApexNavDestination.HOME_DASHBOARD)
                            navigateToRoute(ApexEngineRoutes.ROUTE_DASHBOARD)
                        }
                    )
                }

                // 3. VISUAL FOOD GUIDE
                composable(ApexEngineRoutes.ROUTE_FOOD_GUIDE) {
                    FoodIntelligenceScreen(
                        uiState = uiState,
                        onAddFoodItemProtein = viewModel::addFoodItemProtein,
                        onAddWaterMl = viewModel::addWaterMl,
                        onResetWater = viewModel::resetWater
                    )
                }

                // 4. PROGRESS & WEEKLY CHECK-IN
                composable(ApexEngineRoutes.ROUTE_PROGRESS_LEDGER) {
                    ReportProgressScreen(
                        uiState = uiState,
                        onAddProteinGrams = viewModel::addProteinGrams,
                        onResetProteinGrams = { viewModel.setProteinGrams(0) },
                        onToggleSteps10k = viewModel::toggleSteps10k,
                        onToggleZeroLiquidSugar = viewModel::toggleZeroSugarZeroFried,
                        onToggleZeroCandy = viewModel::toggleZeroCandySweets,
                        onToggleZeroFriedJunk = viewModel::toggleZeroFriedJunk,
                        onToggleKitchenClosed8pm = viewModel::toggleKitchenClosedBy8pm,
                        onDefeatCraving = viewModel::recordCravingDefeated,
                        onSubmitWeeklyAudit = viewModel::submitWeeklyAudit,
                        onSubmitWeeklyCheckIn = viewModel::submitWeeklyCheckIn,
                        onSimulateStagnation = viewModel::simulateStagnationScenario,
                        onLogProgressiveDrop = viewModel::logProgressiveDropScenario,
                        onOpenPlanSettings = {
                            viewModel.setSettingsInitialTab(1)
                            viewModel.selectDestination(ApexNavDestination.CALIBRATION)
                            navigateToRoute(ApexEngineRoutes.ROUTE_SETTINGS_HUB)
                        }
                    )
                }

                // 5. DEDICATED APPLICATION SETTINGS TAB (+ MY PLAN SUB-SCREEN)
                composable(ApexEngineRoutes.ROUTE_SETTINGS_HUB) {
                    PlanCalibrationHubScreen(
                        uiState = uiState,
                        initialTab = settingsInitialTab,
                        onTabChanged = viewModel::setSettingsInitialTab,
                        onSelectAppLanguage = viewModel::selectAppLanguage,
                        onSetUseMetricUnits = viewModel::setUseMetricUnits,
                        onSetKeepScreenAwake = viewModel::setKeepScreenAwake,
                        onSetVibrationEnabled = viewModel::setVibrationEnabled,
                        onClearTodayDailyLogs = viewModel::clearTodayDailyLogs,
                        onResetAllAppProgress = {
                            viewModel.resetAllAppProgress()
                            onboardingPrefs.edit().putBoolean("profile_accepted", false).apply()
                            hasAcceptedProtocol = false
                        },
                        onSetMeasurementsKnown = viewModel::setMeasurementsKnown,
                        onSelectVisualStartingBody = viewModel::selectVisualStartingBody,
                        onSelectExercisesPerDay = viewModel::selectExercisesPerDay,
                        onSelectWorkoutIntensity = viewModel::selectWorkoutIntensity,
                        onSelectRestDurationSeconds = viewModel::selectRestDurationSeconds,
                        onSetIncludeWarmupAndStretch = viewModel::setIncludeWarmupAndStretch,
                        onSetVoiceCoachEnabled = viewModel::setVoiceCoachEnabled,
                        onSetAutoPlayWorkoutMusic = viewModel::setAutoPlayWorkoutMusic,
                        onSelectAudioCoachMode = viewModel::selectAudioCoachMode,
                        onUpdateBiometrics = viewModel::updateBiometrics,
                        onSelectBiologicalSex = viewModel::selectBiologicalSex,
                        onSelectCurrentBracket = viewModel::selectCurrentBfBracket,
                        onSelectGoalBracket = viewModel::selectGoalBfBracket,
                        onUpdateSelectedShapeCardIds = viewModel::updateSelectedShapeCardIds,
                        onSelectWeeklyFrequency = viewModel::selectWeeklyFrequency,
                        onToggleRestDay = viewModel::toggleRestDay,
                        onSelectPlanStartOption = viewModel::selectPlanStartOption,
                        onSelectCustomStartDate = viewModel::selectCustomPlanStartDate,
                        onSelectTrainingEnvironment = viewModel::selectTrainingEnvironment,
                        onSelectExperienceLevel = viewModel::selectExperienceLevel,
                        onSelectRotationWeek = viewModel::selectRotationWeek,
                        onToggleBackgroundAlarms = { armed ->
                            if (armed) {
                                runWithNotificationPermissionIfNeeded {
                                    viewModel.toggleBackgroundAlarms(true)
                                }
                            } else {
                                viewModel.toggleBackgroundAlarms(false)
                            }
                        },
                        onUpdateCustomReminder = { checkpointId, hour, minute, enabled ->
                            if (enabled) {
                                runWithNotificationPermissionIfNeeded {
                                    viewModel.updateCustomReminder(checkpointId, hour, minute, true)
                                }
                            } else {
                                viewModel.updateCustomReminder(checkpointId, hour, minute, false)
                            }
                        },
                        onTestCheckpointAlarm = { checkpoint ->
                            runWithNotificationPermissionIfNeeded {
                                viewModel.testFireCheckpointDispatch(checkpoint)
                            }
                        },
                        onRerunFullOnboardingWizard = {
                            onboardingPrefs.edit().putBoolean("profile_accepted", false).apply()
                            hasAcceptedProtocol = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApexEngineBottomNavigationDock(
    currentRoute: String,
    onSelectRoute: (String) -> Unit
) {
    val strings = LocalAppStrings.current

    data class NavDockItem(
        val route: String,
        val tagKey: String,
        val label: String,
        val icon: ImageVector
    )

    val navItems = listOf(
        NavDockItem(ApexEngineRoutes.ROUTE_DASHBOARD, "dashboard", strings.navDashboard, Icons.Default.Dashboard),
        NavDockItem(ApexEngineRoutes.ROUTE_ACTIVE_SESSION, "workout", strings.navWorkout, Icons.Default.FitnessCenter),
        NavDockItem(ApexEngineRoutes.ROUTE_FOOD_GUIDE, "food_guide", strings.navFoodGuide, Icons.Default.Restaurant),
        NavDockItem(ApexEngineRoutes.ROUTE_PROGRESS_LEDGER, "progress", strings.navProgress, Icons.Default.BarChart),
        NavDockItem(ApexEngineRoutes.ROUTE_SETTINGS_HUB, "settings", strings.navSettings, Icons.Default.Settings)
    )

    Surface(
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, spotColor = Color(0x1A0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(thickness = 1.dp, color = SubtleDivider)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val activeTint = if (isSelected) Color(0xFF0F172A) else TextSecondarySlate

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("nav_dock_${item.tagKey}")
                            .clickable { onSelectRoute(item.route) }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color(0xFFEFF3F8) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = activeTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            color = activeTint,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
