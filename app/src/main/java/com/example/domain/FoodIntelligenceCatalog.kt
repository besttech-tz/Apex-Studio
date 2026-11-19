package com.example.domain

import kotlin.math.roundToInt

enum class FoodGuideGroup(val id: String, val label: String) {
    ALL("all", "All"),
    PROTEINS("proteins", "Proteins"),
    HEALTHY_FATS("healthy_fats", "Healthy Fats"),
    SMART_CARBS("smart_carbs", "Carbs"),
    FIBER_VEGGIES("fiber_veggies", "Greens"),
    SUPPLEMENTS("supplements", "Supplements"),
    CAUTION_TRAPS("caution_traps", "Avoid / Traps")
}

/**
 * Photo Grid Mapping:
 * - photoGridIndex 0 = Proteins Grid (0=Chicken, 1=Eggs, 2=Salmon & Steak, 3=Greek Yogurt & Whey)
 * - photoGridIndex 1 = Fats, Carbs & Greens Grid (0=Avocado/Olive Oil/Nuts, 1=Rice & Potatoes, 2=Oats & Banana, 3=Broccoli & Greens)
 * - photoGridIndex 2 = Meal Combos Grid (0=Chicken+Rice+Broccoli, 1=White Fish+Potatoes+Greens, 2=Smoothie+Oats+PB, 3=Burger+Fries+Candy Trap)
 */
data class FoodEncyclopediaEntry(
    val id: String,
    val name: String,
    val servingLabel: String,
    val group: FoodGuideGroup,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val caloriesKcal: Int,
    val roleTag: String,
    val biologicalBenefit: String,
    val howToEatAndPrepare: String,
    val bestFoodCombination: String,
    val photoGridIndex: Int = 0,
    val photoQuadrant: Int = 0
)

data class HighFatScenarioSpec(
    val id: String,
    val title: String,
    val exampleFoods: String,
    val extraFatGrams: Int,
    val extraCaloriesKcal: Int,
    val immediateActionStep: String,
    val nextMealPrescription: String,
    val photoGridIndex: Int = 0,
    val photoQuadrant: Int = 2
) {
    val carbGramsToTrim: Int
        get() = ((extraFatGrams * 9f) / 4f).roundToInt()
}

data class FoodCombinationBlueprint(
    val id: String,
    val timingBadge: String,
    val title: String,
    val formula: String,
    val examplePlate: String,
    val whyItWorksBiologically: String,
    val isWarningCombo: Boolean = false,
    val photoGridIndex: Int = 2,
    val photoQuadrant: Int = 0
)

data class IntakeManagementRule(
    val id: String,
    val badge: String,
    val title: String,
    val coreRule: String,
    val practicalHowTo: String
)

object FoodIntelligenceCatalog {

