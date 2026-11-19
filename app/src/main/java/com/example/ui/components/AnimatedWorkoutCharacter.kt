package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.request.repeatCount
import com.example.R
import com.example.domain.ExerciseAnimationType
import com.example.system.ExerciseAnimationMediaResolver
import com.example.system.ResolvedExerciseMedia
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private val StagePureWhite = Color(0xFFFFFFFF)
private val StageElectricBlue = Color(0xFF1D63ED)

/**
 * Biomechanical live personal-coach specification & natural trainer voice lines for all 17 exercises.
 */
data class LiveCoachMovementGuide(
    @DrawableRes val photoRes: Int,
    val cropBiasY: Float = -0.15f,
    val startsAtTop: Boolean = true,
    val isHoldMove: Boolean = false,
    val topTrackLabel: String = "TOP",
    val bottomTrackLabel: String = "BOTTOM",
    val phaseOneCommand: String,
    val phaseOneCue: String,
    val phaseOneCoachDialogue: String,
    val phaseTwoCommand: String,
    val phaseTwoCue: String,
    val phaseTwoCoachDialogue: String,
    val setupRule: String,
    val shortDriveVoiceCue: String,
    val spokenFormBreathingTip: String,
    val phaseOneBreath: String = "INHALE",
    val phaseTwoBreath: String = "EXHALE"
)

