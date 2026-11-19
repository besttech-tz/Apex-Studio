package com.example.domain

import kotlin.math.roundToInt

object UnitFormatter {
    private const val KG_TO_LB = 2.20462f

    fun formatWeightRounded(weightKg: Float, useMetric: Boolean): String {
        return if (useMetric) {
            "${weightKg.roundToInt()} kg"
        } else {
            "${(weightKg * KG_TO_LB).roundToInt()} lb"
        }
    }

    fun formatWeightOneDecimal(weightKg: Float, useMetric: Boolean): String {
        return if (useMetric) {
            "${"%.1f".format(weightKg)} kg"
        } else {
            "${"%.1f".format(weightKg * KG_TO_LB)} lb"
        }
    }

    fun formatHeight(heightCm: Float, useMetric: Boolean): String {
        return if (useMetric) {
            "${heightCm.roundToInt()} cm"
        } else {
            val totalInches = (heightCm / 2.54f).roundToInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "${feet}'${inches}\""
        }
    }
}
