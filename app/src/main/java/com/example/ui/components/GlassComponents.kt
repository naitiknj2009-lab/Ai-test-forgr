package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = DarkSurfaceCard.copy(alpha = 0.85f),
    borderColor: Color = DarkBorder.copy(alpha = 0.7f),
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        )
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .shadow(elevation, shape, clip = false, ambientColor = Color.Black.copy(alpha = 0.3f))
            .clip(shape)
            .background(backgroundColor)
            .border(
                border = BorderStroke(
                    borderWidth,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            borderColor.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                ),
                shape = shape
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
fun DifficultyBadge(
    difficulty: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (color, label, emoji) = when (difficulty.uppercase()) {
        "EASY" -> Triple(DifficultyEasy, "Easy", "🟢")
        "HARD" -> Triple(DifficultyHard, "Hard", "🔴")
        else -> Triple(DifficultyMedium, "Medium", "🟡")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 11.sp)
        if (showLabel) {
            Text(
                text = label,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    GlassCard(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryDark
            )

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondaryDark
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = BrandSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun QuestionOptionCard(
    optionText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isCorrectOption: Boolean? = null, // null during test, true/false during review
    showReviewStatus: Boolean = false
) {
    val targetBgColor = when {
        showReviewStatus && isCorrectOption == true -> DifficultyEasy.copy(alpha = 0.2f)
        showReviewStatus && isSelected && isCorrectOption == false -> DifficultyHard.copy(alpha = 0.2f)
        isSelected -> BrandPrimary.copy(alpha = 0.18f)
        else -> DarkSurfaceElevated.copy(alpha = 0.6f)
    }

    val targetBorderColor = when {
        showReviewStatus && isCorrectOption == true -> DifficultyEasy
        showReviewStatus && isSelected && isCorrectOption == false -> DifficultyHard
        isSelected -> BrandPrimary
        else -> DarkBorder.copy(alpha = 0.6f)
    }

    val animatedBg by animateColorAsState(targetBgColor, tween(200), label = "bg")
    val animatedBorder by animateColorAsState(targetBorderColor, tween(200), label = "border")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .border(1.5.dp, animatedBorder, RoundedCornerShape(14.dp)),
        color = animatedBg,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val iconTint = when {
                showReviewStatus && isCorrectOption == true -> DifficultyEasy
                showReviewStatus && isSelected && isCorrectOption == false -> DifficultyHard
                isSelected -> BrandPrimary
                else -> TextSecondaryDark
            }

            Icon(
                imageVector = if (isSelected || (showReviewStatus && isCorrectOption == true)) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = optionText,
                color = if (isSelected || (showReviewStatus && isCorrectOption == true)) TextPrimaryDark else TextSecondaryDark,
                fontSize = 15.sp,
                fontWeight = if (isSelected || (showReviewStatus && isCorrectOption == true)) FontWeight.SemiBold else FontWeight.Normal,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