    val FOOD_ENCYCLOPEDIA: List<FoodEncyclopediaEntry> = listOf(
        // =====================================================================
        // 1. PROTEINS
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_chicken_breast",
            name = "Grilled Chicken Breast",
            servingLabel = "200g raw (150g cooked)",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 46,
            carbsGrams = 0,
            fatGrams = 4,
            caloriesKcal = 230,
            roleTag = "LEAN MUSCLE PROTEIN",
            biologicalBenefit = "Delivers 46g of pure muscle-building protein for just 230 calories, keeping fat low so you stay full and lean.",
            howToEatAndPrepare = "Slice thin, season well with salt, garlic, paprika & lemon, and pan-sear with 1 tsp olive oil or air-fry so it stays juicy.",
            bestFoodCombination = "Jasmine Rice + Steamed Broccoli after workouts, or Boiled Potatoes + Salad for fat loss.",
            photoGridIndex = 0,
            photoQuadrant = 0
        ),
        FoodEncyclopediaEntry(
            id = "enc_whole_eggs",
            name = "Whole Eggs",
            servingLabel = "3 large eggs",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 19,
            carbsGrams = 1,
            fatGrams = 15,
            caloriesKcal = 215,
            roleTag = "PROTEIN + HEALTHY YOLK FAT",
            biologicalBenefit = "Complete amino acids plus yolk Vitamin D, choline, and healthy fats that support natural hormone production.",
            howToEatAndPrepare = "Boil, poach, or scramble on non-stick heat. Want 35g+ protein without doubling fat? Mix 3 whole eggs + 4 egg whites.",
            bestFoodCombination = "3 Whole Eggs + Egg Whites + Toast + Spinach & Tomatoes.",
            photoGridIndex = 0,
            photoQuadrant = 1
        ),
        FoodEncyclopediaEntry(
            id = "enc_lean_beef_steak",
            name = "Lean Steak / 93% Beef",
            servingLabel = "200g raw",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 44,
            carbsGrams = 0,
            fatGrams = 12,
            caloriesKcal = 295,
            roleTag = "IRON, ZINC & STRENGTH",
            biologicalBenefit = "Naturally rich in creatine, iron, zinc, and B12 for lifting strength, red blood cells, and recovery.",
            howToEatAndPrepare = "Grill or pan-sear without heavy butter. Drain extra pan grease when cutting body fat.",
            bestFoodCombination = "Baked Potatoes + Bell Peppers (Vitamin C boosts iron absorption).",
            photoGridIndex = 0,
            photoQuadrant = 2
        ),
        FoodEncyclopediaEntry(
            id = "enc_salmon_sardines",
            name = "Salmon or Sardines",
            servingLabel = "180g fillet",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 38,
            carbsGrams = 0,
            fatGrams = 18,
            caloriesKcal = 320,
            roleTag = "PROTEIN + OMEGA-3",
            biologicalBenefit = "High in Omega-3 fats that protect joints, calm soreness, and help insulin push nutrients into muscle.",
            howToEatAndPrepare = "Bake with lemon and herbs for 12 mins. Since salmon already has 18g healthy fat, cook it with zero extra oil.",
            bestFoodCombination = "Salmon + Boiled Baby Potatoes + Asparagus.",
            photoGridIndex = 0,
            photoQuadrant = 2
        ),
        FoodEncyclopediaEntry(
            id = "enc_white_fish_tuna",
            name = "White Fish or Tuna in Water",
            servingLabel = "200g fillet / 1 drained can",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 40,
            carbsGrams = 0,
            fatGrams = 2,
            caloriesKcal = 185,
            roleTag = "ZERO-FAT RESCUE PROTEIN",
            biologicalBenefit = "Almost pure protein (185 kcal)—the best dinner choice on days when you already ate a fatty lunch.",
            howToEatAndPrepare = "Mix tuna with plain Greek yogurt, lemon, and mustard instead of high-calorie mayonnaise.",
            bestFoodCombination = "White Fish or Tuna + Boiled Potatoes + Crisp Greens.",
            photoGridIndex = 2,
            photoQuadrant = 1
        ),
        FoodEncyclopediaEntry(
            id = "enc_greek_yogurt_cottage",
            name = "Plain Greek Yogurt / Cottage Cheese",
            servingLabel = "250g bowl",
            group = FoodGuideGroup.PROTEINS,
            proteinGrams = 26,
            carbsGrams = 9,
            fatGrams = 3,
            caloriesKcal = 165,
            roleTag = "NIGHT PROTEIN + GUT HEALTH",
            biologicalBenefit = "Slow-digesting protein repairs muscles overnight while probiotics keep digestion smooth.",
            howToEatAndPrepare = "Buy plain 0%–2% yogurt and add fresh berries + cinnamon instead of sugary flavored cups.",
            bestFoodCombination = "Greek Yogurt + Berries + 10g Walnuts (kills 9 PM sweet cravings).",
            photoGridIndex = 0,
            photoQuadrant = 3
        ),

