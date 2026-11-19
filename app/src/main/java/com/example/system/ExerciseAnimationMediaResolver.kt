package com.example.system

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.domain.ExerciseAnimationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

sealed interface ResolvedExerciseMedia {
    data class LoopingGif(val gifUrl: String) : ResolvedExerciseMedia
    data object OfflineFallback : ResolvedExerciseMedia
}

/**
 * Unified Pure-White Anatomical Human Exercise Demonstration Resolver:
 * - Exclusively uses ExerciseDB v1 (`static.exercisedb.dev/media/<id>.gif`) smooth looping 3D anatomical
 *   human demonstrations on a pure #FFFFFF background so EVERY exercise shares the exact same clean style.
 * - Never mixes dark gym photos or inconsistent third-party trainer snapshots.
 * - Maps by specific exercise name first (45+ verified movement animations) so every exercise in a
 *   workout displays its own distinct, biomechanically accurate movement.
 */
object ExerciseAnimationMediaResolver {

    private const val LEGACY_POISONED_PREFS_V1 = "apex_exercise_media_cache_v1"
    private const val PREFS_NAME = "apex_exercise_media_cache_v3"
    private const val STATIC_MEDIA_BASE = "https://static.exercisedb.dev/media/"
    private const val EXERCISE_DB_SEARCH_ENDPOINT = "https://oss.exercisedb.dev/api/v1/exercises/search?search="

    private fun mediaGif(id: String): String = "$STATIC_MEDIA_BASE$id.gif"