fun resolveLiveCoachGuide(type: ExerciseAnimationType): LiveCoachMovementGuide = when (type) {
    ExerciseAnimationType.BODYWEIGHT_SQUATS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_home_wakeup_1794570079145,
        startsAtTop = true,
        topTrackLabel = "STAND",
        bottomTrackLabel = "SQUAT",
        phaseOneCommand = "SQUAT DOWN",
        phaseOneCue = "Sit hips back • heels flat",
        phaseOneCoachDialogue = "Inhale as you sit your hips back...",
        phaseTwoCommand = "STAND TALL!",
        phaseTwoCue = "Drive through heels & squeeze",
        phaseTwoCoachDialogue = "Exhale and stand up tall!",
        setupRule = "Feet shoulder-width apart, chest tall — sit hips back comfortably, then stand tall.",
        shortDriveVoiceCue = "Inhale as you sit down, and exhale as you stand tall",
        spokenFormBreathingTip = "Inhale as you lower your hips, and exhale as you push through your heels to stand."
    )

    ExerciseAnimationType.FORWARD_LUNGES -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_leg_day_power_1794761630544,
        startsAtTop = true,
        topTrackLabel = "STAND",
        bottomTrackLabel = "90° KNEE",
        phaseOneCommand = "DROP BACK KNEE",
        phaseOneCue = "Torso tall • both knees bend",
        phaseOneCoachDialogue = "Inhale as your back knee lowers...",
        phaseTwoCommand = "DRIVE UP!",
        phaseTwoCue = "Push firmly through front heel",
        phaseTwoCoachDialogue = "Exhale and push through your front heel!",
        setupRule = "Step one foot forward, torso upright — lower straight down, then drive back up.",
        shortDriveVoiceCue = "Keep your chest tall and push through your front heel",
        spokenFormBreathingTip = "Inhale as you lower straight down, and exhale as you drive back up."
    )

    ExerciseAnimationType.STANDARD_PUSH_UPS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_chest_arms_1794570080759,
        startsAtTop = true,
        topTrackLabel = "LOCKOUT",
        bottomTrackLabel = "CHEST",
        phaseOneCommand = "LOWER CHEST",
        phaseOneCue = "Elbows 45° • straight body",
        phaseOneCoachDialogue = "Inhale as you lower your chest...",
        phaseTwoCommand = "PUSH UP!",
        phaseTwoCue = "Press floor away strong",
        phaseTwoCoachDialogue = "Exhale and press the floor away!",
        setupRule = "Hands just wider than shoulders, core tight — lower chest smoothly and press up.",
        shortDriveVoiceCue = "Inhale on the way down, and exhale as you press up",
        spokenFormBreathingTip = "Keep your body in a straight line. Inhale going down, and exhale pressing up."
    )

    ExerciseAnimationType.INCLINE_PUSH_UPS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_chest_arms_1794570080759,
        startsAtTop = true,
        topTrackLabel = "ARMS TALL",
        bottomTrackLabel = "EDGE",
        phaseOneCommand = "LOWER TO EDGE",
        phaseOneCue = "Elbows tucked • abs braced",
        phaseOneCoachDialogue = "Inhale bringing chest toward the edge...",
        phaseTwoCommand = "PRESS TALL!",
        phaseTwoCue = "Push palms straight to lockout",
        phaseTwoCoachDialogue = "Exhale and press arms tall!",
        setupRule = "Hands on sturdy bench, counter, or bed edge — lower chest to edge and press tall.",
        shortDriveVoiceCue = "Inhale down to the edge, and exhale pressing tall",
        spokenFormBreathingTip = "Keep your abs braced. Inhale as you lower, and exhale as you press away."
    )

    ExerciseAnimationType.KNEE_PUSH_UPS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_chest_arms_1794570080759,
        startsAtTop = true,
        topTrackLabel = "ARMS TALL",
        bottomTrackLabel = "MAT",
        phaseOneCommand = "LOWER SMOOTH",
        phaseOneCue = "Straight line knees-to-head",
        phaseOneCoachDialogue = "Inhale as you lower smoothly...",
        phaseTwoCommand = "PRESS UP!",
        phaseTwoCue = "Drive palms down & squeeze chest",
        phaseTwoCoachDialogue = "Exhale and press straight up!",
        setupRule = "Knees on mat, straight hip-to-shoulder line — lower chest and press straight up.",
        shortDriveVoiceCue = "Keep hips aligned, exhale as you press up",
        spokenFormBreathingTip = "Inhale as your chest lowers toward the mat, and exhale as you press up."
    )

    ExerciseAnimationType.INCLINE_DB_PRESS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_raw_strength_1794572460981,
        startsAtTop = false,
        topTrackLabel = "PRESS TOP",
        bottomTrackLabel = "CHEST",
        phaseOneCommand = "PRESS UP!",
        phaseOneCue = "Drive dumbbells over upper chest",
        phaseOneCoachDialogue = "Exhale and press the weights up!",
        phaseTwoCommand = "LOWER SMOOTH",
        phaseTwoCue = "Elbows 45° • clean stretch",
        phaseTwoCoachDialogue = "Inhale as you lower to chest level...",
        setupRule = "Bench at 30°–45°, shoulder blades tucked — press dumbbells up, then lower smoothly.",
        shortDriveVoiceCue = "Exhale pressing up, inhale lowering with control",
        spokenFormBreathingTip = "Exhale as you press the dumbbells overhead, and inhale as you lower smoothly."
    )

    ExerciseAnimationType.LAT_PULLDOWN -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_back_shoulders_1794761630996,
        startsAtTop = true,
        topTrackLabel = "STRETCH",
        bottomTrackLabel = "CHEST",
        phaseOneCommand = "PULL TO CHEST!",
        phaseOneCue = "Drive elbows down • squeeze lats",
        phaseOneCoachDialogue = "Exhale and pull bar to upper chest!",
        phaseTwoCommand = "EXTEND UP",
        phaseTwoCue = "Control bar up to full stretch",
        phaseTwoCoachDialogue = "Inhale guiding the bar smoothly up...",
        setupRule = "Sit tall, thighs locked under pad — pull bar to collarbone, then extend smoothly.",
        shortDriveVoiceCue = "Pull elbows down and squeeze your back",
        spokenFormBreathingTip = "Exhale as you pull the bar to your chest, and inhale as your arms extend."
    )

    ExerciseAnimationType.SEATED_CABLE_ROW -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_onboarding_fitness_banner_1794766376674,
        startsAtTop = false,
        topTrackLabel = "SQUEEZE",
        bottomTrackLabel = "REACH",
        phaseOneCommand = "ROW & SQUEEZE!",
        phaseOneCue = "Pull to ribs • pinch shoulder blades",
        phaseOneCoachDialogue = "Exhale and row in tight to ribs!",
        phaseTwoCommand = "EXTEND SMOOTH",
        phaseTwoCue = "Spine tall • reach forward cleanly",
        phaseTwoCoachDialogue = "Inhale extending arms smoothly...",
        setupRule = "Sit tall with knees soft — pull handle to lower ribs, squeeze back, and extend.",
        shortDriveVoiceCue = "Pinch your shoulder blades together, chest proud",
        spokenFormBreathingTip = "Exhale as you row to your ribs, and inhale as you extend forward."
    )

    ExerciseAnimationType.CABLE_LATERAL_RAISE -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_gym_program_1794570078368,
        startsAtTop = false,
        topTrackLabel = "T-LEVEL",
        bottomTrackLabel = "SIDES",
        phaseOneCommand = "RAISE TO T!",
        phaseOneCue = "Sweep wide to shoulder height",
        phaseOneCoachDialogue = "Exhale sweeping arms to shoulder height!",
        phaseTwoCommand = "LOWER SMOOTH",
        phaseTwoCue = "Control down • zero body swing",
        phaseTwoCoachDialogue = "Inhale lowering smoothly to sides...",
        setupRule = "Stand tall with soft elbows — sweep weights out to shoulder height, then lower.",
        shortDriveVoiceCue = "Lift to shoulder height with soft elbows",
        spokenFormBreathingTip = "Exhale as your arms sweep wide, and inhale as they return to your sides."
    )

    ExerciseAnimationType.INCLINE_DB_CURL -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_grip_power_1794572439024,
        startsAtTop = false,
        topTrackLabel = "SQUEEZE",
        bottomTrackLabel = "STRETCH",
        phaseOneCommand = "CURL UP!",
        phaseOneCue = "Elbows pinned • squeeze biceps",
        phaseOneCoachDialogue = "Exhale curling up & squeezing biceps!",
        phaseTwoCommand = "FULL STRETCH",
        phaseTwoCue = "Lower all the way with control",
        phaseTwoCoachDialogue = "Inhale lowering all the way down...",
        setupRule = "Elbows pinned at sides, palms forward — curl weights up and lower to a full stretch.",
        shortDriveVoiceCue = "Keep your elbows pinned and squeeze at the top",
        spokenFormBreathingTip = "Exhale as you curl the weights up, and inhale as you lower to a full stretch."
    )

    ExerciseAnimationType.TRICEP_DIPS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_raw_strength_1794572460981,
        startsAtTop = true,
        topTrackLabel = "LOCKOUT",
        bottomTrackLabel = "90° DIP",
        phaseOneCommand = "DIP TO 90°",
        phaseOneCue = "Back close to bench • elbows back",
        phaseOneCoachDialogue = "Inhale dipping elbows to ninety degrees...",
        phaseTwoCommand = "PRESS STRAIGHT!",
        phaseTwoCue = "Push palms down & lock triceps",
        phaseTwoCoachDialogue = "Exhale pressing straight up!",
        setupRule = "Hands on sturdy chair or bench edge behind you — dip to 90° and press straight up.",
        shortDriveVoiceCue = "Keep hips close to the bench and press tall",
        spokenFormBreathingTip = "Inhale as you lower down, and exhale as you press your arms straight."
    )

    ExerciseAnimationType.DOORFRAME_ROW -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_back_shoulders_1794761630996,
        startsAtTop = false,
        topTrackLabel = "SQUEEZE",
        bottomTrackLabel = "LEAN BACK",
        phaseOneCommand = "PULL CHEST IN!",
        phaseOneCue = "Drive elbows back • pinch blades",
        phaseOneCoachDialogue = "Exhale pulling chest to your hands!",
        phaseTwoCommand = "LEAN SMOOTH",
        phaseTwoCue = "Body straight • extend arms",
        phaseTwoCoachDialogue = "Inhale leaning back with control...",
        setupRule = "Grip sturdy doorframe, plant heels, lean back straight — pull chest to hands.",
        shortDriveVoiceCue = "Pull your chest in and squeeze your shoulder blades",
        spokenFormBreathingTip = "Exhale as you pull your chest forward, and inhale as you lean back smoothly."
    )

    ExerciseAnimationType.PIKE_SHOULDER_PRESS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_chest_arms_1794570080759,
        startsAtTop = true,
        topTrackLabel = "HIGH V",
        bottomTrackLabel = "MAT",
        phaseOneCommand = "LOWER HEAD",
        phaseOneCue = "Hips high in inverted-V",
        phaseOneCoachDialogue = "Inhale lowering head gently toward mat...",
        phaseTwoCommand = "PRESS TALL!",
        phaseTwoCue = "Push floor away through shoulders",
        phaseTwoCoachDialogue = "Exhale pressing shoulders tall!",
        setupRule = "Hands & feet on mat with hips raised in an upside-down V — lower head and press up.",
        shortDriveVoiceCue = "Keep hips high and press through your shoulders",
        spokenFormBreathingTip = "Inhale as you lower down, and exhale as you press back into a high V."
    )

    ExerciseAnimationType.ABDOMINAL_CRUNCHES -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_core_vitality_1794572439738,
        startsAtTop = false,
        topTrackLabel = "CRUNCH",
        bottomTrackLabel = "MAT",
        phaseOneCommand = "CURL ABS UP!",
        phaseOneCue = "Lift shoulder blades • zero neck pull",
        phaseOneCoachDialogue = "Exhale curling ribs toward pelvis!",
        phaseTwoCommand = "UNROLL DOWN",
        phaseTwoCue = "Lower shoulders smoothly to mat",
        phaseTwoCoachDialogue = "Inhale unrolling smoothly down...",
        setupRule = "Lie on mat, knees bent, hands lightly by temples — curl upper abs up and unroll.",
        shortDriveVoiceCue = "Exhale as you curl up, do not pull on your neck",
        spokenFormBreathingTip = "Exhale as your shoulders lift off the mat, and inhale as you lower down."
    )

    ExerciseAnimationType.PLANK_HOLD -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_abs_core_1794570081800,
        startsAtTop = true,
        isHoldMove = true,
        topTrackLabel = "INHALE",
        bottomTrackLabel = "EXHALE",
        phaseOneCommand = "INHALE STEADY",
        phaseOneCue = "Elbows under shoulders • body straight",
        phaseOneCoachDialogue = "Breathe in steady — keep body straight...",
        phaseTwoCommand = "EXHALE & BRACE",
        phaseTwoCue = "Tighten abs & glutes • don't sag",
        phaseTwoCoachDialogue = "Exhale and brace your core tight!",
        setupRule = "Forearms on mat, elbows right under shoulders — hold one straight line head-to-heels.",
        shortDriveVoiceCue = "Hold a straight line and breathe steadily",
        spokenFormBreathingTip = "Keep your body in one straight line, tighten your abs, and breathe naturally."
    )

    ExerciseAnimationType.COBRA_STRETCH -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_cover_posture_stretch_1794570082806,
        startsAtTop = true,
        isHoldMove = true,
        topTrackLabel = "INHALE",
        bottomTrackLabel = "RELAX",
        phaseOneCommand = "BREATHE DEEP",
        phaseOneCue = "Lift chest gently • shoulders down",
        phaseOneCoachDialogue = "Inhale deeply and lengthen your spine...",
        phaseTwoCommand = "EXHALE & RELAX",
        phaseTwoCue = "Drop shoulders away from ears",
        phaseTwoCoachDialogue = "Exhale slowly and relax your shoulders...",
        setupRule = "Lie face-down, palms under shoulders — lift chest gently to a comfortable stretch.",
        shortDriveVoiceCue = "Relax your shoulders down and breathe deeply",
        spokenFormBreathingTip = "Keep your shoulders relaxed away from your ears, and take slow deep breaths."
    )

    ExerciseAnimationType.JUMPING_JACKS -> LiveCoachMovementGuide(
        photoRes = R.drawable.img_workout_hub_banner_1794761668870,
        startsAtTop = false,
        topTrackLabel = "ARMS UP",
        bottomTrackLabel = "FEET IN",
        phaseOneCommand = "JUMP WIDE & UP!",
        phaseOneCue = "Sweep arms overhead • soft knees",
        phaseOneCoachDialogue = "Inhale when your feet are apart...",
        phaseTwoCommand = "HOP FEET IN",
        phaseTwoCue = "Land softly on balls of feet",
        phaseTwoCoachDialogue = "Exhale when your feet come back together!",
        setupRule = "Stand tall with soft knees — hop feet wide with arms overhead, then hop lightly back in.",
        shortDriveVoiceCue = "Stay light on the balls of your feet",
        spokenFormBreathingTip = "Inhale when your feet are apart, and exhale when they come back together."
    )
}