        // =====================================================================
        // 2. HEALTHY DIETARY FATS
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_avocado",
            name = "Fresh Avocado",
            servingLabel = "1/2 medium avocado (100g)",
            group = FoodGuideGroup.HEALTHY_FATS,
            proteinGrams = 2,
            carbsGrams = 8,
            fatGrams = 15,
            caloriesKcal = 160,
            roleTag = "FIBER + CLEAN FAT",
            biologicalBenefit = "Pairs heart-healthy fat with 7g of fiber and potassium, keeping hunger switched off for hours.",
            howToEatAndPrepare = "Use 1/2 avocado as your meal's main fat source (avoid stacking avocado + cheese + oil together).",
            bestFoodCombination = "1/2 Avocado + Eggs + Salsa.",
            photoGridIndex = 1,
            photoQuadrant = 0
        ),
        FoodEncyclopediaEntry(
            id = "enc_olive_oil",
            name = "Extra-Virgin Olive Oil",
            servingLabel = "1 measured tbsp (14g)",
            group = FoodGuideGroup.HEALTHY_FATS,
            proteinGrams = 0,
            carbsGrams = 0,
            fatGrams = 14,
            caloriesKcal = 120,
            roleTag = "HEART & VITAMIN ABSORPTION",
            biologicalBenefit = "Supports heart health, hormone balance, and helps your body absorb Vitamins A, D, E, and K from greens.",
            howToEatAndPrepare = "Always measure with a spoon (1 tsp = 40 kcal, 1 tbsp = 120 kcal). Never pour blindly from the bottle.",
            bestFoodCombination = "1 tsp Olive Oil + Lemon over Grilled Chicken Salad.",
            photoGridIndex = 1,
            photoQuadrant = 0
        ),
        FoodEncyclopediaEntry(
            id = "enc_almonds_walnuts",
            name = "Raw Almonds & Walnuts",
            servingLabel = "1 small handful (28g)",
            group = FoodGuideGroup.HEALTHY_FATS,
            proteinGrams = 6,
            carbsGrams = 5,
            fatGrams = 15,
            caloriesKcal = 170,
            roleTag = "MAGNESIUM & BRAIN FAT",
            biologicalBenefit = "Rich in magnesium and plant omega-3s for deep sleep, recovery, and steady afternoon energy.",
            howToEatAndPrepare = "Portion 28g (~22 almonds) into a small cup and close the bag so you don't accidentally eat 600 kcal.",
            bestFoodCombination = "28g Almonds + 1 Protein Shake or Greek Yogurt.",
            photoGridIndex = 1,
            photoQuadrant = 0
        ),
        FoodEncyclopediaEntry(
            id = "enc_peanut_butter",
            name = "100% Natural Peanut Butter",
            servingLabel = "1 level tbsp (16g)",
            group = FoodGuideGroup.HEALTHY_FATS,
            proteinGrams = 4,
            carbsGrams = 3,
            fatGrams = 8,
            caloriesKcal = 95,
            roleTag = "DENSE CLEAN ENERGY",
            biologicalBenefit = "Great healthy fat and calorie booster for thin lifters—remember it is primarily a fat source, not high protein.",
            howToEatAndPrepare = "Pick 100% peanuts + salt (no added sugar or palm oil) and level the spoon.",
            bestFoodCombination = "1 tbsp Peanut Butter stirred into Whey Oatmeal.",
            photoGridIndex = 2,
            photoQuadrant = 2
        ),

        // =====================================================================
        // 3. SMART CARBS
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_boiled_potatoes",
            name = "Boiled or Air-Fried Potatoes",
            servingLabel = "250g raw weight",
            group = FoodGuideGroup.SMART_CARBS,
            proteinGrams = 5,
            carbsGrams = 44,
            fatGrams = 0,
            caloriesKcal = 195,
            roleTag = "#1 FULLNESS CARB",
            biologicalBenefit = "Ranked the #1 most filling food per calorie. Potatoes don't make you fat—deep-frying them in oil does.",
            howToEatAndPrepare = "Boil, bake, or cube and air-fry with garlic & rosemary.",
            bestFoodCombination = "Boiled Potatoes + Lean Steak or Chicken + Green Beans.",
            photoGridIndex = 1,
            photoQuadrant = 1
        ),
        FoodEncyclopediaEntry(
            id = "enc_jasmine_rice",
            name = "White / Jasmine Rice",
            servingLabel = "180g cooked (~1 cup)",
            group = FoodGuideGroup.SMART_CARBS,
            proteinGrams = 4,
            carbsGrams = 45,
            fatGrams = 0,
            caloriesKcal = 205,
            roleTag = "FAST MUSCLE FUEL",
            biologicalBenefit = "Easy on digestion with zero bloat—rapidly refills muscle energy before or right after lifting.",
            howToEatAndPrepare = "Steam plain in water or broth without butter. Put your biggest portion after training.",
            bestFoodCombination = "Jasmine Rice + Grilled Chicken + Broccoli.",
            photoGridIndex = 1,
            photoQuadrant = 1
        ),
        FoodEncyclopediaEntry(
            id = "enc_rolled_oats",
            name = "Rolled Oats & Banana",
            servingLabel = "60g dry oats",
            group = FoodGuideGroup.SMART_CARBS,
            proteinGrams = 8,
            carbsGrams = 40,
            fatGrams = 4,
            caloriesKcal = 228,
            roleTag = "STEADY WORKOUT ENERGY",
            biologicalBenefit = "Slow-release fiber fuels 60-minute workouts without mid-session energy crashes.",
            howToEatAndPrepare = "Never eat plain carb-only oats—always mix in 1 scoop Whey or eat alongside eggs.",
            bestFoodCombination = "60g Oats + 1 Scoop Whey + Sliced Banana (60–90m pre-workout).",
            photoGridIndex = 1,
            photoQuadrant = 2
        ),
        FoodEncyclopediaEntry(
            id = "enc_berries_banana",
            name = "Fresh Berries & Fruit",
            servingLabel = "200g berries / 1 banana",
            group = FoodGuideGroup.SMART_CARBS,
            proteinGrams = 1,
            carbsGrams = 22,
            fatGrams = 0,
            caloriesKcal = 90,
            roleTag = "SWEET CRAVING FIX",
            biologicalBenefit = "Huge bowl of natural sweetness, antioxidants, and fiber for under 90 calories.",
            howToEatAndPrepare = "Eat whole fruit (not bottled fruit juice). Keep berries ready for evening sweet cravings.",
            bestFoodCombination = "200g Berries + Plain Greek Yogurt.",
            photoGridIndex = 0,
            photoQuadrant = 3
        ),

        // =====================================================================
        // 4. GREENS & GUT VOLUME
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_cruciferous_greens",
            name = "Broccoli, Spinach & Bell Peppers",
            servingLabel = "250g big plateful",
            group = FoodGuideGroup.FIBER_VEGGIES,
            proteinGrams = 6,
            carbsGrams = 12,
            fatGrams = 1,
            caloriesKcal = 75,
            roleTag = "ZERO-HUNGER PLATE VOLUME",
            biologicalBenefit = "Fills your stomach physically for only 75 kcal while delivering fiber, potassium, and Vitamin C.",
            howToEatAndPrepare = "Steam or roast with lemon, garlic, and salt—don't drown greens in creamy dressings.",
            bestFoodCombination = "Fill 40% of your dinner plate beside Chicken/Fish + Potatoes.",
            photoGridIndex = 1,
            photoQuadrant = 3
        ),

        // =====================================================================
        // 5. SUPPLEMENTS
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_whey_isolate",
            name = "Whey Protein Shake",
            servingLabel = "1 scoop (32g)",
            group = FoodGuideGroup.SUPPLEMENTS,
            proteinGrams = 25,
            carbsGrams = 2,
            fatGrams = 1,
            caloriesKcal = 120,
            roleTag = "FAST 25G PROTEIN TOOL",
            biologicalBenefit = "Simple, convenient dairy protein to hit high daily protein targets for only 120 calories.",
            howToEatAndPrepare = "Shake with cold water when losing fat, or blend with milk + oats + banana when gaining muscle.",
            bestFoodCombination = "1 Scoop Whey + 5g Creatine in water post-workout.",
            photoGridIndex = 0,
            photoQuadrant = 3
        ),
        FoodEncyclopediaEntry(
            id = "enc_creatine_mono",
            name = "Creatine Monohydrate",
            servingLabel = "5g daily (1 tsp)",
            group = FoodGuideGroup.SUPPLEMENTS,
            proteinGrams = 0,
            carbsGrams = 0,
            fatGrams = 0,
            caloriesKcal = 0,
            roleTag = "+1–2 REPS STRENGTH",
            biologicalBenefit = "Boosts lifting strength and pulls water inside muscle cells so muscles look fuller and harder.",
            howToEatAndPrepare = "Take 5g every single day with a full glass of water.",
            bestFoodCombination = "Take with your post-workout meal or shake.",
            photoGridIndex = 0,
            photoQuadrant = 3
        ),

        // =====================================================================
        // 6. AVOID / CALORIE TRAPS
        // =====================================================================
        FoodEncyclopediaEntry(
            id = "enc_blind_cooking_oil",
            name = "TRAP: Unmeasured Oil & Creamy Mayo",
            servingLabel = "3 tbsp free-poured oil / mayo",
            group = FoodGuideGroup.CAUTION_TRAPS,
            proteinGrams = 0,
            carbsGrams = 2,
            fatGrams = 42,
            caloriesKcal = 380,
            roleTag = "HIDDEN 380 KCAL",
            biologicalBenefit = "Adds zero fullness yet silently wipes out your entire daily fat-loss deficit in 5 seconds.",
            howToEatAndPrepare = "Measure oil with 1 teaspoon, or swap mayo for Greek yogurt + lemon + garlic.",
            bestFoodCombination = "Swap 2 tbsp Mayo (200 kcal) -> Greek Yogurt Garlic Sauce (25 kcal).",
            photoGridIndex = 2,
            photoQuadrant = 3
        ),
        FoodEncyclopediaEntry(
            id = "enc_sugar_fat_combo",
            name = "TRAP: Burger + Fries + Soda + Candy",
            servingLabel = "1 combo meal",
            group = FoodGuideGroup.CAUTION_TRAPS,
            proteinGrams = 18,
            carbsGrams = 98,
            fatGrams = 48,
            caloriesKcal = 910,
            roleTag = "FAT + SUGAR STORAGE BOMB",
            biologicalBenefit = "Liquid sugar spikes insulin right while 48g of deep-fried fat enters your blood—storing straight to the waist.",
            howToEatAndPrepare = "Eating out? Order a double meat burger (skip the fries, pick zero-sugar drink) and save 550 calories.",
            bestFoodCombination = "Swap Fries + Sweet Soda -> Extra Meat Patty + Water/Zero.",
            photoGridIndex = 2,
            photoQuadrant = 3
        )
    )

    val HIGH_FAT_MANAGEMENT_SCENARIOS: List<HighFatScenarioSpec> = listOf(
        HighFatScenarioSpec(
            id = "fat_steak_thighs",
            title = "Ate Fatty Meat (Ribeye, Lamb, Thighs)",
            exampleFoods = "~250g ribeye or skin-on chicken (+28g fat)",
            extraFatGrams = 28,
            extraCaloriesKcal = 252,
            immediateActionStep = "No stress—meat fat + protein keeps you full. Simply trim ~63g carbs (~1 cup cooked rice) today so total calories stay on target.",
            nextMealPrescription = "Next meal: Lean Chicken Breast, White Fish, or 0% Greek Yogurt + greens cooked without extra oil.",
            photoGridIndex = 0,
            photoQuadrant = 2
        ),
        HighFatScenarioSpec(
            id = "fat_oil_avocado",
            title = "Meal Had Extra Oil, Ghee, or Avocado",
            exampleFoods = "Oily stir-fry or whole avocado (+22g fat)",
            extraFatGrams = 22,
            extraCaloriesKcal = 198,
            immediateActionStep = "Trim ~50g carbs (halve your dinner rice/bread). Keep your full daily protein target.",
            nextMealPrescription = "Next meal: Air-fried or steamed chicken/fish + vegetables with lemon & spices.",
            photoGridIndex = 1,
            photoQuadrant = 0
        ),
        HighFatScenarioSpec(
            id = "fat_nuts_pb_cheese",
            title = "Ate Extra Nuts, Peanut Butter, or Cheese",
            exampleFoods = "2–3 big spoons PB or extra nuts (+26g fat)",
            extraFatGrams = 26,
            extraCaloriesKcal = 234,
            immediateActionStep = "Trim ~58g carbs from dinner and take a brisk 15-minute walk tonight.",
            nextMealPrescription = "Switch night snacks to 0% Greek Yogurt + Berries and keep nut jars closed.",
            photoGridIndex = 2,
            photoQuadrant = 2
        ),
        HighFatScenarioSpec(
            id = "fat_heavy_cheat",
            title = "Ate Fast Food, Fried Meal, or Pizza",
            exampleFoods = "Burger + fries or 3 slices pizza (+55g fat)",
            extraFatGrams = 55,
            extraCaloriesKcal = 495,
            immediateActionStep = "Never starve yourself tomorrow. Walk 20 minutes right after eating so leg muscles burn incoming glucose & fat.",
            nextMealPrescription = "Drink 750ml water (salt adds temporary water weight, not real fat) and make your next 2 meals lean protein + greens.",
            photoGridIndex = 2,
            photoQuadrant = 3
        )
    )

    val FOOD_COMBINATIONS: List<FoodCombinationBlueprint> = listOf(
        FoodCombinationBlueprint(
            id = "combo_post_workout",
            timingBadge = "POST-WORKOUT PLATE",
            title = "Muscle Recovery & Refill Combo",
            formula = "Lean Protein + Clean Rice + Green Veg",
            examplePlate = "200g Grilled Chicken + 180g Jasmine Rice + Steamed Broccoli (510 kcal • 52g Protein)",
            whyItWorksBiologically = "After lifting, your muscles act like a sponge—rice refills muscle energy fast while lean chicken repairs muscle fibers.",
            photoGridIndex = 2,
            photoQuadrant = 0
        ),
        FoodCombinationBlueprint(
            id = "combo_fat_loss_dinner",
            timingBadge = "FAT-LOSS DINNER",
            title = "High-Volume Zero-Hunger Plate",
            formula = "Lean Fish/Chicken + Boiled Potatoes + Greens",
            examplePlate = "200g White Fish + 220g Boiled Baby Potatoes + Asparagus (460 kcal • 46g Protein)",
            whyItWorksBiologically = "Over 600g of real food for just 460 calories—physically fills your stomach so late-night snack cravings disappear.",
            photoGridIndex = 2,
            photoQuadrant = 1
        ),
        FoodCombinationBlueprint(
            id = "combo_clean_mass",
            timingBadge = "SKINNY-TO-FIT BUILDER",
            title = "High-Calorie Clean Mass Shake",
            formula = "Whey + Oats + Banana + Natural Peanut Butter",
            examplePlate = "300ml Milk + 1 Scoop Whey + 70g Oats + 1 Banana + 1 tbsp PB (740 kcal • 46g Protein)",
            whyItWorksBiologically = "Lets thin users with small appetites drink 740 clean muscle-building calories in 3 minutes with zero junk food.",
            photoGridIndex = 2,
            photoQuadrant = 2
        ),
        FoodCombinationBlueprint(
            id = "combo_hormone_morning",
            timingBadge = "MORNING BREAKFAST",
            title = "All-Morning Focus & Satiety Plate",
            formula = "Whole Eggs + Egg Whites + Avocado/Greens",
            examplePlate = "3 Whole Eggs + 3 Egg Whites + Spinach & 1/2 Avocado (390 kcal • 33g Protein)",
            whyItWorksBiologically = "High protein + healthy egg & avocado fats keep blood sugar rock-steady until lunch with zero mid-morning crash.",
            photoGridIndex = 0,
            photoQuadrant = 1
        ),
        FoodCombinationBlueprint(
            id = "combo_avoid_bomb",
            timingBadge = "WORST COMBINATION TO AVOID",
            title = "Deep-Fried Fat + Liquid Sugar Together",
            formula = "Fried Oil + Refined Flour + Sweet Drink",
            examplePlate = "Burger + Fries + Soda + Candy (950+ kcal • High Fat Storage)",
            whyItWorksBiologically = "Sugar spikes insulin (locking fat cells into storage mode) at the exact moment 50g of fried oil hits your bloodstream.",
            isWarningCombo = true,
            photoGridIndex = 2,
            photoQuadrant = 3
        )
    )

    val INTAKE_MANAGEMENT_RULES: List<IntakeManagementRule> = listOf(
        IntakeManagementRule(
            id = "rule_fat_floor",
            badge = "RULE 1",
            title = "Never Cut Healthy Fat to Zero",
            coreRule = "Aim for ~0.8g of healthy fat per kg of bodyweight every day.",
            practicalHowTo = "Zero-fat diets crash hormones, mood, and joints. Eat measured healthy fats daily from whole eggs, olive oil, salmon, avocado, and raw nuts."
        ),
        IntakeManagementRule(
            id = "rule_carb_fat_seesaw",
            badge = "RULE 2",
            title = "The Seesaw Rule: Fat Up = Carbs Down",
            coreRule = "Keep Protein fixed every day; let Fat and Carbs trade places.",
            practicalHowTo = "Having salmon, steak, or whole eggs (higher fat)? Keep rice/bread smaller at that meal. Having chicken breast or tuna (low fat)? Enjoy a bigger bowl of rice or potatoes."
        ),
        IntakeManagementRule(
            id = "rule_eating_order",
            badge = "RULE 3",
            title = "Eat Greens & Protein First, Carbs Last",
            coreRule = "Eating order cuts blood-sugar spikes and hunger by ~30%.",
            practicalHowTo = "Start your plate with vegetables and meat/eggs before eating rice, bread, or fruit so you get full faster and stay energized."
        ),
        IntakeManagementRule(
            id = "rule_hand_portions",
            badge = "RULE 4",
            title = "No Food Scale? Use Your Hand",
            coreRule = "Portion any restaurant or home meal in 5 seconds:",
            practicalHowTo = "• 1 Palm of Meat/Fish = ~35g Protein\n• 1 Fist of Veggies = ~40 kcal Volume\n• 1 Cupped Hand of Rice/Potato = ~30g Carbs\n• 1 Thumb of Oil/Peanut Butter = ~12g Fat"
        )
    )
}
