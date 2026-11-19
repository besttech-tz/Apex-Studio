package com.example.domain

import kotlin.math.roundToInt

enum class SupplementPreference(
    val id: String,
    val label: String,
    val shortNote: String
) {
    WITH_SUPPLEMENTS(
        id = "WITH_SUPPLEMENTS",
        label = "Food + Whey / Creatine",
        shortNote = "Easiest path to hit 180g–210g protein without overeating calories"
    ),
    WHOLE_FOODS_ONLY(
        id = "WHOLE_FOODS_ONLY",
        label = "100% Whole Foods",
        shortNote = "Zero powders — pure eggs, meat, fish, dairy & legumes"
    );

    companion object {
        fun fromId(raw: String?): SupplementPreference =
            entries.firstOrNull { it.id == raw } ?: WITH_SUPPLEMENTS
    }
}

enum class ProteinFoodCategory(val label: String) {
    ALL("All Foods"),
    MEAT_POULTRY("Meat & Poultry"),
    EGGS_DAIRY("Eggs & Dairy"),
    FISH_SEAFOOD("Fish"),
    SUPPLEMENTS("Supplements"),
    PLANT_BUDGET("Beans & Plant")
}

data class ProteinFoodItem(
    val id: String,
    val name: String,
    val portionDescription: String,
    val proteinGrams: Int,
    val caloriesKcal: Int,
    val category: ProteinFoodCategory,
    val qualityBadge: String
)

data class ProposedMealSlot(
    val slotKey: String,
    val timeWindow: String,
    val title: String,
    val menuDescription: String,
    val proteinGrams: Int,
    val approxCalories: Int
)

data class JunkSabotageItem(
    val id: String,
    val name: String,
    val portionLabel: String,
    val emptyCalories: Int,
    val sugarGrams: Int,
    val fatLossDaysWiped: Float,
    val extraStepsToBurn: Int,
    val nonStopCardioMinutes: Int,
    val brutalTruth: String,
    val instantHighProteinSwap: String,
    val swapProteinGrams: Int
)

object FuelAndRestraintCatalog {