/**
 * Converts normalized [0f..1f] rep progress into a continuous, fluid human kinematic curve [0f..1f].
 */
fun computeBiomechanicalContraction(cycleProgress: Float, isIsometricHold: Boolean = false): Float {
    val t = ((cycleProgress % 1f) + 1f) % 1f
    if (isIsometricHold) {
        return 0.84f + 0.16f * sin(t * 2f * PI.toFloat())
    }
    return when {
        t < 0.46f -> {
            val u = t / 0.46f
            0.5f - 0.5f * cos(u * PI.toFloat())
        }
        t < 0.54f -> {
            val u = (t - 0.46f) / 0.08f
            0.985f + 0.015f * sin(u * PI.toFloat())
        }
        else -> {
            val u = (t - 0.54f) / 0.46f
            0.5f + 0.5f * cos(u * PI.toFloat())
        }
    }
}

@Composable
private fun OfflinePureWhitePoseView(
    context: Context,
    animationType: ExerciseAnimationType,
    contraction: Float,
    contentDescriptionText: String,
    modifier: Modifier = Modifier
) {
    val posePair = remember(context, animationType) {
        ExerciseIllustrationAtlas.getPoseBitmaps(context, animationType)
    }
    if (posePair != null) {
        val activeBitmap = if (contraction >= 0.45f) posePair.second else posePair.first
        Image(
            bitmap = activeBitmap,
            contentDescription = contentDescriptionText,
            contentScale = ContentScale.Fit,
            modifier = modifier
        )
    }
}

