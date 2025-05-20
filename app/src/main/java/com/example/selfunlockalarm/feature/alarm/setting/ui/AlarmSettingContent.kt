package com.example.selfunlockalarm.feature.alarm.setting.ui

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfunlockalarm.app.theme.BgGradientEnd
import com.example.selfunlockalarm.app.theme.BgGradientVia
import com.example.selfunlockalarm.app.theme.BorderBlueLight
import com.example.selfunlockalarm.app.theme.CardBg
import com.example.selfunlockalarm.app.theme.GradientEndPurple
import com.example.selfunlockalarm.app.theme.GradientStartBlue
import com.example.selfunlockalarm.app.theme.TextBlue
import com.example.selfunlockalarm.app.theme.TextGradientEnd
import com.example.selfunlockalarm.app.theme.TextGradientStart
import com.example.selfunlockalarm.app.theme.TextWhite
import com.example.selfunlockalarm.feature.alarm.setting.viewmodel.AlarmSettingUiState
import java.util.Locale

@Composable
fun AlarmSettingContent(
    uiState: AlarmSettingUiState,
    onTimeClick: () -> Unit,
    onToggleAlarm: (Boolean) -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mainCardShape = RoundedCornerShape(32.dp)

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = mainCardShape,
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientStartBlue, GradientEndPurple)
                    )
                )
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "アラーム設定",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Time Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onTimeClick() }
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                BgGradientVia.copy(alpha = 0.7f),
                                BgGradientEnd.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(32.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d:%02d",
                        uiState.selectedHour,
                        uiState.selectedMinute
                    ),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(TextGradientStart, TextGradientEnd)
                        ),
                        letterSpacing = (-2).sp
                    )
                )
            }

            // Time Picker Button
            Button(
                onClick = onTimeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStartBlue, GradientEndPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "時間を設定",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "時間を設定",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Alarm Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                BgGradientVia.copy(alpha = 0.7f),
                                BgGradientEnd.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GradientStartBlue, GradientEndPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "アラームアイコン",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "アラームを有効にする",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Switch(
                    checked = uiState.isAlarmEnabled,
                    onCheckedChange = onToggleAlarm,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextWhite,
                        checkedTrackColor = GradientStartBlue,
                    )
                )
            }

            // Permission Button
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !uiState.canScheduleExactAlarms) {
                OutlinedButton(
                    onClick = onRequestExactAlarmPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, BorderBlueLight),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = TextBlue
                    )
                ) {
                    Text(
                        "正確なアラーム権限を設定",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmSettingContent_Preview() {
    val sampleState = AlarmSettingUiState(
        selectedHour = 7,
        selectedMinute = 30,
        isAlarmEnabled = true,
        canScheduleExactAlarms = false,
    )
    AlarmSettingContent(
        uiState = sampleState,
        onTimeClick = {},
        onToggleAlarm = {},
        onRequestExactAlarmPermission = {},
        modifier = Modifier.fillMaxWidth()
    )
}