    val PROTEIN_FOOD_TABLE: List<ProteinFoodItem> = listOf(
        ProteinFoodItem(
            id = "chicken_200",
            name = "Chicken Breast (Cooked)",
            portionDescription = "200g weighed cooked (2 palm fillets)",
            proteinGrams = 62,
            caloriesKcal = 330,
            category = ProteinFoodCategory.MEAT_POULTRY,
            qualityBadge = "ELITE LEAN"
        ),
        ProteinFoodItem(
            id = "chicken_100",
            name = "Chicken Breast (Single)",
            portionDescription = "100g weighed cooked (1 palm fillet)",
            proteinGrams = 31,
            caloriesKcal = 165,
            category = ProteinFoodCategory.MEAT_POULTRY,
            qualityBadge = "LEAN STAPLE"
        ),
        ProteinFoodItem(
            id = "whey_double",
            name = "Whey Protein Isolate (2 Scoops)",
            portionDescription = "64g powder in cold water",
            proteinGrams = 50,
            caloriesKcal = 230,
            category = ProteinFoodCategory.SUPPLEMENTS,
            qualityBadge = "200G SHORTCUT"
        ),
        ProteinFoodItem(
            id = "whey_single",
            name = "Whey Protein Isolate (1 Scoop)",
            portionDescription = "32g powder in water or milk",
            proteinGrams = 25,
            caloriesKcal = 115,
            category = ProteinFoodCategory.SUPPLEMENTS,
            qualityBadge = "FAST ABSORB"
        ),
        ProteinFoodItem(
            id = "lean_beef_200",
            name = "Lean Beef / Steak (90/10)",
            portionDescription = "200g cooked lean mince or sirloin",
            proteinGrams = 52,
            caloriesKcal = 352,
            category = ProteinFoodCategory.MEAT_POULTRY,
            qualityBadge = "IRON + ZINC"
        ),
        ProteinFoodItem(
            id = "white_fish_200",
            name = "White Fish / Tilapia / Cod",
            portionDescription = "200g baked or grilled fillet",
            proteinGrams = 42,
            caloriesKcal = 195,
            category = ProteinFoodCategory.FISH_SEAFOOD,
            qualityBadge = "ULTRA LOW CAL"
        ),
        ProteinFoodItem(
            id = "tuna_can",
            name = "Canned Tuna (In Water)",
            portionDescription = "1 drained can (120g meat)",
            proteinGrams = 28,
            caloriesKcal = 130,
            category = ProteinFoodCategory.FISH_SEAFOOD,
            qualityBadge = "BUDGET KING"
        ),
        ProteinFoodItem(
            id = "salmon_150",
            name = "Salmon / Sardines",
            portionDescription = "150g cooked fillet or 1.5 tins",
            proteinGrams = 31,
            caloriesKcal = 310,
            category = ProteinFoodCategory.FISH_SEAFOOD,
            qualityBadge = "OMEGA-3"
        ),
        ProteinFoodItem(
            id = "eggs_4",
            name = "Whole Large Eggs (4x)",
            portionDescription = "4 boiled, poached, or dry-scrambled eggs",
            proteinGrams = 25,
            caloriesKcal = 285,
            category = ProteinFoodCategory.EGGS_DAIRY,
            qualityBadge = "MORNING ANCHOR"
        ),
        ProteinFoodItem(
            id = "egg_whites_200",
            name = "Liquid Egg Whites",
            portionDescription = "200ml (~6 egg whites)",
            proteinGrams = 22,
            caloriesKcal = 104,
            category = ProteinFoodCategory.EGGS_DAIRY,
            qualityBadge = "PURE ALBUMIN"
        ),
        ProteinFoodItem(
            id = "greek_yogurt_250",
            name = "0% Greek Yogurt / Skyr",
            portionDescription = "250g bowl (unsweetened)",
            proteinGrams = 25,
            caloriesKcal = 145,
            category = ProteinFoodCategory.EGGS_DAIRY,
            qualityBadge = "CRAVING KILLER"
        ),
        ProteinFoodItem(
            id = "cottage_cheese_200",
            name = "Low-Fat Cottage Cheese",
            portionDescription = "200g cup (slow-digesting casein)",
            proteinGrams = 24,
            caloriesKcal = 160,
            category = ProteinFoodCategory.EGGS_DAIRY,
            qualityBadge = "NIGHT RECOVERY"
        ),
        ProteinFoodItem(
            id = "turkey_breast_150",
            name = "Turkey Breast",
            portionDescription = "150g roasted breast slices",
            proteinGrams = 44,
            caloriesKcal = 210,
            category = ProteinFoodCategory.MEAT_POULTRY,
            qualityBadge = "ZERO FAT"
        ),
        ProteinFoodItem(
            id = "tofu_tempeh_200",
            name = "Firm Tofu / Tempeh",
            portionDescription = "200g pan-seared block",
            proteinGrams = 32,
            caloriesKcal = 290,
            category = ProteinFoodCategory.PLANT_BUDGET,
            qualityBadge = "COMPLETE SOY"
        ),
        ProteinFoodItem(
            id = "lentils_beans_250",
            name = "Cooked Lentils / Black Beans",
            portionDescription = "250g cooked bowl (high fiber)",
            proteinGrams = 22,
            caloriesKcal = 285,
            category = ProteinFoodCategory.PLANT_BUDGET,
            qualityBadge = "BUDGET FIBER"
        )
    )

