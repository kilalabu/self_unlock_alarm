package com.example.selfunlockalarm.alarm.setting.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfunlockalarm.alarm.setting.viewmodel.AlarmSettingUiState
import java.util.Locale

@Composable
fun AlarmSettingContent(
    uiState: AlarmSettingUiState,
    onTimeClick: () -> Unit,
    onToggleAlarm: (Boolean) -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    onPinSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "起床アラーム設定",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = String.format(Locale.ROOT, "%02d:%02d", uiState.selectedHour, uiState.selectedMinute),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onTimeClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("時間を選択")
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "アラームを有効にする", fontSize = 18.sp)
                    Switch(
                        checked = uiState.isAlarmEnabled,
                        onCheckedChange = onToggleAlarm
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !uiState.canScheduleExactAlarms) {
                    OutlinedButton(
                        onClick = onRequestExactAlarmPermission,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("正確なアラーム権限を設定")
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // PINコード設定ボタン
                Button(
                    onClick = { onPinSettingClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("PINコード設定")
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
        onPinSettingClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
