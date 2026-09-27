package com.apccarvalho.authapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.apccarvalho.authapp.R
import com.apccarvalho.authapp.domain.PasswordCriterion
import com.apccarvalho.authapp.domain.PasswordStrength
import com.apccarvalho.authapp.domain.StrengthLevel
import com.apccarvalho.authapp.ui.common.labelRes
import com.apccarvalho.authapp.ui.theme.Spacing
import com.apccarvalho.authapp.ui.theme.StrengthFair
import com.apccarvalho.authapp.ui.theme.StrengthGood
import com.apccarvalho.authapp.ui.theme.StrengthStrong
import com.apccarvalho.authapp.ui.theme.StrengthWeak

/**
 * Barra de força + checklist de critérios, atualizados a cada tecla.
 * O nível é sempre escrito por extenso: a cor nunca é a única pista.
 */
@Composable
fun PasswordStrengthMeter(strength: PasswordStrength, modifier: Modifier = Modifier) {
    val progress by animateFloatAsState(strength.progress, label = "strengthProgress")
    val barColor by animateColorAsState(strength.level.color(), label = "strengthColor")
    val levelLabel = stringResource(strength.level.labelRes())
    val a11yLabel = stringResource(R.string.strength_a11y, levelLabel)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = a11yLabel
                progressBarRangeInfo = ProgressBarRangeInfo(strength.progress, 0f..1f)
            },
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(CircleShape)
                        .background(barColor),
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = levelLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(48.dp),
            )
        }
        Spacer(Modifier.height(Spacing.sm))

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            PasswordCriterion.entries.forEach { criterion ->
                CriterionRow(criterion, met = criterion in strength.met)
            }
        }
    }
}

@Composable
private fun CriterionRow(criterion: PasswordCriterion, met: Boolean) {
    val label = stringResource(criterion.labelRes())
    val stateText = stringResource(if (met) R.string.criterion_met else R.string.criterion_not_met)
    val tint by animateColorAsState(
        if (met) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "criterionTint",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        // TalkBack lê "Uma letra maiúscula, atendido".
        modifier = Modifier.clearAndSetSemantics { contentDescription = "$label, $stateText" },
    ) {
        Icon(
            painter = painterResource(
                if (met) R.drawable.ic_check_circle else R.drawable.ic_circle_outline,
            ),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun StrengthLevel.color(): Color = when (this) {
    StrengthLevel.EMPTY -> Color.Transparent
    StrengthLevel.WEAK -> StrengthWeak
    StrengthLevel.FAIR -> StrengthFair
    StrengthLevel.GOOD -> StrengthGood
    StrengthLevel.STRONG -> StrengthStrong
}