    val JUNK_SABOTAGE_ITEMS: List<JunkSabotageItem> = listOf(
        JunkSabotageItem(
            id = "candy_chocolate",
            name = "Candy Bar / Chocolate Sweets",
            portionLabel = "2 bars or candy pack (~100g)",
            emptyCalories = 510,
            sugarGrams = 56,
            fatLossDaysWiped = 1.15f,
            extraStepsToBurn = 11200,
            nonStopCardioMinutes = 54,
            brutalTruth = "Spikes blood sugar in 12 minutes, crashes your energy, and feeds belly fat while giving your muscles 0g usable protein.",
            instantHighProteinSwap = "1 Scoop Chocolate Whey blended with ice OR 250g cold Greek Yogurt + 1 tsp cocoa & berries.",
            swapProteinGrams = 25
        ),
        JunkSabotageItem(
            id = "sugary_soda",
            name = "Soda / Sweetened Juice / Energy Drink",
            portionLabel = "500ml–600ml bottle",
            emptyCalories = 260,
            sugarGrams = 62,
            fatLossDaysWiped = 0.6f,
            extraStepsToBurn = 5800,
            nonStopCardioMinutes = 28,
            brutalTruth = "Liquid sugar does not register fullness in your brain—you drink 62g pure sugar and stay just as hungry 15 minutes later.",
            instantHighProteinSwap = "500ml ice-cold sparkling water + squeeze of lime/lemon or zero-calorie electrolyte water.",
            swapProteinGrams = 0
        ),
        JunkSabotageItem(
            id = "chips_biscuits",
            name = "Chips, Biscuits, Cake or Pastries",
            portionLabel = "1 medium bag / 4–5 sweet biscuits",
            emptyCalories = 580,
            sugarGrams = 34,
            fatLossDaysWiped = 1.3f,
            extraStepsToBurn = 12600,
            nonStopCardioMinutes = 62,
            brutalTruth = "Refined flour + seed oils are engineered so your brain never gets a 'stop eating' signal—erasing 1.3 days of strict discipline in 5 minutes.",
            instantHighProteinSwap = "3 boiled eggs with salt & pepper OR 1 apple + 200g Cottage Cheese.",
            swapProteinGrams = 22
        ),
        JunkSabotageItem(
            id = "fried_fast_food",
            name = "Fast-Food Burger + Deep-Fried Fries",
            portionLabel = "1 takeout combo meal",
            emptyCalories = 1140,
            sugarGrams = 48,
            fatLossDaysWiped = 2.5f,
            extraStepsToBurn = 24500,
            nonStopCardioMinutes = 118,
            brutalTruth = "One 10-minute fast-food combo destroys TWO AND A HALF DAYS of your calorie deficit and leaves you sluggish for tomorrow's lift.",
            instantHighProteinSwap = "200g grilled chicken or lean beef patty + air-fried/boiled potatoes + zero-sugar drink.",
            swapProteinGrams = 55
        )
    )

    /**
     * Builds an exact 4-anchor daily meal plan whose protein portions add up to the user's
     * calibrated dailyProteinFloorGrams (e.g., 160g, 185g, 200g+) and match their weight direction.
     */
    fun buildDailyMealProposition(
        targetProteinGrams: Int,
        targetCaloriesKcal: Int,
        direction: MetabolicDirection,
        supplementPref: SupplementPreference
    ): List<ProposedMealSlot> {
        val safeProtein = targetProteinGrams.coerceIn(110, 260)

        // Distribute protein cleanly across 4 daily anchors so every meal builds muscle synthesis:
        // Breakfast 22%, Lunch 30%, Post-Workout/Snack 23%, Dinner 25%
        val p1 = (safeProtein * 0.22f).roundToInt()
        val p2 = (safeProtein * 0.30f).roundToInt()
        val p3 = (safeProtein * 0.23f).roundToInt()
        val p4 = (safeProtein - p1 - p2 - p3).coerceAtLeast(25)

        val k1 = (targetCaloriesKcal * 0.24f).roundToInt()
        val k2 = (targetCaloriesKcal * 0.31f).roundToInt()
        val k3 = (targetCaloriesKcal * 0.18f).roundToInt()
        val k4 = (targetCaloriesKcal - k1 - k2 - k3).coerceAtLeast(350)

        val carbSideLunch = when (direction) {
            MetabolicDirection.GAIN_LEAN_MASS -> "280g cooked jasmine rice + 1 tbsp olive oil"
            MetabolicDirection.BODY_RECOMP -> "180g cooked rice or potatoes + green veg"
            MetabolicDirection.CUT_FAT -> "220g boiled/roasted potatoes (high fullness) + broccoli"
        }

        val carbSideDinner = when (direction) {
            MetabolicDirection.GAIN_LEAN_MASS -> "250g rice/pasta + avocado + steamed greens"
            MetabolicDirection.BODY_RECOMP -> "150g sweet potato + mixed salad"
            MetabolicDirection.CUT_FAT -> "Large roasted vegetable bowl + 120g potatoes"
        }

        val chickenGramsLunch = ((p2 / 31.0f) * 100f).roundToInt().coerceIn(120, 280)
        val beefOrFishGramsDinner = ((p4 / 28.0f) * 100f).roundToInt().coerceIn(130, 260)

        val breakfastDesc = if (direction == MetabolicDirection.GAIN_LEAN_MASS) {
            "4 whole eggs + 150ml egg whites + 80g oats & banana"
        } else {
            "3 whole eggs + 200ml liquid egg whites (or 250g 0% Greek Yogurt + 2 eggs)"
        }

        val postWorkoutDesc = if (supplementPref == SupplementPreference.WITH_SUPPLEMENTS) {
            val scoops = if (p3 >= 44) "2 scoops Whey Isolate" else "1.5 scoops Whey Isolate"
            val fruitAdd = if (direction == MetabolicDirection.GAIN_LEAN_MASS) " + 1 banana & 5g Creatine" else " in ice water + 5g Creatine"
            "$scoops$fruitAdd"
        } else {
            val tunaCans = if (p3 >= 42) "1.5 cans drained Tuna" else "1 can drained Tuna"
            "$tunaCans + 150g low-fat Cottage Cheese or Greek Yogurt"
        }

        return listOf(
            ProposedMealSlot(
                slotKey = "breakfast",
                timeWindow = "08:00 AM • ANCHOR 1",
                title = "High-Protein Morning Lock",
                menuDescription = breakfastDesc,
                proteinGrams = p1,
                approxCalories = k1
            ),
            ProposedMealSlot(
                slotKey = "lunch",
                timeWindow = "01:00 PM • ANCHOR 2",
                title = "Midday Lean Mass Plate",
                menuDescription = "${chickenGramsLunch}g cooked Chicken Breast + $carbSideLunch",
                proteinGrams = p2,
                approxCalories = k2
            ),
            ProposedMealSlot(
                slotKey = "snack",
                timeWindow = "05:00 PM • ANCHOR 3",
                title = if (supplementPref == SupplementPreference.WITH_SUPPLEMENTS) {
                    "Post-Lift Shake & Creatine"
                } else {
                    "Afternoon Whole-Food Bridge"
                },
                menuDescription = postWorkoutDesc,
                proteinGrams = p3,
                approxCalories = k3
            ),
            ProposedMealSlot(
                slotKey = "dinner",
                timeWindow = "07:45 PM • ANCHOR 4",
                title = "Evening Recovery & Kitchen Lock",
                menuDescription = "${beefOrFishGramsDinner}g Lean Beef or White Fish + $carbSideDinner",
                proteinGrams = p4,
                approxCalories = k4
            )
        )
    }

