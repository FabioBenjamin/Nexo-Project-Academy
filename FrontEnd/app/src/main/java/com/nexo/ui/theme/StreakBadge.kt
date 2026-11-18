package com.nexo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StreakBadge(
    streakDays: Int,
    studiedToday: Boolean,
    modifier: Modifier = Modifier
) {
    val flameColor = if (studiedToday) Color(0xFFFF5722) else Color(0xFF9CA3AF)
    val backgroundColor = if (studiedToday) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (studiedToday) Color(0xFFFFAB91) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val textColor = if (studiedToday) Color(0xFFD84315) else MaterialTheme.colorScheme.onSurfaceVariant

    val label = if (streakDays == 1) "1 dia" else "$streakDays dias"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = if (studiedToday) "Ofensiva ativa hoje" else "Ofensiva pendente hoje",
            tint = flameColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}