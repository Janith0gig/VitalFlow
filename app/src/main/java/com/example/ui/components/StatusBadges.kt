package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.unit.sp
import com.example.model.BloodBag
import com.example.model.BloodStatus
import com.example.model.BloodType
import com.example.ui.theme.BloodRed
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.RareBloodAccent
import com.example.ui.theme.RareBloodContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.StatusExpired
import com.example.ui.theme.StatusExpiredContainer
import com.example.ui.theme.StatusExpiring
import com.example.ui.theme.StatusExpiringContainer
import com.example.ui.theme.StatusFresh
import com.example.ui.theme.StatusFreshContainer

@Composable
fun BloodTypeBadge(
    bloodType: BloodType,
    modifier: Modifier = Modifier,
    size: Int = 44
) {
    val bgColor = if (bloodType.isRare) BloodRed else SoftRose
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodType.label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.42).sp
        )
    }
}

@Composable
fun RareIndicator(
    modifier: Modifier = Modifier,
    label: String = "Rare"
) {
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.LabDarkBackground
    val containerColor = if (isDark) Color(0xFF381B3F) else RareBloodContainer
    val accentColor = if (isDark) Color(0xFFE1BEE7) else RareBloodAccent

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = "Rare Blood",
            tint = accentColor,
            modifier = Modifier.size(11.dp)
        )
        if (label.isNotBlank()) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = accentColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun ExpirationStatusBadge(
    bag: BloodBag,
    modifier: Modifier = Modifier
) {
    val days = bag.getDaysUntilExpiration()
    val status = bag.getEffectiveStatus()

    val (bg, textColor, text, icon) = when (status) {
        BloodStatus.EXPIRED -> {
            val daysAgo = kotlin.math.abs(days)
            Quad(
                StatusExpiredContainer,
                StatusExpired,
                if (daysAgo == 0L) "Expired today" else "Expired $daysAgo d ago",
                Icons.Default.Warning
            )
        }
        BloodStatus.EXPIRING_SOON -> {
            Quad(
                StatusExpiringContainer,
                StatusExpiring,
                if (days == 0L) "Expires today!" else if (days == 1L) "Expires in 1 day!" else "Expires in $days days",
                Icons.Default.Schedule
            )
        }
        BloodStatus.TRANSFUSED -> {
            Quad(
                Color(0xFFEDE7F6),
                Color(0xFF5E35B1),
                "Transfused",
                Icons.Default.Schedule
            )
        }
        BloodStatus.FRESH -> {
            Quad(
                StatusFreshContainer,
                StatusFresh,
                "$days days left",
                Icons.Default.Schedule
            )
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = " $text",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