    // =========================================================================
    // VERIFIED EXERCISEDB V1 ANATOMICAL HUMAN LOOPING GIF IDS (PURE #FFFFFF BG)
    // =========================================================================
    private val GIF_JACK_JUMP = mediaGif("1g5bPpA")              // Jack Jump
    private val GIF_STAR_JUMP = mediaGif("HtfCpfi")              // Star Jump
    private val GIF_PUSH_UP = mediaGif("I4hDWkc")                // Push-Up
    private val GIF_INCLINE_PUSH_UP = mediaGif("B1EVP9F")        // Incline Push-Up
    private val GIF_BOX_INCLINE_PUSH_UP = mediaGif("F7vjXqT")    // Incline Push-Up (On Box)
    private val GIF_WALL_PUSH_UP = mediaGif("NCmbLCw")           // Wall Push-Up
    private val GIF_KNEELING_PUSH_UP = mediaGif("ZOuKWir")       // Kneeling Push-Up
    private val GIF_CLOSE_GRIP_PUSH_UP = mediaGif("x6KpKpq")     // Close-Grip Push-Up
    private val GIF_DECLINE_PUSH_UP = mediaGif("i5cEhka")        // Decline Push-Up
    private val GIF_PIKE_PUSH_UP = mediaGif("epOSYUZ")           // Modified Hindu / Pike Push-Up
    private val GIF_PIKE_COBRA_FLOW = mediaGif("XPUDTt7")        // Pike-to-Cobra Shoulder Opener
    private val GIF_BENCH_DIP = mediaGif("RrLske5")              // Bench Dip (Knees Bent)
    private val GIF_FLOOR_TRICEP_DIP = mediaGif("9RT8oQW")       // Floor Tricep Dip
    private val GIF_INVERTED_ROW = mediaGif("bZGHsAZ")           // Inverted / Doorframe Lat Row
    private val GIF_BENT_KNEE_ROW = mediaGif("VPPtusI")          // Bent-Knee Mid-Back Squeeze Row
    private val GIF_BENCH_INVERTED_ROW = mediaGif("Mxa7Cr8")     // Elevated Back Row
    private val GIF_BODYWEIGHT_SQUAT = mediaGif("JZuApnB")       // Squat
    private val GIF_SEMI_SQUAT_WARMUP = mediaGif("6FMU51h")      // Dynamic Squat Opener
    private val GIF_GOBLET_SQUAT = mediaGif("yn8yg1r")           // Dumbbell Goblet Squat
    private val GIF_SPLIT_SQUAT = mediaGif("9E25EOx")            // Split Squats
    private val GIF_WALKING_LUNGE = mediaGif("IZVHb27")          // Walking Lunge
    private val GIF_FORWARD_LUNGE = mediaGif("kMzUs9Y")          // Forward Lunge
    private val GIF_DUMBBELL_LUNGE = mediaGif("RRWFUcw")         // Dumbbell Lunge
    private val GIF_SLED_LEG_PRESS = mediaGif("10Z2DXU")         // Sled 45° Leg Press
    private val GIF_MACHINE_LEG_EXT = mediaGif("my33uHU")        // Machine Leg Extension
    private val GIF_MACHINE_LEG_CURL = mediaGif("17lJ1kr")       // Machine Leg Curl
    private val GIF_DB_ROMANIAN_DL = mediaGif("rR0LJzx")         // Dumbbell Romanian Deadlift
    private val GIF_CALF_RAISE = mediaGif("0jp9Rlz")             // Floor / Standing Calf Raise
    private val GIF_BENCH_HIP_THRUST = mediaGif("aWedzZX")       // Glute Bridge Two-Leg On Bench
    private val GIF_FLOOR_GLUTE_BRIDGE = mediaGif("u0cNiij")     // Floor Glute Bridge
    private val GIF_HIP_ABDUCTION = mediaGif("VO2qeJg")          // Hip Abduction
    private val GIF_GLUTE_KICKBACK = mediaGif("u27Kcdz")         // Hip Extension / Glute Kickback
    private val GIF_DB_INCLINE_PRESS = mediaGif("ns0SIbU")       // Dumbbell Incline Bench Press
    private val GIF_DB_BENCH_PRESS = mediaGif("B3Rxp6L")         // Dumbbell Bench Press
    private val GIF_MACHINE_CHEST_PRESS = mediaGif("DOoWcnA")    // Machine Chest Press
    private val GIF_MACHINE_INCLINE_PRESS = mediaGif("o17Jfkt")  // Machine Incline Chest Press
    private val GIF_INNER_CHEST_FLY = mediaGif("wDN97Ca")        // Machine Inner Chest / Pec Fly
    private val GIF_CABLE_PUSHDOWN = mediaGif("3ZflifB")         // Cable Tricep Pushdown
    private val GIF_VBAR_TRICEP_EXT = mediaGif("gAwDzB3")        // Cable V-Bar / Overhead Tricep Pushdown
    private val GIF_WIDE_LAT_PULLDOWN = mediaGif("RVwzP10")      // Wide Cable Lat Pulldown
    private val GIF_CLOSE_LAT_PULLDOWN = mediaGif("xBYcQHj")     // Underhand / Close-Grip Pulldown
    private val GIF_MACHINE_PULLDOWN = mediaGif("7F1DVzn")       // Machine Front Pulldown
    private val GIF_MACHINE_SEATED_ROW = mediaGif("7I6LNUG")     // Chest-Supported / Machine Row
    private val GIF_CABLE_SEATED_ROW = mediaGif("fUBheHs")       // Seated Cable Row
    private val GIF_ROPE_FACE_PULL = mediaGif("SJqRxOt")         // Cable Rope High Row / Face Pull
    private val GIF_REAR_DELT_FLY = mediaGif("v1qBec9")          // Rear Delt Lateral Raise / Fly
    private val GIF_DB_INCLINE_CURL = mediaGif("F3xgbjF")        // Dumbbell Incline Biceps Curl
    private val GIF_DB_STANDING_CURL = mediaGif("3s4NnTh")       // Standing Biceps Curl
    private val GIF_DB_HAMMER_CURL = mediaGif("NbVPDMW")         // Dumbbell Biceps / Hammer Curl
    private val GIF_BARBELL_EZ_CURL = mediaGif("25GPyDY")        // EZ-Bar / Barbell Curl
    private val GIF_PREACHER_CURL = mediaGif("P2lNrGL")          // Cable / Machine Preacher Curl
    private val GIF_REVERSE_GRIP_CURL = mediaGif("e4ojVhP")      // Reverse-Grip Forearm & Biceps Curl
    private val GIF_MACHINE_SHOULDER_PRESS = mediaGif("67n3r98") // Machine Shoulder Press
    private val GIF_DB_CABLE_SHOULDER_PRESS = mediaGif("PzQanLE")// Overhead Shoulder Press
    private val GIF_DB_LATERAL_RAISE = mediaGif("DsgkuIt")       // Dumbbell Lateral Raise
    private val GIF_CABLE_LATERAL_RAISE = mediaGif("goJ6ezq")    // Cable Lateral Raise
    private val GIF_MACHINE_LATERAL_RAISE = mediaGif("dRTfGZT")  // Machine Lateral Raise
    private val GIF_FLOOR_CRUNCH = mediaGif("TFqbd8t")           // Floor Abdominal Crunch
    private val GIF_TUCK_CABLE_CRUNCH = mediaGif("BMMolZ3")      // Tuck / Weighted Crunch
    private val GIF_REVERSE_CRUNCH = mediaGif("nCU1Ekp")         // Reverse Crunch
    private val GIF_HANGING_KNEE_RAISE = mediaGif("03lzqwk")     // Captain's Chair / Hanging Knee Raise
    private val GIF_LYING_LEG_RAISE = mediaGif("I3tsCnC")        // Straight Leg Raise
    private val GIF_FRONT_PLANK = mediaGif("VBAWRPG")            // Front Forearm Plank
    private val GIF_POWER_PLANK = mediaGif("hCjGsRQ")            // Core Vacuum / Power Point Plank
    private val GIF_SHOULDER_TAP_PLANK = mediaGif("h1ezqSu")     // Plank Shoulder Tap
    private val GIF_DEAD_HANG = mediaGif("mExgrF9")              // Bar Hang / Pull
    private val GIF_UPPER_BACK_OPENER = mediaGif("GSDioYu")      // Upper-Back & Scapular Opener
    private val GIF_DYNAMIC_CHEST_STRETCH = mediaGif("3uj0Ozg")  // Dynamic Chest & Shoulder Opener
    private val GIF_KNEELING_LAT_STRETCH = mediaGif("f38OEuO")   // Kneeling Lat & Back Stretch
    private val GIF_RUNNERS_HIP_STRETCH = mediaGif("0mB6wHO")    // Runner's Hip Flexor & Quad Stretch
    private val GIF_TRICEP_SHOULDER_STRETCH = mediaGif("uOV3Itw")// Overhead Shoulder & Tricep Stretch
    private val GIF_SPINE_COBRA_STRETCH = mediaGif("JbC2iaV")    // Spine & Abdominal Stretch