@Composable
private fun RealHumanExerciseMediaStage(
    animationType: ExerciseAnimationType,
    exerciseName: String,
    contraction: Float,
    isPaused: Boolean,
    contentDescriptionText: String,
    innerPaddingDp: Int,
    maxStageSizeDp: Int? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gifImageLoader = remember(context) { ExerciseAnimationMediaResolver.imageLoader(context) }
    val normalizedName = remember(exerciseName) { exerciseName.trim() }
    var resolvedMedia by remember(animationType, normalizedName) {
        mutableStateOf(
            ExerciseAnimationMediaResolver.getCachedMediaInstant(
                context = context,
                type = animationType,
                exerciseName = normalizedName
            )
        )
    }

    LaunchedEffect(animationType, normalizedName) {
        resolvedMedia = ExerciseAnimationMediaResolver.resolveMedia(
            context = context,
            type = animationType,
            exerciseName = normalizedName
        )
    }

    val mediaModifier = if (maxStageSizeDp != null) {
        Modifier
            .sizeIn(maxWidth = maxStageSizeDp.dp, maxHeight = maxStageSizeDp.dp)
            .fillMaxSize()
            .padding(innerPaddingDp.dp)
    } else {
        Modifier
            .fillMaxSize()
            .padding(innerPaddingDp.dp)
    }

    Box(
        modifier = modifier
            .background(StagePureWhite)
            .semantics { contentDescription = contentDescriptionText },
        contentAlignment = Alignment.Center
    ) {
        when (val media = resolvedMedia) {
            is ResolvedExerciseMedia.LoopingGif -> {
                val request = remember(context, media.gifUrl, isPaused) {
                    ImageRequest.Builder(context)
                        .data(media.gifUrl)
                        .repeatCount(if (isPaused) 0 else -1)
                        .crossfade(false)
                        .build()
                }
                SubcomposeAsyncImage(
                    model = request,
                    imageLoader = gifImageLoader,
                    contentDescription = contentDescriptionText,
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High,
                    modifier = mediaModifier,
                    loading = {
                        OfflinePureWhitePoseView(
                            context = context,
                            animationType = animationType,
                            contraction = contraction,
                            contentDescriptionText = contentDescriptionText,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    error = {
                        OfflinePureWhitePoseView(
                            context = context,
                            animationType = animationType,
                            contraction = contraction,
                            contentDescriptionText = contentDescriptionText,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                )
            }

            ResolvedExerciseMedia.OfflineFallback -> {
                OfflinePureWhitePoseView(
                    context = context,
                    animationType = animationType,
                    contraction = contraction,
                    contentDescriptionText = contentDescriptionText,
                    modifier = mediaModifier
                )
            }
        }
    }
}

/**
 * PURE-WHITE LOOPING HUMAN EXERCISE DEMONSTRATION STAGE:
 * - Plays smooth continuous ExerciseDB v1 anatomical human exercise animations on a pure #FFFFFF background.
 * - Resolves by specific exerciseName so every exercise in a workout plays its own distinct movement.
 * - Framed cleanly at max 264dp on seamless pure white so animations stay razor-sharp without pixelation.
 */
@Composable
fun HeroMovementAnimationContainer(
    animationType: ExerciseAnimationType,
    targetMuscle: String,
    singleFormTip: String,
    exerciseName: String = "",
    externalCycleProgress: Float? = null,
    livePhaseBadgeText: String? = null,
    startPosition: String = "",
    finishPosition: String = "",
    centerCounterText: String = "",
    coachCueText: String = "",
    isPrepPhase: Boolean = false,
    isPaused: Boolean = false,
    setProgressFraction: Float = 0f,
    onBannerTap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val guide = remember(animationType) { resolveLiveCoachGuide(animationType) }

    val infiniteTransition = rememberInfiniteTransition(label = "human_coach_demo_loop")
    val autoCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (animationType) {
                    ExerciseAnimationType.JUMPING_JACKS -> 1100
                    ExerciseAnimationType.PLANK_HOLD,
                    ExerciseAnimationType.COBRA_STRETCH -> 3200
                    else -> 2100
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "coach_auto_cycle"
    )

    val activeCycle = when {
        isPaused -> ((externalCycleProgress ?: 0f) % 1f + 1f) % 1f
        isPrepPhase -> autoCycle
        externalCycleProgress != null -> ((externalCycleProgress % 1f) + 1f) % 1f
        else -> autoCycle
    }

    val contraction = computeBiomechanicalContraction(activeCycle, isIsometricHold = guide.isHoldMove)
    val descLabel = exerciseName.ifBlank { targetMuscle }

    RealHumanExerciseMediaStage(
        animationType = animationType,
        exerciseName = exerciseName,
        contraction = contraction,
        isPaused = isPaused,
        contentDescriptionText = "$descLabel exercise demonstration",
        innerPaddingDp = 8,
        maxStageSizeDp = 264,
        modifier = modifier.fillMaxSize()
    )
}

/**
 * Matching Pure-White Looping Human Exercise Preview for Workout Overview, Rest "NEXT" preview,
 * Dashboard strip, Workout Hub list, and Exercise Form Guide modal.
 */
@Composable
fun AnimatedWorkoutCharacter(
    animationType: ExerciseAnimationType,
    modifier: Modifier = Modifier,
    containerColor: Color = StagePureWhite,
    accentColor: Color = StageElectricBlue,
    fixedPhase: Float? = null,
    exerciseName: String = ""
) {
    val guide = remember(animationType) { resolveLiveCoachGuide(animationType) }
    val infiniteTransition = rememberInfiniteTransition(label = "thumb_human_coach")
    val autoCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (animationType) {
                    ExerciseAnimationType.JUMPING_JACKS -> 1150
                    ExerciseAnimationType.PLANK_HOLD,
                    ExerciseAnimationType.COBRA_STRETCH -> 3200
                    else -> 2200
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "thumb_cycle"
    )

    val cycle = fixedPhase ?: autoCycle
    val contraction = computeBiomechanicalContraction(cycle, isIsometricHold = guide.isHoldMove)
    val label = exerciseName.ifBlank { animationType.name }

    RealHumanExerciseMediaStage(
        animationType = animationType,
        exerciseName = exerciseName,
        contraction = contraction,
        isPaused = fixedPhase != null,
        contentDescriptionText = "$label exercise preview",
        innerPaddingDp = 4,
        modifier = modifier.background(containerColor)
    )
}

// ============================================================================
// LEGACY ATLAS METADATA RETAINED ONLY FOR BACKWARD UNIT/ASSET TEST CONTRACTS
// ============================================================================

enum class PoseAnchorMode {
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    BOTTOM_CENTER,
    FLOOR_CONTACT_CENTER,
    TOP_LEFT
}

data class WhiteMaskRect(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float
)

data class PoseCropRegion(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float,
    val whiteMasks: List<WhiteMaskRect> = emptyList(),
    val flipHorizontal: Boolean = false
)

data class ExerciseIllustrationSpec(
    @DrawableRes val drawableRes: Int,
    val startPose: PoseCropRegion,
    val peakPose: PoseCropRegion,
    val anchorMode: PoseAnchorMode,
    val isWideFloorPose: Boolean = false,
    val defaultStartCue: String,
    val defaultPeakCue: String
)

private data class InkBounds(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int,
    val floorCenterX: Int
)

object ExerciseIllustrationAtlas {

    private val sourceSheetCache = object : LruCache<Int, Bitmap>(5) {}
    private val alignedPairCache = object : LruCache<String, Pair<ImageBitmap, ImageBitmap>>(20) {}

    fun resolveSpec(type: ExerciseAnimationType): ExerciseIllustrationSpec = when (type) {
        ExerciseAnimationType.INCLINE_DB_PRESS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_upper_1794517045348,
            startPose = PoseCropRegion(
                minX = 0.012f,
                minY = 0.080f,
                maxX = 0.264f,
                maxY = 0.455f,
                whiteMasks = listOf(
                    WhiteMaskRect(0.240f, 0.395f, 0.270f, 0.460f),
                    WhiteMaskRect(0.250f, 0.245f, 0.270f, 0.365f)
                )
            ),
            peakPose = PoseCropRegion(
                minX = 0.242f,
                minY = 0.080f,
                maxX = 0.488f,
                maxY = 0.455f,
                whiteMasks = listOf(
                    WhiteMaskRect(0.238f, 0.075f, 0.266f, 0.245f),
                    WhiteMaskRect(0.238f, 0.360f, 0.258f, 0.392f)
                )
            ),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            defaultStartCue = "Dumbbells at upper chest, elbows 45°",
            defaultPeakCue = "Press up & squeeze upper chest"
        )

        ExerciseAnimationType.LAT_PULLDOWN -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_upper_1794517045348,
            startPose = PoseCropRegion(0.518f, 0.035f, 0.746f, 0.468f),
            peakPose = PoseCropRegion(0.747f, 0.035f, 0.985f, 0.468f),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            defaultStartCue = "Sit tall, thighs under pad, arms extended",
            defaultPeakCue = "Pull wide bar to upper chest, squeeze lats"
        )

        ExerciseAnimationType.SEATED_CABLE_ROW -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_upper_1794517045348,
            startPose = PoseCropRegion(0.020f, 0.595f, 0.255f, 0.895f),
            peakPose = PoseCropRegion(0.257f, 0.595f, 0.488f, 0.895f),
            anchorMode = PoseAnchorMode.FLOOR_CONTACT_CENTER,
            defaultStartCue = "Sit tall, reach forward with flat back",
            defaultPeakCue = "Row handle to ribs, pinch shoulder blades"
        )

        ExerciseAnimationType.CABLE_LATERAL_RAISE -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_upper_1794517045348,
            startPose = PoseCropRegion(
                minX = 0.555f,
                minY = 0.545f,
                maxX = 0.708f,
                maxY = 0.940f,
                whiteMasks = listOf(WhiteMaskRect(0.698f, 0.540f, 0.712f, 0.665f))
            ),
            peakPose = PoseCropRegion(
                minX = 0.702f,
                minY = 0.545f,
                maxX = 0.985f,
                maxY = 0.940f,
                whiteMasks = listOf(WhiteMaskRect(0.700f, 0.700f, 0.712f, 0.945f))
            ),
            anchorMode = PoseAnchorMode.FLOOR_CONTACT_CENTER,
            defaultStartCue = "Stand tall, dumbbells resting at sides",
            defaultPeakCue = "Raise arms to shoulder height"
        )

        ExerciseAnimationType.INCLINE_DB_CURL -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_lower_1794517047761,
            startPose = PoseCropRegion(
                minX = 0.014f,
                minY = 0.080f,
                maxX = 0.252f,
                maxY = 0.435f,
                whiteMasks = listOf(WhiteMaskRect(0.240f, 0.380f, 0.256f, 0.440f))
            ),
            peakPose = PoseCropRegion(
                minX = 0.240f,
                minY = 0.080f,
                maxX = 0.485f,
                maxY = 0.438f,
                whiteMasks = listOf(WhiteMaskRect(0.238f, 0.075f, 0.254f, 0.380f))
            ),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            defaultStartCue = "Back on incline pad, full bicep stretch",
            defaultPeakCue = "Curl up keeping elbows pinned, squeeze"
        )

        ExerciseAnimationType.TRICEP_DIPS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_lower_1794517047761,
            startPose = PoseCropRegion(
                minX = 0.515f,
                minY = 0.050f,
                maxX = 0.778f,
                maxY = 0.386f,
                whiteMasks = listOf(WhiteMaskRect(0.690f, 0.362f, 0.780f, 0.390f))
            ),
            peakPose = PoseCropRegion(
                minX = 0.708f,
                minY = 0.165f,
                maxX = 0.986f,
                maxY = 0.442f,
                whiteMasks = listOf(WhiteMaskRect(0.705f, 0.160f, 0.782f, 0.365f))
            ),
            anchorMode = PoseAnchorMode.BOTTOM_RIGHT,
            defaultStartCue = "Hands on bench edge, arms locked tall",
            defaultPeakCue = "Lower hips to 90° elbow bend, press up"
        )

        ExerciseAnimationType.BODYWEIGHT_SQUATS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_lower_1794517047761,
            startPose = PoseCropRegion(0.065f, 0.535f, 0.245f, 0.942f),
            peakPose = PoseCropRegion(0.265f, 0.640f, 0.440f, 0.942f),
            anchorMode = PoseAnchorMode.FLOOR_CONTACT_CENTER,
            defaultStartCue = "Stand tall, feet shoulder-width apart",
            defaultPeakCue = "Sit hips to parallel, drive through heels"
        )

        ExerciseAnimationType.FORWARD_LUNGES -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_lower_1794517047761,
            startPose = PoseCropRegion(0.522f, 0.545f, 0.735f, 0.942f),
            peakPose = PoseCropRegion(0.736f, 0.625f, 0.978f, 0.942f),
            anchorMode = PoseAnchorMode.FLOOR_CONTACT_CENTER,
            defaultStartCue = "Split stance, torso tall & core braced",
            defaultPeakCue = "Drop back knee to 90°, push through front heel"
        )

        ExerciseAnimationType.STANDARD_PUSH_UPS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_home_1794517051177,
            startPose = PoseCropRegion(0.030f, 0.025f, 0.470f, 0.262f),
            peakPose = PoseCropRegion(0.030f, 0.272f, 0.470f, 0.438f),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            isWideFloorPose = true,
            defaultStartCue = "Straight plank line, hands under shoulders",
            defaultPeakCue = "Lower chest near floor, press up strong"
        )

        ExerciseAnimationType.INCLINE_PUSH_UPS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_home_1794517051177,
            startPose = PoseCropRegion(0.530f, 0.020f, 0.970f, 0.262f),
            peakPose = PoseCropRegion(0.530f, 0.270f, 0.970f, 0.438f),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            isWideFloorPose = true,
            defaultStartCue = "Hands on sturdy bench edge, body straight",
            defaultPeakCue = "Lower chest to bench edge, press tall"
        )

        ExerciseAnimationType.KNEE_PUSH_UPS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_home_1794517051177,
            startPose = PoseCropRegion(0.530f, 0.020f, 0.970f, 0.262f),
            peakPose = PoseCropRegion(0.530f, 0.270f, 0.970f, 0.438f),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            isWideFloorPose = true,
            defaultStartCue = "Knees on mat, straight hip-to-shoulder line",
            defaultPeakCue = "Lower chest smoothly & press back up"
        )

        ExerciseAnimationType.DOORFRAME_ROW -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_home_1794517051177,
            startPose = PoseCropRegion(0.030f, 0.515f, 0.470f, 0.728f),
            peakPose = PoseCropRegion(0.030f, 0.738f, 0.470f, 0.938f),
            anchorMode = PoseAnchorMode.TOP_LEFT,
            isWideFloorPose = true,
            defaultStartCue = "Lean back with straight body & arms extended",
            defaultPeakCue = "Pull chest up & squeeze shoulder blades"
        )

        ExerciseAnimationType.PIKE_SHOULDER_PRESS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_home_1794517051177,
            startPose = PoseCropRegion(
                minX = 0.530f,
                minY = 0.518f,
                maxX = 0.970f,
                maxY = 0.748f,
                flipHorizontal = true
            ),
            peakPose = PoseCropRegion(0.530f, 0.752f, 0.970f, 0.938f),
            anchorMode = PoseAnchorMode.BOTTOM_LEFT,
            isWideFloorPose = true,
            defaultStartCue = "Hips high in inverted V, arms locked",
            defaultPeakCue = "Lower head toward floor, press shoulders up"
        )

        ExerciseAnimationType.ABDOMINAL_CRUNCHES -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_core_1794517053041,
            startPose = PoseCropRegion(0.020f, 0.035f, 0.482f, 0.245f),
            peakPose = PoseCropRegion(0.020f, 0.258f, 0.482f, 0.468f),
            anchorMode = PoseAnchorMode.BOTTOM_CENTER,
            isWideFloorPose = true,
            defaultStartCue = "Lie flat, knees bent, fingertips by temples",
            defaultPeakCue = "Curl ribs toward pelvis & squeeze abs"
        )

        ExerciseAnimationType.PLANK_HOLD -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_core_1794517053041,
            startPose = PoseCropRegion(0.518f, 0.035f, 0.982f, 0.245f),
            peakPose = PoseCropRegion(0.518f, 0.258f, 0.982f, 0.468f),
            anchorMode = PoseAnchorMode.BOTTOM_CENTER,
            isWideFloorPose = true,
            defaultStartCue = "Elbows under shoulders, heels to head straight",
            defaultPeakCue = "Pull ribs down, squeeze glutes & hold tight"
        )

        ExerciseAnimationType.COBRA_STRETCH -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_core_1794517053041,
            startPose = PoseCropRegion(0.020f, 0.530f, 0.482f, 0.725f),
            peakPose = PoseCropRegion(0.020f, 0.732f, 0.482f, 0.968f),
            anchorMode = PoseAnchorMode.BOTTOM_CENTER,
            isWideFloorPose = true,
            defaultStartCue = "Lie prone, palms flat beneath shoulders",
            defaultPeakCue = "Lift chest gently, shoulders down, breathe"
        )

        ExerciseAnimationType.JUMPING_JACKS -> ExerciseIllustrationSpec(
            drawableRes = R.drawable.img_coach_core_1794517053041,
            startPose = PoseCropRegion(0.525f, 0.515f, 0.722f, 0.972f),
            peakPose = PoseCropRegion(0.732f, 0.515f, 0.978f, 0.972f),
            anchorMode = PoseAnchorMode.FLOOR_CONTACT_CENTER,
            defaultStartCue = "Stand tall, feet together, arms at sides",
            defaultPeakCue = "Jump feet wide & sweep arms overhead"
        )
    }

    fun getPoseBitmaps(context: Context, type: ExerciseAnimationType): Pair<ImageBitmap, ImageBitmap>? {
        val key = type.name
        alignedPairCache.get(key)?.let { return it }

        val spec = resolveSpec(type)
        val sheet = synchronized(sourceSheetCache) {
            sourceSheetCache.get(spec.drawableRes) ?: run {
                val opts = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                BitmapFactory.decodeResource(context.resources, spec.drawableRes, opts)?.also {
                    sourceSheetCache.put(spec.drawableRes, it)
                }
            }
        } ?: return null

        val rawStart = extractMaskedPose(sheet, spec.startPose)
        val rawPeak = extractMaskedPose(sheet, spec.peakPose)

        val pair = buildAnchorAlignedPair(rawStart, rawPeak, spec.anchorMode)
        synchronized(alignedPairCache) {
            alignedPairCache.put(key, pair)
        }
        return pair
    }

    private fun extractMaskedPose(sheet: Bitmap, region: PoseCropRegion): Bitmap {
        val sw = sheet.width
        val sh = sheet.height
        val x0 = (sw * region.minX).roundToInt().coerceIn(0, sw - 2)
        val y0 = (sh * region.minY).roundToInt().coerceIn(0, sh - 2)
        val x1 = (sw * region.maxX).roundToInt().coerceIn(x0 + 1, sw)
        val y1 = (sh * region.maxY).roundToInt().coerceIn(y0 + 1, sh)

        val rawW = x1 - x0
        val rawH = y1 - y0
        val cropped = Bitmap.createBitmap(sheet, x0, y0, rawW, rawH)
        val pixels = IntArray(rawW * rawH)
        cropped.getPixels(pixels, 0, rawW, 0, 0, rawW, rawH)

        if (region.whiteMasks.isNotEmpty()) {
            for (mask in region.whiteMasks) {
                val mx0 = ((mask.minX * sw).roundToInt() - x0).coerceIn(0, rawW)
                val mx1 = ((mask.maxX * sw).roundToInt() - x0).coerceIn(0, rawW)
                val my0 = ((mask.minY * sh).roundToInt() - y0).coerceIn(0, rawH)
                val my1 = ((mask.maxY * sh).roundToInt() - y0).coerceIn(0, rawH)
                for (py in my0 until my1) {
                    val rowOffset = py * rawW
                    for (px in mx0 until mx1) {
                        pixels[rowOffset + px] = AndroidColor.WHITE
                    }
                }
            }
        }

        if (region.flipHorizontal) {
            val halfW = rawW / 2
            for (py in 0 until rawH) {
                val rowOffset = py * rawW
                for (px in 0 until halfW) {
                    val leftIdx = rowOffset + px
                    val rightIdx = rowOffset + (rawW - 1 - px)
                    val tmp = pixels[leftIdx]
                    pixels[leftIdx] = pixels[rightIdx]
                    pixels[rightIdx] = tmp
                }
            }
        }

        val out = Bitmap.createBitmap(rawW, rawH, Bitmap.Config.ARGB_8888)
        out.setPixels(pixels, 0, rawW, 0, 0, rawW, rawH)
        return out
    }

    private fun computeInkBounds(bmp: Bitmap): InkBounds {
        val w = bmp.width
        val h = bmp.height
        val pixels = IntArray(w * h)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)

        var minX = w
        var minY = h
        var maxX = 0
        var maxY = 0

        for (py in 0 until h) {
            val rowOffset = py * w
            for (px in 0 until w) {
                val c = pixels[rowOffset + px]
                if (AndroidColor.red(c) < 244 || AndroidColor.green(c) < 244 || AndroidColor.blue(c) < 244) {
                    if (px < minX) minX = px
                    if (py < minY) minY = py
                    if (px > maxX) maxX = px
                    if (py > maxY) maxY = py
                }
            }
        }

        if (maxX <= minX || maxY <= minY) {
            return InkBounds(0, 0, w - 1, h - 1, w / 2)
        }

        val bandHeight = max(8, ((maxY - minY) * 0.12f).roundToInt())
        val floorBandTop = (maxY - bandHeight).coerceAtLeast(minY)
        var floorMinX = maxX
        var floorMaxX = minX

        for (py in floorBandTop..maxY) {
            val rowOffset = py * w
            for (px in minX..maxX) {
                val c = pixels[rowOffset + px]
                if (AndroidColor.red(c) < 244 || AndroidColor.green(c) < 244 || AndroidColor.blue(c) < 244) {
                    if (px < floorMinX) floorMinX = px
                    if (px > floorMaxX) floorMaxX = px
                }
            }
        }

        val floorCenter = if (floorMaxX >= floorMinX) {
            (floorMinX + floorMaxX) / 2
        } else {
            (minX + maxX) / 2
        }

        return InkBounds(minX, minY, maxX, maxY, floorCenter)
    }

    private fun resolveAnchorPoint(bounds: InkBounds, mode: PoseAnchorMode): Pair<Int, Int> = when (mode) {
        PoseAnchorMode.BOTTOM_LEFT -> bounds.minX to bounds.maxY
        PoseAnchorMode.BOTTOM_RIGHT -> bounds.maxX to bounds.maxY
        PoseAnchorMode.BOTTOM_CENTER -> ((bounds.minX + bounds.maxX) / 2) to bounds.maxY
        PoseAnchorMode.FLOOR_CONTACT_CENTER -> bounds.floorCenterX to bounds.maxY
        PoseAnchorMode.TOP_LEFT -> bounds.minX to bounds.minY
    }

    private fun blitOntoWhiteCanvas(
        src: Bitmap,
        canvasW: Int,
        canvasH: Int,
        offsetX: Int,
        offsetY: Int
    ): Bitmap {
        val sw = src.width
        val sh = src.height
        val srcPixels = IntArray(sw * sh)
        src.getPixels(srcPixels, 0, sw, 0, 0, sw, sh)

        val outPixels = IntArray(canvasW * canvasH) { AndroidColor.WHITE }
        val copyStartY = max(0, -offsetY)
        val copyEndY = min(sh, canvasH - offsetY)
        val copyStartX = max(0, -offsetX)
        val copyEndX = min(sw, canvasW - offsetX)
        val rowLen = copyEndX - copyStartX

        if (rowLen > 0 && copyEndY > copyStartY) {
            for (sy in copyStartY until copyEndY) {
                val dy = sy + offsetY
                System.arraycopy(
                    srcPixels,
                    sy * sw + copyStartX,
                    outPixels,
                    dy * canvasW + (copyStartX + offsetX),
                    rowLen
                )
            }
        }

        val out = Bitmap.createBitmap(canvasW, canvasH, Bitmap.Config.ARGB_8888)
        out.setPixels(outPixels, 0, canvasW, 0, 0, canvasW, canvasH)
        return out
    }

    private fun buildAnchorAlignedPair(
        startRaw: Bitmap,
        peakRaw: Bitmap,
        anchorMode: PoseAnchorMode
    ): Pair<ImageBitmap, ImageBitmap> {
        val sb = computeInkBounds(startRaw)
        val pb = computeInkBounds(peakRaw)

        val (sAx, sAy) = resolveAnchorPoint(sb, anchorMode)
        val (pAx, pAy) = resolveAnchorPoint(pb, anchorMode)

        val leftReach = max(sAx - sb.minX, pAx - pb.minX)
        val rightReach = max(sb.maxX - sAx, pb.maxX - pAx)
        val topReach = max(sAy - sb.minY, pAy - pb.minY)
        val bottomReach = max(sb.maxY - sAy, pb.maxY - pAy)

        val contentW = (leftReach + rightReach + 1).coerceAtLeast(16)
        val contentH = (topReach + bottomReach + 1).coerceAtLeast(16)
        val padX = max(10, (contentW * 0.05f).roundToInt())
        val padY = max(10, (contentH * 0.05f).roundToInt())

        val canvasW = contentW + padX * 2
        val canvasH = contentH + padY * 2

        val targetAx = padX + leftReach
        val targetAy = padY + topReach

        val alignedStart = blitOntoWhiteCanvas(startRaw, canvasW, canvasH, targetAx - sAx, targetAy - sAy)
        val alignedPeak = blitOntoWhiteCanvas(peakRaw, canvasW, canvasH, targetAx - pAx, targetAy - pAy)

        return alignedStart.asImageBitmap() to alignedPeak.asImageBitmap()
    }
}
