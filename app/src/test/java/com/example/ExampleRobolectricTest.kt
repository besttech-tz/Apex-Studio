package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.system.ApexNotificationScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name and APEX_OPERATIONS notification channel`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Apex Protocol", appName)

        ApexNotificationScheduler.ensureNotificationChannel(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(ApexNotificationScheduler.CHANNEL_ID)
        assertNotNull(channel)
        assertEquals("APEX_OPERATIONS", channel.id)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
    }

    @Test
    fun `verify all 17 exercise illustrations decode start and peak human poses`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.domain.ExerciseAnimationType.entries.forEach { type ->
            val poses = com.example.ui.components.ExerciseIllustrationAtlas.getPoseBitmaps(context, type)
            assertNotNull("Expected non-null 2D poses for $type", poses)
            val (startBmp, peakBmp) = poses!!
            assert(startBmp.width > 20 && startBmp.height > 20) { "Start pose too small for $type" }
            assertEquals("Start and Peak width must match for $type", startBmp.width, peakBmp.width)
            assertEquals("Start and Peak height must match for $type", startBmp.height, peakBmp.height)
        }
    }

    @Test
    fun `verify all home workout and program cover images decode cleanly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.domain.HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.forEach { qs ->
            val resId = com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(qs)
            val bmp = android.graphics.BitmapFactory.decodeResource(context.resources, resId)
            assertNotNull("Expected cover bitmap for ${qs.id}", bmp)
            assert(bmp.width > 100 && bmp.height > 100) { "Cover bitmap too small for ${qs.id}" }
        }

        val studioBanners = listOf(
            R.drawable.img_workout_hub_banner_1794761668870,
            R.drawable.img_fitness_music_studio_1794761666737,
            R.drawable.img_nutrition_fuel_banner_1794761668466,
            R.drawable.img_cover_clean_fuel_hero_1794761632141,
            R.drawable.img_cover_back_shoulders_1794761630996,
            R.drawable.img_cover_leg_day_power_1794761630544,
            R.drawable.img_onboarding_fitness_banner_1794766376674,
            R.drawable.img_settings_studio_banner_1794766376203
        )
        studioBanners.forEach { resId ->
            val bmp = android.graphics.BitmapFactory.decodeResource(context.resources, resId)
            assertNotNull("Expected non-null studio banner bitmap for $resId", bmp)
            assert(bmp.width > 100 && bmp.height > 100)
        }

        com.example.system.WorkoutMusicEngine.selectTrack(
            com.example.system.WorkoutMusicTrack.FOCUS_GROOVE,
            autoPlay = false
        )
        assertEquals(
            com.example.system.WorkoutMusicTrack.FOCUS_GROOVE,
            com.example.system.WorkoutMusicEngine.state.value.selectedTrack
        )
        com.example.system.WorkoutMusicEngine.stop()
    }

    @Test
    fun `verify ExerciseAnimationMediaResolver ranks clean human GIFs and builds GIF ImageLoader`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val loader = com.example.system.ExerciseAnimationMediaResolver.imageLoader(context)
        assertNotNull(loader)

        val sampleSquatResponse = """
            {
              "success": true,
              "data": [
                {
                  "exerciseId": "bar1",
                  "name": "barbell full squat",
                  "gifUrl": "https://static.exercisedb.dev/media/bar1.gif",
                  "equipments": ["barbell"]
                },
                {
                  "exerciseId": "bw1",
                  "name": "air squat",
                  "gifUrl": "https://static.exercisedb.dev/media/bw1.gif",
                  "equipments": ["body weight"]
                }
              ]
            }
        """.trimIndent()

        val chosenSquatGif = com.example.system.ExerciseAnimationMediaResolver.pickBestExerciseDbGifForTest(
            sampleSquatResponse,
            com.example.domain.ExerciseAnimationType.BODYWEIGHT_SQUATS
        )
        assertEquals("https://static.exercisedb.dev/media/bw1.gif", chosenSquatGif)

        val samplePushupResponse = """
            {
              "success": true,
              "data": [
                {
                  "exerciseId": "inc1",
                  "name": "incline push-up",
                  "gifUrl": "https://static.exercisedb.dev/media/inc1.gif",
                  "equipments": ["body weight"]
                },
                {
                  "exerciseId": "std1",
                  "name": "push-up",
                  "gifUrl": "https://static.exercisedb.dev/media/std1.gif",
                  "equipments": ["body weight"]
                }
              ]
            }
        """.trimIndent()

        val chosenPushupGif = com.example.system.ExerciseAnimationMediaResolver.pickBestExerciseDbGifForTest(
            samplePushupResponse,
            com.example.domain.ExerciseAnimationType.STANDARD_PUSH_UPS
        )
        assertEquals("https://static.exercisedb.dev/media/std1.gif", chosenPushupGif)

        // Unrelated ExerciseDB results (score < 40) must be rejected so wrong GIFs never poison the cache
        val unrelatedDipResponse = """
            {
              "success": true,
              "data": [
                {
                  "exerciseId": "dip99",
                  "name": "parallel bar chest dip",
                  "gifUrl": "https://static.exercisedb.dev/media/dip99.gif",
                  "equipments": ["body weight"]
                }
              ]
            }
        """.trimIndent()
        val rejectedGif = com.example.system.ExerciseAnimationMediaResolver.pickBestExerciseDbGifForTest(
            unrelatedDipResponse,
            com.example.domain.ExerciseAnimationType.JUMPING_JACKS
        )
        org.junit.Assert.assertNull("Unrelated ExerciseDB item must be rejected", rejectedGif)

        // Every quick/specialty/challenge workout must resolve every exercise to a distinct LoopingGif
        com.example.domain.HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.forEach { workout ->
            val urls = workout.exercises.map { move ->
                val media = com.example.system.ExerciseAnimationMediaResolver.getCachedMediaInstant(
                    context = context,
                    type = move.animationType,
                    exerciseName = move.name
                )
                org.junit.Assert.assertTrue(
                    "Expected LoopingGif for ${move.name}",
                    media is com.example.system.ResolvedExerciseMedia.LoopingGif
                )
                val gifUrl = (media as com.example.system.ResolvedExerciseMedia.LoopingGif).gifUrl
                org.junit.Assert.assertTrue(
                    "Expected unified static.exercisedb.dev GIF for ${move.name}, got $gifUrl",
                    gifUrl.startsWith("https://static.exercisedb.dev/media/") && gifUrl.endsWith(".gif")
                )
                gifUrl
            }
            assertEquals(
                "Every exercise in ${workout.id} (${workout.exercises.map { it.name }}) must have a distinct GIF",
                workout.exercises.size,
                urls.distinct().size
            )
        }
    }

    @get:org.junit.Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @Test
    fun `verify female curvy silhouette sheet slices cleanly into 4 distinct feminine target figures`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val targetCardIds = listOf(
            "goal_1_fat_loss",
            "goal_3_athletic",
            "goal_2_vtaper",
            "goal_4_power_mass"
        )
        targetCardIds.forEach { cardId ->
            val preset = com.example.ui.components.PhysiqueSilhouettePreset.fromCardId(cardId)
            val bmp = com.example.ui.components.PhysiqueSheetSlicer.getFigureBitmap(
                context = context,
                sex = com.example.domain.BiologicalSex.FEMALE,
                preset = preset
            )
            assertNotNull("Expected sliced feminine figure bitmap for $cardId", bmp)
            assert(bmp!!.width > 80 && bmp.height > 120) {
                "Sliced feminine figure bitmap too small (${bmp.width}x${bmp.height}) for $cardId"
            }
        }
    }

    @Test
    fun `verify guided workout player renders clean phase pills and working 3-option Switch Exercise sheet`() {
        val femaleProfile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.FEMALE,
            experienceLevel = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.GYM,
            selectedStartingShapeCardId = "shape_5_underweight",
            selectedGoalCardId = "goal_4_power_mass",
            includeWarmupAndStretch = true,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val femaleSplit = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
            startingShapeId = "shape_5_underweight",
            goalCardId = "goal_4_power_mass"
        )
        val testUiState = com.example.ui.ApexUiState(
            profile = femaleProfile,
            availableCombatDays = femaleSplit,
            activeCombatDay = femaleSplit.first(),
            openBriefingDirectly = true
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = testUiState
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(150L)

        // Overview displays personalized blueprint summary
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("blueprint_overview_summary"))
            .assertExists()

        // Launch active workout from overview -> starts in clean Warm-Up phase with pure-Gym joint opener (no jumping jacks!)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("banner_workout_phase_identity"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("WARM-UP • 1/2", substring = true))
            .assertExists()
        composeTestRule.onNode(
            androidx.compose.ui.test.hasText("Dynamic Squat Opener", substring = true, ignoreCase = true)
        ).assertExists()

        // Skip warm-up -> transitions cleanly into Main Workout
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("banner_workout_phase_identity"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("MAIN WORKOUT • 1/5", substring = true))
            .assertExists()
        composeTestRule.onNode(
            androidx.compose.ui.test.hasText("Padded Bench / Machine Hip Thrust", substring = true, ignoreCase = true)
        ).assertExists()

        // Tap Switch Exercise (⇄) -> opens 3-option sheet -> pick Small Gym (Dumbbells Only) -> visibly switches exercise!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_swap_variant"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_swap_exercise"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("swap_option_0")).assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("swap_option_1")).assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("swap_option_2")).assertExists()

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("swap_option_1"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(
            androidx.compose.ui.test.hasText("Dumbbell Glute Bridge", substring = true, ignoreCase = true)
        ).assertExists()
    }

    @Test
    fun `verify workout back button returns to Workout Hub when launched from Workout tab and supports Continue and Start New`() {
        val profile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.FEMALE,
            experienceLevel = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.GYM,
            includeWarmupAndStretch = true,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = profile.biologicalSex,
            ageTier = profile.ageTier,
            experience = profile.experienceLevel,
            frequency = profile.weeklyFrequency
        )
        var navigatedHomeCount = 0

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            var uiState by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(
                    com.example.ui.ApexUiState(
                        profile = profile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        openBriefingDirectly = true,
                        workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                        workoutLaunchMode = com.example.ui.WorkoutLaunchMode.OVERVIEW,
                        workoutLaunchNonce = 1L
                    )
                )
            }
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = uiState,
                    onSaveMidwayWorkout = { snap ->
                        uiState = uiState.copy(midwayWorkout = snap)
                    },
                    onClearMidwayWorkout = {
                        uiState = uiState.copy(midwayWorkout = null)
                    },
                    onReturnToWorkoutHub = {
                        uiState = uiState.copy(
                            openBriefingDirectly = false,
                            workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                            workoutLaunchMode = com.example.ui.WorkoutLaunchMode.HUB,
                            workoutLaunchNonce = uiState.workoutLaunchNonce + 1L
                        )
                    },
                    onResumeMidwayFromHub = {
                        uiState = uiState.copy(
                            openBriefingDirectly = false,
                            workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                            workoutLaunchMode = com.example.ui.WorkoutLaunchMode.RESUME_ACTIVE,
                            workoutLaunchNonce = uiState.workoutLaunchNonce + 1L
                        )
                    },
                    onRestartMidwayFromHub = {
                        uiState = uiState.copy(
                            openBriefingDirectly = false,
                            workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                            workoutLaunchMode = com.example.ui.WorkoutLaunchMode.RESTART_FRESH,
                            workoutLaunchNonce = uiState.workoutLaunchNonce + 1L,
                            midwayWorkout = null
                        )
                    },
                    onNavigateBackHome = {
                        navigatedHomeCount += 1
                    }
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // Start workout -> Skip Warm-Up -> Advance to Main Workout Move 2
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(1800L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_next_exercise"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(400L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("MAIN WORKOUT • 2/5", substring = true))
            .assertExists()

        // Press Back -> MUST return to Workout Hub (NOT previous exercise, NOT Home Dashboard!)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_back_to_dashboard"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        assertEquals("Back from Workout-tab session must stay on Workout Hub", 0, navigatedHomeCount)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("screen_workout_journey_hub"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_midway_workout_session"))
            .assertExists()

        // Tap Continue -> jumps directly back into Move 2 of 5 (skips overview!)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_resume_midway_workout"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("MAIN WORKOUT • 2/5", substring = true))
            .assertExists()

        // Press Back again -> tap Start New -> restarts cleanly at Move 1 (WARM-UP • 1/2)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_back_to_dashboard"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_restart_midway_workout"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("WARM-UP • 1/2", substring = true))
            .assertExists()
    }

    @Test
    fun `verify workout back button returns to Dashboard when launched from Dashboard and supports Mark Complete before Post-Gym Cool-Down`() {
        val profile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.MALE,
            experienceLevel = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.GYM,
            includeWarmupAndStretch = true,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = profile.biologicalSex,
            ageTier = profile.ageTier,
            experience = profile.experienceLevel,
            frequency = profile.weeklyFrequency
        )
        var onDashboard = false
        var completedMidwayCount = 0

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            var uiState by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(
                    com.example.ui.ApexUiState(
                        profile = profile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        openBriefingDirectly = true,
                        workoutOriginPage = com.example.ui.WorkoutOriginPage.DASHBOARD,
                        workoutLaunchMode = com.example.ui.WorkoutLaunchMode.OVERVIEW,
                        workoutLaunchNonce = 1L
                    )
                )
            }
            var showDashboard by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(false)
            }
            com.example.ui.theme.ApexProtocolTheme {
                if (showDashboard) {
                    com.example.ui.screens.HomeDashboardScreen(
                        uiState = uiState,
                        onCompleteMidwayWorkout = {
                            completedMidwayCount += 1
                            uiState = uiState.copy(midwayWorkout = null)
                        }
                    )
                } else {
                    com.example.ui.screens.CombatDeckScreen(
                        uiState = uiState,
                        onSaveMidwayWorkout = { snap ->
                            uiState = uiState.copy(midwayWorkout = snap)
                        },
                        onClearMidwayWorkout = {
                            uiState = uiState.copy(midwayWorkout = null)
                        },
                        onNavigateBackHome = {
                            onDashboard = true
                            showDashboard = true
                        }
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // Start workout from Dashboard -> Skip Warm-Up -> Step through all 5 Main moves to reach Post-Gym Cool-Down Stretch
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(1800L)

        repeat(5) {
            composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_next_exercise"))
                .performClick()
            composeTestRule.mainClock.advanceTimeBy(1800L)
        }

        composeTestRule.onNode(androidx.compose.ui.test.hasText("COOL-DOWN • 1/2", substring = true))
            .assertExists()

        // Leave before finishing Post-Gym Cool-Down -> returns to Dashboard!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_back_to_dashboard"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        org.junit.Assert.assertTrue("Expected Back to return to Dashboard", onDashboard)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("home_dashboard_list"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_midway_workout_session"))
            .assertExists()
        composeTestRule.onNode(
            androidx.compose.ui.test.hasText("MAIN WORKOUT DONE", substring = true, ignoreCase = true)
        ).assertExists()

        // Tap Mark Complete -> logs completion and removes midway card!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_complete_midway_workout"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300L)

        assertEquals(1, completedMidwayCount)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_midway_workout_session"))
            .assertDoesNotExist()
    }

    @Test
    fun `verify extra workout pack separates Warm-Up Main Sets and Stretch and supports 3-option Switch Exercise`() {
        val femaleProfile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.FEMALE,
            experienceLevel = com.example.domain.ExperienceLevel.BEGINNER_JOINT_SAFE,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.GYM,
            selectedStartingShapeCardId = "shape_5_underweight",
            selectedGoalCardId = "goal_4_power_mass",
            includeWarmupAndStretch = true,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = femaleProfile.biologicalSex,
            ageTier = femaleProfile.ageTier,
            experience = femaleProfile.experienceLevel,
            frequency = femaleProfile.weeklyFrequency
        )
        val bootyPack = com.example.domain.HomeWorkoutCatalog.FEMALE_SHAPING_WORKOUTS
            .first { it.id == "track_glute_shelf_sculpt" }

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = com.example.ui.ApexUiState(
                        profile = femaleProfile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        activeQuickStrike = bootyPack,
                        openBriefingDirectly = true
                    )
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // Overview separates Warm-Up Prep from Main Workout Sets and exposes Switch Exercise (⇄)
        composeTestRule.onNode(androidx.compose.ui.test.hasText("WARM-UP PREP", substring = true, ignoreCase = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_overview_swap_0"))
            .assertExists()

        // Launch pack -> starts in Warm-Up -> Skip Warm-Up -> Main Workout Move 1/3 -> Switch to Small Gym Dumbbells!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("WARM-UP • 1/1", substring = true))
            .assertExists()

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(250L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("MAIN WORKOUT • 1/4", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("Padded Hip Thrust", substring = true, ignoreCase = true))
            .assertExists()

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_swap_variant"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_swap_exercise"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("swap_option_1"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("Dumbbell Glute Bridge", substring = true, ignoreCase = true))
            .assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "w412dp-h915dp")
    fun `verify multi-day challenge renders interactive Day Roadmap and new gender-matched studio photos decode`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val newGenderStudioAssets = listOf(
            R.drawable.img_male_back_1795026866340,
            R.drawable.img_male_legs_1795026867840,
            R.drawable.img_male_abs_1795026870074,
            R.drawable.img_female_home_1795026870729,
            R.drawable.img_female_stretch_1795026870917
        )
        newGenderStudioAssets.forEach { resId ->
            val bmp = android.graphics.BitmapFactory.decodeResource(context.resources, resId)
            assertNotNull("Expected valid bitmap for new studio asset $resId", bmp)
            assert(bmp.width > 100 && bmp.height > 100)
        }

        val femaleProfile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.FEMALE,
            experienceLevel = com.example.domain.ExperienceLevel.BEGINNER_JOINT_SAFE,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.GYM,
            challengeCompletedDays = mapOf("challenge_7d_booty_lift" to setOf(1, 2)),
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = femaleProfile.biologicalSex,
            ageTier = femaleProfile.ageTier,
            experience = femaleProfile.experienceLevel,
            frequency = femaleProfile.weeklyFrequency
        )
        val challenge7d = com.example.domain.HomeWorkoutCatalog.FEMALE_SHAPING_WORKOUTS
            .first { it.id == "challenge_7d_booty_lift" }

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = com.example.ui.ApexUiState(
                        profile = femaleProfile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        activeQuickStrike = challenge7d,
                        openBriefingDirectly = true
                    )
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // Day 1 & Day 2 are completed -> roadmap auto-selects Day 3 and shows 2 / 7 Done!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_challenge_day_roadmap"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("2 / 7 Done", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("START DAY 3", substring = true))
            .assertExists()

        // Tap Day 5 pill -> updates active Day CTA to START DAY 5
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("challenge_day_pill_5"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(150L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("START DAY 5", substring = true))
            .assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "w412dp-h915dp")
    fun `verify 14-day pull-up challenge roadmap and live Can't Do It 0-to-Hero mastery coach dialog`() {
        val maleProfile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.MALE,
            experienceLevel = com.example.domain.ExperienceLevel.BEGINNER_JOINT_SAFE,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.HOME,
            challengeCompletedDays = mapOf("challenge_14d_pullup_vtaper_mastery" to setOf(1)),
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = maleProfile.biologicalSex,
            ageTier = maleProfile.ageTier,
            experience = maleProfile.experienceLevel,
            frequency = maleProfile.weeklyFrequency
        )
        val pullUpChallenge = com.example.domain.HomeWorkoutCatalog.workoutsForSex(com.example.domain.BiologicalSex.MALE)
            .first { it.id == "challenge_14d_pullup_vtaper_mastery" }

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = com.example.ui.ApexUiState(
                        profile = maleProfile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        activeQuickStrike = pullUpChallenge,
                        openBriefingDirectly = true
                    )
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // 1. 14-Day Challenge Roadmap renders 1 / 14 Done and auto-selects Day 2
        composeTestRule.onNode(androidx.compose.ui.test.hasText("14-Day Challenge Roadmap", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("1 / 14 Done", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("START DAY 2", substring = true))
            .assertExists()

        // 2. Launch Day 2 -> Skip Warm-Up -> Main Workout Move 1 (Dead Hang & Scapular Pulls)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(250L)

        // 3. User struggles / doesn't know how -> taps "Can't do it or failed mid-set? 0→Hero Tips →"
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_cant_do_zero_to_hero"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_zero_to_hero_coach"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("ladder_step_card_0"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("ladder_step_card_3"))
            .assertExists()

        // 4. Tap "Switch to Easier Variation Now" right from the 0->Hero dialog -> opens Swap sheet!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_zero_to_hero_open_swap"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_swap_exercise"))
            .assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "w412dp-h915dp")
    fun `verify switching from Pull-Up challenge to Calisthenics Stage 4 opens Planche briefing and Planche 0-to-Hero ladder`() {
        val maleProfile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.MALE,
            experienceLevel = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.HOME,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = maleProfile.biologicalSex,
            ageTier = maleProfile.ageTier,
            experience = maleProfile.experienceLevel,
            frequency = maleProfile.weeklyFrequency
        )
        val pullUpChallenge = com.example.domain.HomeWorkoutCatalog.workoutsForSex(com.example.domain.BiologicalSex.MALE)
            .first { it.id == "challenge_14d_pullup_vtaper_mastery" }

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            var uiState by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(
                    com.example.ui.ApexUiState(
                        profile = maleProfile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        activeQuickStrike = pullUpChallenge,
                        openBriefingDirectly = true,
                        workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                        workoutLaunchMode = com.example.ui.WorkoutLaunchMode.OVERVIEW,
                        workoutLaunchNonce = 1L
                    )
                )
            }
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = uiState,
                    onReturnToWorkoutHub = {
                        uiState = uiState.copy(
                            activeQuickStrike = null,
                            openBriefingDirectly = false,
                            workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                            workoutLaunchMode = com.example.ui.WorkoutLaunchMode.HUB,
                            workoutLaunchNonce = uiState.workoutLaunchNonce + 1L
                        )
                    },
                    onStartQuickStrikeFromHub = { track ->
                        uiState = uiState.copy(
                            activeQuickStrike = track,
                            openBriefingDirectly = true,
                            workoutOriginPage = com.example.ui.WorkoutOriginPage.WORKOUT_HUB,
                            workoutLaunchMode = com.example.ui.WorkoutLaunchMode.OVERVIEW,
                            workoutLaunchNonce = uiState.workoutLaunchNonce + 1L
                        )
                    }
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // 0. Initially viewing Pull-Up Challenge -> tap Back to return to Workout Hub
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_overview_back"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(250L)

        // 1. Select Calisthenics category chip & scroll to Stage 4 Elite pack
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("category_chip_calisthenics"))
            .performScrollTo()
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("hub_track_card_track_calisthenics_stage4_elite"))
            .performScrollTo()
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(250L)

        // 2. Verify it opens Stage 4 28-Day Elite Calisthenics Challenge (with 28-Day Challenge Roadmap & Expected Results!)
        composeTestRule.onNode(androidx.compose.ui.test.hasText("Stage 4: 28-Day Elite Planche", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_challenge_day_roadmap"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("Parallettes Planche Hold & Lean", substring = true))
            .assertExists()

        // 3. Start workout -> Skip Warm-Up -> tap 0->Hero Tips on Planche -> verify Planche ladder opens!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_workout_from_overview"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_skip_warmup_active"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(250L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_cant_do_zero_to_hero"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasText("0 → Full Planche & Straight-Arm Strength", substring = true))
            .assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "w412dp-h915dp")
    fun `verify top-of-workouts Target Exercises directory opens Neck Tongue Mewing and Voice Breath with timelines`() {
        val profile = com.example.data.local.CalibrationProfileState(
            biologicalSex = com.example.domain.BiologicalSex.MALE,
            experienceLevel = com.example.domain.ExperienceLevel.NEW_BEGINNER,
            trainingEnvironment = com.example.data.local.TrainingEnvironment.HOME,
            audioCoachMode = com.example.domain.WorkoutAudioCoachMode.MUTED
        )
        val split = com.example.domain.CombatSplitCatalog.getSplitForProfile(
            sex = profile.biologicalSex,
            ageTier = profile.ageTier,
            experience = profile.experienceLevel,
            frequency = profile.weeklyFrequency
        )
        val weekSchedule = com.example.domain.WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = split,
            restDaysIso = profile.restDaysIso
        )

        var startedTargetWorkout: com.example.domain.QuickStrikeWorkout? = null

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            com.example.ui.theme.ApexProtocolTheme {
                com.example.ui.screens.CombatDeckScreen(
                    uiState = com.example.ui.ApexUiState(
                        profile = profile,
                        availableCombatDays = split,
                        activeCombatDay = split.first(),
                        weekSchedule = weekSchedule,
                        workoutLaunchMode = com.example.ui.WorkoutLaunchMode.HUB
                    ),
                    onStartQuickStrikeFromHub = { startedTargetWorkout = it }
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(200L)

        // 1. Target Exercises Directory appears right at the top of Workouts
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("card_target_exercises_directory"))
            .assertExists()

        // 2. Filter to Neck, Voice & Skills zone and open Jawline, Neck & Tongue Posture (Mewing)
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("target_zone_pill_head_voice_mobility"))
            .performScrollTo()
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("target_group_chip_target_ex_jaw_neck_tongue"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_target_group_detail"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("Jawline, Neck & Tongue Posture (Mewing)", substring = true))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("EXPECTED RESULT TIMELINE", substring = true))
            .assertExists()

        // 3. Tap Tongue-Roof Mewing Suction Hold -> verify dedicated 0->Hero guide opens!
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("target_detail_move_1"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("dialog_zero_to_hero_coach"))
            .assertExists()
        composeTestRule.onNode(androidx.compose.ui.test.hasText("Jawline, Neck & Tongue Posture", substring = true))
            .assertExists()

        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_close_zero_to_hero"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        // 4. Start the focused session directly from the Target Group Detail modal
        composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("btn_start_target_group_session"))
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(200L)

        org.junit.Assert.assertNotNull(startedTargetWorkout)
        org.junit.Assert.assertEquals("target_ex_jaw_neck_tongue", startedTargetWorkout?.id)
    }
}