    /**
     * Generates a direct, honest, motivating message tailored to the user's current numbers.
     */
    fun buildLiveCoachMotivation(
        direction: MetabolicDirection,
        consumedProtein: Int,
        targetProtein: Int,
        cleanRulesLockedCount: Int,
        cravingsDefeated: Int
    ): Pair<String, String> {
        val remainingProtein = (targetProtein - consumedProtein).coerceAtLeast(0)
        return when {
            consumedProtein >= targetProtein && cleanRulesLockedCount >= 3 -> {
                "ELITE EXECUTION LOCKED TODAY" to
                    "You hit ${consumedProtein}g/${targetProtein}g protein and kept junk out of your bloodstream. Sleep 8 hours tonight—your body literally remodels while you sleep."
            }
            cravingsDefeated > 0 && remainingProtein > 0 -> {
                "CRAVING DEFEATED ($cravingsDefeated TODAY) — FINISH YOUR PROTEIN" to
                    "Saying NO to junk saved your calorie budget. Now close the remaining ${remainingProtein}g protein gap with real food or whey so cravings cannot come back tonight."
            }
            consumedProtein < targetProtein / 2 -> {
                when (direction) {
                    MetabolicDirection.GAIN_LEAN_MASS ->
                        "MUSCLE CANNOT GROW ON AIR (${remainingProtein}g PROTEIN LEFT)" to
                            "You chose to build mass and widen your frame. If you undereat protein today, lifting only breaks tissue down without rebuilding it. Eat your next anchor meal now."
                    MetabolicDirection.CUT_FAT ->
                        "HUNGER GUARD ALERT (${remainingProtein}g PROTEIN LEFT)" to
                            "90% of night candy & junk binges happen because you undereat protein during the day. Hit ${targetProtein / 2}g+ before late afternoon to kill sugar cravings."
                    MetabolicDirection.BODY_RECOMP ->
                        "RECOMP TRUTH (${remainingProtein}g PROTEIN LEFT)" to
                            "To shrink a soft stomach while widening narrow shoulders, protein is non-negotiable. Open the Food Table below and log your next real meal."
                }
            }
            else -> {
                "PAST HALFWAY — ${remainingProtein}g LEFT TO LOCK TODAY" to
                    "Strong momentum (${consumedProtein}g logged). One more high-protein meal or shake locks your ${targetProtein}g floor—do not ruin a clean day with 5 minutes of candy or soda tonight."
            }
        }
    }
}