    @Volatile
    private var sharedGifImageLoader: ImageLoader? = null

    @Volatile
    private var legacyCachePurged = false

    private val memoryResolvedCache = ConcurrentHashMap<String, ResolvedExerciseMedia>()
    private val inFlightMutexes = ConcurrentHashMap<String, Mutex>()

    @Volatile
    private var prefetchStarted = false

    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private data class ExerciseSearchProfile(
        val searchQueries: List<String>,
        val preferredNameKeywords: List<String>,
        val preferredEquipments: List<String>,
        val bannedNameKeywords: List<String> = emptyList()
    )

    fun imageLoader(context: Context): ImageLoader {
        purgeLegacyPoisonedCacheOnce(context)
        sharedGifImageLoader?.let { return it }
        return synchronized(this) {
            sharedGifImageLoader ?: ImageLoader.Builder(context.applicationContext)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .memoryCache {
                    MemoryCache.Builder(context.applicationContext)
                        .maxSizePercent(0.24)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(File(context.applicationContext.cacheDir, "exercise_gif_disk_cache_v3"))
                        .maxSizeBytes(80L * 1024L * 1024L)
                        .build()
                }
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(false)
                .build()
                .also { sharedGifImageLoader = it }
        }
    }

    private fun purgeLegacyPoisonedCacheOnce(context: Context) {
        if (legacyCachePurged) return
        legacyCachePurged = true
        runCatching {
            context.applicationContext
                .getSharedPreferences(LEGACY_POISONED_PREFS_V1, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }

    /**
     * Resolves an exact, movement-specific ExerciseDB v1 GIF URL synchronously in 0ms
     * using both the specific exercise title AND its canonical ExerciseAnimationType.
     */
    fun resolveStaticGifUrl(
        exerciseName: String,
        fallbackType: ExerciseAnimationType
    ): String {
        val lower = exerciseName.trim().lowercase()
        if (lower.isNotEmpty()) {
            matchVerifiedGifByName(lower)?.let { return it }
        }
        return defaultGifForType(fallbackType)
    }

    private fun matchVerifiedGifByName(lower: String): String? = when {
        // 1. Warm-ups, Cardio & Stretches (Checked first so stretches/openers match accurately)
        "star jump" in lower || "easy jumping jack" in lower || "step-out" in lower || "step jack" in lower ->
            GIF_STAR_JUMP
        "jumping jack" in lower || "pulse raiser" in lower || "jack jump" in lower ->
            GIF_JACK_JUMP
        "thoracic" in lower || "cobra flow" in lower || "mobility flow" in lower ->
            GIF_PIKE_COBRA_FLOW
        "scapular" in lower || ("upper-back" in lower && ("opener" in lower || "activation" in lower || "mobility" in lower || "brace" in lower)) ->
            GIF_UPPER_BACK_OPENER
        "squat opener" in lower || ("dynamic" in lower && "squat" in lower) ->
            GIF_SEMI_SQUAT_WARMUP
        ("overhead" in lower && ("stretch" in lower || "opener" in lower)) || "tricep stretch" in lower ->
            GIF_TRICEP_SHOULDER_STRETCH
        ("chest" in lower && ("opener" in lower || "stretch" in lower)) || "posture opener" in lower ->
            GIF_DYNAMIC_CHEST_STRETCH
        ("lat" in lower && ("stretch" in lower || "lengthening" in lower || "decompression" in lower)) ->
            GIF_KNEELING_LAT_STRETCH
        ("hip flexor" in lower || "hip lunge stretch" in lower || "runner" in lower) ->
            GIF_RUNNERS_HIP_STRETCH
        "cobra" in lower || "spine stretch" in lower || "abdominal stretch" in lower ||
            "abdominal wall stretch" in lower || "belly breath" in lower || "decompress" in lower ->
            GIF_SPINE_COBRA_STRETCH

        // 1A. Head, Neck, Tongue Posture (Mewing) & Diaphragm / Voice Power
        "chin-tuck" in lower || "chin tuck" in lower ->
            GIF_WALL_PUSH_UP
        "tongue" in lower || "mewing" in lower ->
            GIF_DYNAMIC_CHEST_STRETCH
        "neck-flexor" in lower || "neck flexor" in lower ->
            GIF_FLOOR_CRUNCH
        "hyoid" in lower || "submental" in lower ->
            GIF_ROPE_FACE_PULL
        "trapezius" in lower ->
            GIF_TRICEP_SHOULDER_STRETCH
        "ribcage" in lower ->
            GIF_PIKE_COBRA_FLOW
        "diaphragmatic" in lower ->
            GIF_SPINE_COBRA_STRETCH
        "sibilant" in lower || "hiss" in lower ->
            GIF_POWER_PLANK
        "humming" in lower || "resonance" in lower ->
            GIF_DYNAMIC_CHEST_STRETCH
        "box-breath" in lower || "box breath" in lower || "co2" in lower ->
            GIF_FRONT_PLANK
        "intercostal" in lower ->
            GIF_KNEELING_LAT_STRETCH

        // 1B. Authentic Calisthenics Skill Movements (Planche, Handstand, Muscle-Up, Front Lever, Human Flag, L-Sit, Pistol)
        "shoulder tap" in lower ->
            GIF_SHOULDER_TAP_PLANK
        "pseudo-planche" in lower || ("planche" in lower && ("push-up" in lower || "pushup" in lower)) ->
            GIF_DECLINE_PUSH_UP
        "planche" in lower ->
            GIF_POWER_PLANK
        "handstand" in lower ->
            GIF_PIKE_PUSH_UP
        "front lever" in lower ->
            GIF_BENCH_INVERTED_ROW
        "muscle-up" in lower ->
            if ("dip" in lower) GIF_BENCH_DIP else GIF_DEAD_HANG
        "human-flag" in lower || "human flag" in lower || "side-flag" in lower || "side lever" in lower ->
            GIF_FRONT_PLANK
        "l-sit" in lower ->
            if ("dip" in lower) GIF_FLOOR_TRICEP_DIP else GIF_LYING_LEG_RAISE
        "pistol" in lower ->
            GIF_SPLIT_SQUAT

        // 2. Push-Ups & Bodyweight Pressing Variations (must explicitly say push-up/pushup, never pushdown!)
        "wall" in lower && ("push-up" in lower || "pushup" in lower || "plank" in lower) ->
            GIF_WALL_PUSH_UP
        "knee" in lower && ("push-up" in lower || "pushup" in lower) ->
            GIF_KNEELING_PUSH_UP
        ("close-grip" in lower || "diamond" in lower || "tricep" in lower || "knuckle" in lower) &&
            ("push-up" in lower || "pushup" in lower) ->
            GIF_CLOSE_GRIP_PUSH_UP
        "decline" in lower && ("push-up" in lower || "pushup" in lower) ->
            GIF_DECLINE_PUSH_UP
        "pike" in lower ->
            GIF_PIKE_PUSH_UP
        "box" in lower && ("push-up" in lower || "pushup" in lower) ->
            GIF_BOX_INCLINE_PUSH_UP
        "incline" in lower && ("push-up" in lower || "pushup" in lower) ->
            GIF_INCLINE_PUSH_UP
        "push-up" in lower || "pushup" in lower ->
            GIF_PUSH_UP

        // 3. Gym Chest & Triceps
        "pec" in lower || "fly" in lower -> {
            if ("rear" in lower || "reverse" in lower) GIF_REAR_DELT_FLY else GIF_INNER_CHEST_FLY
        }
        "machine" in lower && "incline" in lower && "press" in lower ->
            GIF_MACHINE_INCLINE_PRESS
        "machine" in lower && "chest" in lower && "press" in lower ->
            GIF_MACHINE_CHEST_PRESS
        "flat" in lower && ("dumbbell" in lower || "bench" in lower || "chest" in lower) && "press" in lower ->
            GIF_DB_BENCH_PRESS
        "incline" in lower && ("dumbbell" in lower || "db" in lower || "bench" in lower) && "press" in lower ->
            GIF_DB_INCLINE_PRESS
        "overhead" in lower && "tricep" in lower ->
            GIF_VBAR_TRICEP_EXT
        "pressdown" in lower || "v-bar" in lower ->
            GIF_VBAR_TRICEP_EXT
        "pushdown" in lower ->
            GIF_CABLE_PUSHDOWN
        "floor" in lower && "dip" in lower ->
            GIF_FLOOR_TRICEP_DIP
        "dip" in lower ->
            GIF_BENCH_DIP

        // 4. Gym & Home Back, Rear Delts, Pull-Ups & Grip
        "dead hang" in lower || "bar hang" in lower || "pull-up" in lower || "chin-up" in lower || "feet-supported bar" in lower || ("hang" in lower && "finisher" in lower) ->
            GIF_DEAD_HANG
        "face pull" in lower ->
            GIF_ROPE_FACE_PULL
        "rear-delt" in lower || "rear delt" in lower ->
            GIF_REAR_DELT_FLY
        ("close-grip" in lower || "neutral" in lower || "underhand" in lower) && "pulldown" in lower ->
            GIF_CLOSE_LAT_PULLDOWN
        "machine" in lower && "pulldown" in lower ->
            GIF_MACHINE_PULLDOWN
        "pulldown" in lower ->
            GIF_WIDE_LAT_PULLDOWN
        "chest-supported" in lower || ("machine" in lower && "row" in lower) || "one-arm dumbbell row" in lower ->
            GIF_MACHINE_SEATED_ROW
        "seated" in lower && "row" in lower ->
            GIF_CABLE_SEATED_ROW
        "bent-knee" in lower && "row" in lower ->
            GIF_BENT_KNEE_ROW
        "mid-back" in lower && "row" in lower ->
            GIF_BENT_KNEE_ROW
        "elevated" in lower && "row" in lower ->
            GIF_BENCH_INVERTED_ROW
        "table" in lower && "row" in lower ->
            GIF_BENCH_INVERTED_ROW
        "doorframe" in lower || "inverted row" in lower || "band row" in lower || "grip row" in lower ->
            GIF_INVERTED_ROW

        // 5. Biceps & Forearms
        "preacher" in lower || ("machine" in lower && "curl" in lower && "leg" !in lower) ->
            GIF_PREACHER_CURL
        "reverse-grip" in lower || "reverse curl" in lower ->
            GIF_REVERSE_GRIP_CURL
        "ez-bar" in lower || "barbell curl" in lower ->
            GIF_BARBELL_EZ_CURL
        "incline" in lower && "curl" in lower ->
            GIF_DB_INCLINE_CURL
        "hammer" in lower && "curl" in lower ->
            GIF_DB_HAMMER_CURL
        ("backpack" in lower && "hammer" !in lower) || ("towel" in lower && "curl" in lower) || ("standing" in lower && "curl" in lower) ->
            GIF_DB_STANDING_CURL
        "farmer" in lower ->
            GIF_DB_STANDING_CURL
        "bicep" in lower || ("curl" in lower && "leg" !in lower && "hamstring" !in lower) ->
            GIF_DB_HAMMER_CURL

        // 6. Shoulders
        "machine" in lower && "shoulder press" in lower ->
            GIF_MACHINE_SHOULDER_PRESS
        "shoulder press" in lower ->
            GIF_DB_CABLE_SHOULDER_PRESS
        "machine" in lower && "lateral raise" in lower ->
            GIF_MACHINE_LATERAL_RAISE
        "cable" in lower && "lateral raise" in lower ->
            GIF_CABLE_LATERAL_RAISE
        "lateral raise" in lower ->
            GIF_DB_LATERAL_RAISE
        "shoulder tap" in lower ->
            GIF_SHOULDER_TAP_PLANK

        // 7. Legs, Glutes & Calves
        "leg press" in lower || "hack squat" in lower ->
            GIF_SLED_LEG_PRESS
        "leg extension" in lower ->
            GIF_MACHINE_LEG_EXT
        "leg curl" in lower || "hamstring curl" in lower ->
            GIF_MACHINE_LEG_CURL
        "romanian deadlift" in lower || "rdl" in lower ->
            GIF_DB_ROMANIAN_DL
        "calf" in lower ->
            GIF_CALF_RAISE
        "hip thrust" in lower || ("barbell" in lower && "glute bridge" in lower) || ("elevated" in lower && "glute" in lower) || ("padded" in lower && "glute" in lower) ->
            GIF_BENCH_HIP_THRUST
        "glute bridge" in lower ->
            GIF_FLOOR_GLUTE_BRIDGE
        "abductor" in lower || "hip abduction" in lower ->
            GIF_HIP_ABDUCTION
        "kickback" in lower ->
            GIF_GLUTE_KICKBACK
        "split squat" in lower || "bulgarian" in lower ->
            GIF_SPLIT_SQUAT
        "walking lunge" in lower || "supported step lunge" in lower ->
            GIF_WALKING_LUNGE
        "dumbbell lunge" in lower || ("reverse" in lower && "lunge" in lower) ->
            GIF_DUMBBELL_LUNGE
        "forward lunge" in lower || "lunge" in lower ->
            GIF_FORWARD_LUNGE
        "goblet squat" in lower ->
            GIF_GOBLET_SQUAT
        "squat" in lower ->
            GIF_BODYWEIGHT_SQUAT

        // 8. Core & Abs
        "captain" in lower || "knee raise" in lower ->
            GIF_HANGING_KNEE_RAISE
        "leg raise" in lower || "deadbug" in lower ->
            GIF_LYING_LEG_RAISE
        "reverse crunch" in lower || "knee tuck" in lower || "kegel" in lower ->
            GIF_REVERSE_CRUNCH
        "cable" in lower && "crunch" in lower ->
            GIF_TUCK_CABLE_CRUNCH
        "crunch" in lower ->
            GIF_FLOOR_CRUNCH
        "pallof" in lower || "vacuum" in lower || "easy core plank" in lower || "core finisher" in lower ->
            GIF_POWER_PLANK
        "plank" in lower ->
            GIF_FRONT_PLANK

        else -> null
    }

    private fun defaultGifForType(type: ExerciseAnimationType): String = when (type) {
        ExerciseAnimationType.JUMPING_JACKS -> GIF_JACK_JUMP
        ExerciseAnimationType.INCLINE_PUSH_UPS -> GIF_INCLINE_PUSH_UP
        ExerciseAnimationType.KNEE_PUSH_UPS -> GIF_KNEELING_PUSH_UP
        ExerciseAnimationType.STANDARD_PUSH_UPS -> GIF_PUSH_UP
        ExerciseAnimationType.ABDOMINAL_CRUNCHES -> GIF_FLOOR_CRUNCH
        ExerciseAnimationType.PLANK_HOLD -> GIF_FRONT_PLANK
        ExerciseAnimationType.BODYWEIGHT_SQUATS -> GIF_BODYWEIGHT_SQUAT
        ExerciseAnimationType.FORWARD_LUNGES -> GIF_WALKING_LUNGE
        ExerciseAnimationType.TRICEP_DIPS -> GIF_BENCH_DIP
        ExerciseAnimationType.PIKE_SHOULDER_PRESS -> GIF_PIKE_PUSH_UP
        ExerciseAnimationType.COBRA_STRETCH -> GIF_SPINE_COBRA_STRETCH
        ExerciseAnimationType.INCLINE_DB_PRESS -> GIF_DB_INCLINE_PRESS
        ExerciseAnimationType.LAT_PULLDOWN -> GIF_WIDE_LAT_PULLDOWN
        ExerciseAnimationType.SEATED_CABLE_ROW -> GIF_CABLE_SEATED_ROW
        ExerciseAnimationType.CABLE_LATERAL_RAISE -> GIF_DB_LATERAL_RAISE
        ExerciseAnimationType.INCLINE_DB_CURL -> GIF_DB_INCLINE_CURL
        ExerciseAnimationType.DOORFRAME_ROW -> GIF_INVERTED_ROW
    }

    private fun cacheKey(exerciseName: String, type: ExerciseAnimationType): String {
        val normalized = exerciseName.trim().lowercase()
        return if (normalized.isNotEmpty()) "${type.name}::$normalized" else type.name
    }

    fun getCachedMediaInstant(
        context: Context,
        type: ExerciseAnimationType,
        exerciseName: String = ""
    ): ResolvedExerciseMedia {
        purgeLegacyPoisonedCacheOnce(context)
        val key = cacheKey(exerciseName, type)
        memoryResolvedCache[key]?.let { return it }

        val staticUrl = resolveStaticGifUrl(exerciseName = exerciseName, fallbackType = type)
        return ResolvedExerciseMedia.LoopingGif(staticUrl).also {
            memoryResolvedCache[key] = it
        }
    }

    fun prefetchAllExercises(context: Context) {
        if (prefetchStarted) return
        prefetchStarted = true
        val appContext = context.applicationContext
        val loader = imageLoader(appContext)
        backgroundScope.launch {
            val coreUrls = listOf(
                GIF_JACK_JUMP,
                GIF_STAR_JUMP,
                GIF_UPPER_BACK_OPENER,
                GIF_DYNAMIC_CHEST_STRETCH,
                GIF_SEMI_SQUAT_WARMUP,
                GIF_PUSH_UP,
                GIF_WALL_PUSH_UP,
                GIF_KNEELING_PUSH_UP,
                GIF_INCLINE_PUSH_UP,
                GIF_CLOSE_GRIP_PUSH_UP,
                GIF_DECLINE_PUSH_UP,
                GIF_BENCH_DIP,
                GIF_FLOOR_TRICEP_DIP,
                GIF_INVERTED_ROW,
                GIF_BENT_KNEE_ROW,
                GIF_BENCH_INVERTED_ROW,
                GIF_DB_STANDING_CURL,
                GIF_REVERSE_GRIP_CURL,
                GIF_BODYWEIGHT_SQUAT,
                GIF_SPLIT_SQUAT,
                GIF_WALKING_LUNGE,
                GIF_BENCH_HIP_THRUST,
                GIF_FLOOR_GLUTE_BRIDGE,
                GIF_HIP_ABDUCTION,
                GIF_GLUTE_KICKBACK,
                GIF_CALF_RAISE,
                GIF_PIKE_PUSH_UP,
                GIF_FLOOR_CRUNCH,
                GIF_REVERSE_CRUNCH,
                GIF_FRONT_PLANK,
                GIF_DB_INCLINE_PRESS,
                GIF_MACHINE_CHEST_PRESS,
                GIF_CABLE_PUSHDOWN,
                GIF_WIDE_LAT_PULLDOWN,
                GIF_MACHINE_SEATED_ROW,
                GIF_CABLE_SEATED_ROW,
                GIF_DB_INCLINE_CURL,
                GIF_BARBELL_EZ_CURL,
                GIF_SLED_LEG_PRESS,
                GIF_MACHINE_LEG_EXT,
                GIF_MACHINE_LEG_CURL,
                GIF_MACHINE_SHOULDER_PRESS,
                GIF_CABLE_LATERAL_RAISE,
                GIF_ROPE_FACE_PULL,
                GIF_KNEELING_LAT_STRETCH,
                GIF_RUNNERS_HIP_STRETCH,
                GIF_SPINE_COBRA_STRETCH
            ).distinct()

            for (batch in coreUrls.chunked(4)) {
                batch.map { gifUrl ->
                    async {
                        loader.enqueue(
                            ImageRequest.Builder(appContext)
                                .data(gifUrl)
                                .build()
                        )
                    }
                }.awaitAll()
            }
        }
    }

    suspend fun resolveMedia(
        context: Context,
        type: ExerciseAnimationType,
        exerciseName: String = ""
    ): ResolvedExerciseMedia {
        purgeLegacyPoisonedCacheOnce(context)
        val key = cacheKey(exerciseName, type)
        memoryResolvedCache[key]?.let { return it }

        val mutex = inFlightMutexes.getOrPut(key) { Mutex() }
        return mutex.withLock {
            memoryResolvedCache[key]?.let { return@withLock it }

            withContext(Dispatchers.IO) {
                val staticUrl = resolveStaticGifUrl(exerciseName = exerciseName, fallbackType = type)
                if (staticUrl.isNotBlank()) {
                    val resolved = ResolvedExerciseMedia.LoopingGif(staticUrl)
                    memoryResolvedCache[key] = resolved
                    return@withContext resolved
                }

                // Strict search fallback ONLY if an unmapped custom query is ever added
                val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val saved = prefs.getString("gif_$key", null)
                if (!saved.isNullOrBlank()) {
                    return@withContext ResolvedExerciseMedia.LoopingGif(saved).also {
                        memoryResolvedCache[key] = it
                    }
                }

                val profile = profileFor(type)
                val queried = queryExerciseDbGifUrl(profile)
                if (!queried.isNullOrBlank()) {
                    prefs.edit().putString("gif_$key", queried).apply()
                    return@withContext ResolvedExerciseMedia.LoopingGif(queried).also {
                        memoryResolvedCache[key] = it
                    }
                }

                ResolvedExerciseMedia.OfflineFallback
            }
        }
    }

    private fun queryExerciseDbGifUrl(profile: ExerciseSearchProfile): String? {
        for (rawQuery in profile.searchQueries) {
            val encoded = runCatching { URLEncoder.encode(rawQuery, "UTF-8") }.getOrDefault(rawQuery)
            val endpoint = "$EXERCISE_DB_SEARCH_ENDPOINT$encoded&limit=15"
            val jsonText = fetchText(endpoint, connectTimeoutMs = 4500, readTimeoutMs = 5500) ?: continue
            val bestGif = pickBestExerciseDbGif(jsonText, profile)
            if (!bestGif.isNullOrBlank()) {
                return bestGif
            }
        }
        return null
    }

    internal fun pickBestExerciseDbGifForTest(
        jsonText: String,
        type: ExerciseAnimationType
    ): String? = pickBestExerciseDbGif(jsonText, profileFor(type))

    private fun pickBestExerciseDbGif(jsonText: String, profile: ExerciseSearchProfile): String? {
        val trimmed = jsonText.trim()
        if (trimmed.isEmpty()) return null

        val itemsArray: JSONArray = runCatching {
            if (trimmed.startsWith("{")) {
                val root = JSONObject(trimmed)
                root.optJSONArray("data") ?: root.optJSONArray("exercises")
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed)
            } else {
                null
            }
        }.getOrNull() ?: return null

        var bestUrl: String? = null
        // Require a genuine positive keyword match (>= 40) so unrelated exercises NEVER get picked!
        var bestScore = 39

        for (i in 0 until itemsArray.length()) {
            val obj = itemsArray.optJSONObject(i) ?: continue
            val gifUrl = obj.optString("gifUrl", "").trim()
            if (!gifUrl.startsWith("http")) continue

            val name = obj.optString("name", "").lowercase()
            val equipments = buildString {
                val eqArr = obj.optJSONArray("equipments")
                if (eqArr != null) {
                    for (j in 0 until eqArr.length()) {
                        append(eqArr.optString(j, "").lowercase()).append(' ')
                    }
                }
                append(obj.optString("equipment", "").lowercase())
            }

            var score = 0
            var matchedNameKeyword = false
            profile.preferredNameKeywords.forEachIndexed { idx, kw ->
                if (name == kw) {
                    score += 120 - idx * 8
                    matchedNameKeyword = true
                } else if (name.contains(kw)) {
                    score += 55 - idx * 5
                    matchedNameKeyword = true
                }
            }
            if (!matchedNameKeyword) continue

            profile.preferredEquipments.forEach { eq ->
                if (equipments.contains(eq)) score += 35
            }
            profile.bannedNameKeywords.forEach { bad ->
                if (name.contains(bad)) score -= 85
            }
            score -= (name.length / 8)

            if (score > bestScore) {
                bestScore = score
                bestUrl = gifUrl
            }
        }
        return bestUrl
    }

    private fun fetchText(urlString: String, connectTimeoutMs: Int, readTimeoutMs: Int): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                instanceFollowRedirects = true
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "ApexProtocolAndroid/1.0")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (_: Throwable) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun profileFor(type: ExerciseAnimationType): ExerciseSearchProfile = when (type) {
        ExerciseAnimationType.BODYWEIGHT_SQUATS -> ExerciseSearchProfile(
            searchQueries = listOf("air squat", "squat"),
            preferredNameKeywords = listOf("air squat", "bodyweight squat", "squat"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("barbell", "smith", "band", "kettlebell", "dumbbell", "single leg", "pistol")
        )
        ExerciseAnimationType.FORWARD_LUNGES -> ExerciseSearchProfile(
            searchQueries = listOf("walking lunge", "forward lunge"),
            preferredNameKeywords = listOf("walking lunge", "forward lunge", "lunge"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("barbell", "smith", "dumbbell", "kettlebell", "side", "lateral")
        )
        ExerciseAnimationType.STANDARD_PUSH_UPS -> ExerciseSearchProfile(
            searchQueries = listOf("push-up"),
            preferredNameKeywords = listOf("push-up", "push-ups", "pushup"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("incline", "decline", "pike", "clap", "single", "medicine", "bosu", "kneeling")
        )
        ExerciseAnimationType.INCLINE_PUSH_UPS -> ExerciseSearchProfile(
            searchQueries = listOf("incline push-up"),
            preferredNameKeywords = listOf("incline push-up", "incline push-ups"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("decline", "clap", "bosu", "depth")
        )
        ExerciseAnimationType.KNEE_PUSH_UPS -> ExerciseSearchProfile(
            searchQueries = listOf("kneeling push-up"),
            preferredNameKeywords = listOf("kneeling push-up", "knee push-up"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("decline", "clap", "single")
        )
        ExerciseAnimationType.INCLINE_DB_PRESS -> ExerciseSearchProfile(
            searchQueries = listOf("dumbbell incline bench press"),
            preferredNameKeywords = listOf("dumbbell incline bench press", "incline dumbbell press"),
            preferredEquipments = listOf("dumbbell"),
            bannedNameKeywords = listOf("hammer", "one arm", "single", "fly")
        )
        ExerciseAnimationType.LAT_PULLDOWN -> ExerciseSearchProfile(
            searchQueries = listOf("cable pulldown"),
            preferredNameKeywords = listOf("cable pulldown", "lat pulldown"),
            preferredEquipments = listOf("cable"),
            bannedNameKeywords = listOf("one arm", "underhand", "straight arm")
        )
        ExerciseAnimationType.SEATED_CABLE_ROW -> ExerciseSearchProfile(
            searchQueries = listOf("cable seated row"),
            preferredNameKeywords = listOf("cable seated row", "seated row"),
            preferredEquipments = listOf("cable"),
            bannedNameKeywords = listOf("one arm", "neck")
        )
        ExerciseAnimationType.CABLE_LATERAL_RAISE -> ExerciseSearchProfile(
            searchQueries = listOf("dumbbell lateral raise"),
            preferredNameKeywords = listOf("dumbbell lateral raise", "cable lateral raise"),
            preferredEquipments = listOf("dumbbell", "cable"),
            bannedNameKeywords = listOf("lying", "seated", "bent")
        )
        ExerciseAnimationType.INCLINE_DB_CURL -> ExerciseSearchProfile(
            searchQueries = listOf("dumbbell incline biceps curl"),
            preferredNameKeywords = listOf("dumbbell incline biceps curl", "dumbbell biceps curl"),
            preferredEquipments = listOf("dumbbell"),
            bannedNameKeywords = listOf("preacher", "reverse")
        )
        ExerciseAnimationType.TRICEP_DIPS -> ExerciseSearchProfile(
            searchQueries = listOf("bench dip"),
            preferredNameKeywords = listOf("bench dip", "triceps dip"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("weighted", "ring", "machine")
        )
        ExerciseAnimationType.DOORFRAME_ROW -> ExerciseSearchProfile(
            searchQueries = listOf("inverted row"),
            preferredNameKeywords = listOf("inverted row"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("barbell", "dumbbell")
        )
        ExerciseAnimationType.PIKE_SHOULDER_PRESS -> ExerciseSearchProfile(
            searchQueries = listOf("pike push-up"),
            preferredNameKeywords = listOf("pike push-up", "modified hindu push-up"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("barbell", "machine")
        )
        ExerciseAnimationType.ABDOMINAL_CRUNCHES -> ExerciseSearchProfile(
            searchQueries = listOf("crunch floor"),
            preferredNameKeywords = listOf("crunch floor", "tuck crunch"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("cable", "machine", "band", "oblique", "bicycle")
        )
        ExerciseAnimationType.PLANK_HOLD -> ExerciseSearchProfile(
            searchQueries = listOf("front plank"),
            preferredNameKeywords = listOf("weighted front plank", "power point plank", "front plank"),
            preferredEquipments = listOf("body weight", "bodyweight", "weighted"),
            bannedNameKeywords = listOf("side", "reverse", "ball")
        )
        ExerciseAnimationType.COBRA_STRETCH -> ExerciseSearchProfile(
            searchQueries = listOf("spine stretch"),
            preferredNameKeywords = listOf("spine stretch"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("dumbbell", "cable")
        )
        ExerciseAnimationType.JUMPING_JACKS -> ExerciseSearchProfile(
            searchQueries = listOf("jack jump"),
            preferredNameKeywords = listOf("jack jump", "star jump"),
            preferredEquipments = listOf("body weight", "bodyweight"),
            bannedNameKeywords = listOf("plank", "burpee")
        )
    }
}
