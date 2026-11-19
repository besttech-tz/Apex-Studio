package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.TextSecondarySlate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val PickerInk = Color(0xFF0F172A)
private val PickerAccentBlue = Color(0xFF1D63ED)

/**
 * Interactive Material 3 Calendar Date Picker for choosing the exact Starting Day (Day 1).
 * Uses UTC start-of-day epoch conversion so local timezones never shift the selected calendar date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanStartDatePickerDialog(
    initialDateIso: String,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val today = remember { LocalDate.now() }
    val initialDate = remember(initialDateIso, today) {
        val parsed = runCatching { LocalDate.parse(initialDateIso) }.getOrNull() ?: today
        if (parsed.isBefore(today)) today else parsed
    }
    val todayUtcMillis = remember(today) {
        today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val initialUtcMillis = remember(initialDate) {
        initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val selectableDates = remember(todayUtcMillis, today) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayUtcMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= today.year && year <= today.year + 2
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialUtcMillis,
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: initialUtcMillis
                    val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    onDateSelected(if (picked.isBefore(today)) today else picked)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PickerInk,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(end = 8.dp, bottom = 6.dp)
                    .defaultMinSize(minHeight = 42.dp)
                    .testTag("btn_confirm_start_date_picker")
            ) {
                Text(
                    text = "Set Day 1",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .defaultMinSize(minHeight = 42.dp)
                    .testTag("btn_cancel_start_date_picker")
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondarySlate
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = DatePickerDefaults.colors(
            containerColor = PureWhiteCard
        )
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = true,
            title = {
                Text(
                    text = "CHOOSE STARTING DAY (DAY 1)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSecondarySlate,
                    letterSpacing = 0.6.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = PureWhiteCard,
                titleContentColor = TextSecondarySlate,
                headlineContentColor = PickerInk,
                weekdayContentColor = TextSecondarySlate,
                subheadContentColor = PickerInk,
                navigationContentColor = PickerInk,
                yearContentColor = PickerInk,
                disabledYearContentColor = Color(0xFFCBD5E1),
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = PickerInk,
                dayContentColor = PickerInk,
                disabledDayContentColor = Color(0xFFCBD5E1),
                selectedDayContentColor = Color.White,
                disabledSelectedDayContentColor = Color(0xFFCBD5E1),
                selectedDayContainerColor = PickerInk,
                disabledSelectedDayContainerColor = Color(0xFFF1F5F9),
                todayContentColor = PickerAccentBlue,
                todayDateBorderColor = PickerAccentBlue
            ),
            modifier = Modifier.testTag("calendar_start_date_picker")
        )
    }
}

private data class QuickClockChip(
    val label: String,
    val hour: Int,
    val minute: Int
)

private val QUICK_CLOCK_CHIPS = listOf(
    QuickClockChip("6:30 AM", 6, 30),
    QuickClockChip("8:00 AM", 8, 0),
    QuickClockChip("12:30 PM", 12, 30),
    QuickClockChip("5:30 PM", 17, 30),
    QuickClockChip("9:00 PM", 21, 0)
)

/**
 * Interactive Material 3 Clock + Keyboard Time Picker for choosing an exact daily reminder time
 * (e.g., 8:00 AM, 9:00 PM, 6:45 AM, or any hour:minute).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExactReminderTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onTimeConfirmed: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var pickerKey by remember { mutableStateOf(0) }
    var seedHour by remember { mutableStateOf(initialHour.coerceIn(0, 23)) }
    var seedMinute by remember { mutableStateOf(initialMinute.coerceIn(0, 59)) }
    var showKeyboardInput by rememberSaveable { mutableStateOf(false) }

    val timePickerState = androidx.compose.runtime.key(pickerKey) {
        rememberTimePickerState(
            initialHour = seedHour,
            initialMinute = seedMinute,
            is24Hour = false
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, CardBorderLight),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("dialog_exact_time_picker")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "EXACT REMINDER TIME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondarySlate,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PickerInk
                        )
                    }

                    IconButton(
                        onClick = { showKeyboardInput = !showKeyboardInput },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_toggle_time_input_mode")
                    ) {
                        Icon(
                            imageVector = if (showKeyboardInput) Icons.Default.Schedule else Icons.Default.Keyboard,
                            contentDescription = if (showKeyboardInput) "Switch to clock dial" else "Type exact time",
                            tint = PickerInk
                        )
                    }
                }

                // Quick 1-Tap Popular Hours (8:00 AM, 9:00 PM, etc.)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    QUICK_CLOCK_CHIPS.forEach { chip ->
                        val active = timePickerState.hour == chip.hour && timePickerState.minute == chip.minute
                        Surface(
                            onClick = {
                                seedHour = chip.hour
                                seedMinute = chip.minute
                                pickerKey += 1
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) PickerInk else SoftSurfaceTint,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 36.dp)
                                .testTag("quick_clock_${chip.hour}_${chip.minute}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = chip.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.White else PickerInk,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                val pickerColors = TimePickerDefaults.colors(
                    clockDialColor = SoftSurfaceTint,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = PickerInk,
                    selectorColor = PickerInk,
                    containerColor = PureWhiteCard,
                    periodSelectorBorderColor = CardBorderLight,
                    periodSelectorSelectedContainerColor = PickerInk,
                    periodSelectorUnselectedContainerColor = SoftSurfaceTint,
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorUnselectedContentColor = PickerInk,
                    timeSelectorSelectedContainerColor = PickerInk,
                    timeSelectorUnselectedContainerColor = SoftSurfaceTint,
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = PickerInk
                )

                if (showKeyboardInput) {
                    TimeInput(
                        state = timePickerState,
                        colors = pickerColors
                    )
                } else {
                    TimePicker(
                        state = timePickerState,
                        colors = pickerColors
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .testTag("btn_cancel_exact_time")
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondarySlate
                        )
                    }
                    Button(
                        onClick = {
                            onTimeConfirmed(timePickerState.hour, timePickerState.minute)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PickerInk,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .defaultMinSize(minHeight = 44.dp)
                            .testTag("btn_confirm_exact_time")
                    ) {
                        Text(
                            text = "Save Time",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
