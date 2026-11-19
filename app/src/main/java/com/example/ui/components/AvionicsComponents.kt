package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AvionicsSurface
import com.example.ui.theme.AvionicsSurfaceElevated
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.NominalGreen
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.TelemetryDim
import com.example.ui.theme.TelemetryMuted
import com.example.ui.theme.TelemetryWhite

/**
 * Clean background modifier (no sci-fi grid lines).
 */
fun Modifier.tacticalGridBackground(
    gridColor: Color = Color.Transparent,
    stepDp: Dp = 24.dp
): Modifier = this

/**
 * Clean container modifier (no sci-fi crosshair corner ticks).
 */
fun Modifier.crosshairCornerMarkers(
    markerColor: Color = Color.Transparent,
    armLengthDp: Dp = 6.dp,
    strokeWidthDp: Dp = 1.5.dp
): Modifier = this

@Composable
fun TacticalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = HairlineBorder,
    cornerMarkerColor: Color = Color.Transparent,
    backgroundColor: Color = AvionicsSurface,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun TacticalSectionHeader(
    indexCode: String,
    title: String,
    rightTag: String? = null,
    accentColor: Color = AlertAmber,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TelemetryWhite,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (rightTag != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = rightTag,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TelemetryMetricBox(
    label: String,
    value: String,
    unit: String,
    subtext: String,
    accentColor: Color = AlertAmber,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(AvionicsSurfaceElevated, RoundedCornerShape(16.dp))
            .border(1.dp, HairlineBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TelemetryMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = TelemetryWhite,
                fontWeight = FontWeight.Bold
            )
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelLarge,
                    color = accentColor,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
        if (subtext.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = TelemetryMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TacticalActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    accentColor: Color = AlertAmber,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val bgColor = when {
        !enabled -> AvionicsSurface
        isPrimary -> accentColor
        else -> AvionicsSurfaceElevated
    }
    val contentColor = when {
        !enabled -> TelemetryDim
        isPrimary -> Color.White
        else -> TelemetryWhite
    }
    val borderColor = when {
        !enabled -> HairlineBorder
        isPrimary -> accentColor
        else -> HairlineBorder
    }

    Box(
        modifier = modifier
            .let { if (testTag != null) it.testTag(testTag) else it }
            .defaultMinSize(minHeight = 50.dp, minWidth = 48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color = NominalGreen,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(50))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
