package com.example.selfunlockalarm.feature.pinsetting.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.app.theme.MdPurpleSecondary
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.app.theme.TextGradientEnd
import com.example.selfunlockalarm.app.theme.TextGradientStart
import com.example.selfunlockalarm.feature.pinsetting.viewmodel.PinSettingUiState
import com.example.selfunlockalarm.feature.pinsetting.viewmodel.PinSettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: PinSettingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPinSetSuccessfully: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.uiState.collectLatest { state ->
            if (state is PinSettingUiState.Ready && state.stage == PinSettingUiState.Ready.Stage.COMPLETE) {
                // PIN設定完了後、少し待ってから前の画面に戻る
                delay(1500)
                onPinSetSuccessfully()
            }
        }
    }
    SelfUnlockAlarmTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PINコード設定") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                        }
                    }
                )
            }
        ) { innerPadding ->
            when (val currentUiState = uiState) {
                PinSettingUiState.Loading -> {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PinSettingUiState.Ready -> {
                    PinSettingContent(
                        uiState = currentUiState,
                        onDigitClick = { digit -> viewModel.onDigitEntered(digit) },
                        onBackspaceClick = { viewModel.onBackspace() },
                        onResetConfirmPin = { viewModel.resetConfirmPin() },
                        modifier = modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun PinSettingContent(
    uiState: PinSettingUiState.Ready,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onResetConfirmPin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleText = when (uiState.stage) {
            PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> "新しいPINコードを入力してください"
            PinSettingUiState.Ready.Stage.CONFIRM_PIN -> "確認のため、もう一度PINコードを入力してください"
            PinSettingUiState.Ready.Stage.COMPLETE -> "PINコードを設定しました"
        }

        Text(
            text = titleText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val displayPin = when (uiState.stage) {
            PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> uiState.inputPin
            PinSettingUiState.Ready.Stage.CONFIRM_PIN -> uiState.confirmPin
            PinSettingUiState.Ready.Stage.COMPLETE -> uiState.inputPin
        }

        PinDisplay(pinLength = displayPin.length)

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onResetConfirmPin,
                modifier = Modifier.padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MdPurpleSecondary,
                    contentColor = Color.White
                ),
            ) {
                Text("もう一度入力する")
            }
        } else if (uiState.stage == PinSettingUiState.Ready.Stage.COMPLETE) {
            Text(
                text = "PINコードを設定しました",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            // エラーメッセージがない場合は、同じ高さのスペーサーを確保
            Spacer(modifier = Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 8.dp + 8.dp + 40.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 完了ステージ以外ではキーパッドを表示
        if (uiState.stage != PinSettingUiState.Ready.Stage.COMPLETE) {
            NumericKeypad(
                onDigitClick = onDigitClick,
                onBackspaceClick = onBackspaceClick
            )
        }
    }
}

@Composable
private fun PinDisplay(pinLength: Int, maxLength: Int = 4) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(maxLength) { index ->
            val dotModifier = if (index < pinLength) {
                Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(TextGradientStart, TextGradientEnd)
                    ),
                    shape = CircleShape
                )
            } else {
                Modifier.background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = CircleShape
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .then(dotModifier)
            )
        }
    }
}

@Composable
private fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "", "0", "⌫" // "" は空きスペース、"⌫" はバックスペース
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .width(280.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(buttons) { buttonText ->
                val buttonModifier = Modifier.size(width = 80.dp, height = 64.dp)

                when (buttonText) {
                    "" -> Spacer(modifier = buttonModifier)
                    "⌫" -> {
                        OutlinedButton(
                            onClick = onBackspaceClick,
                            modifier = buttonModifier,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Backspace",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    else -> {
                        Button(
                            onClick = { onDigitClick(buttonText) },
                            modifier = buttonModifier,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(TextGradientStart, TextGradientEnd)
                                        ),
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    buttonText,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_InputPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "12",
        confirmPin = "",
        stage = PinSettingUiState.Ready.Stage.INPUT_NEW_PIN
    )
    PinSettingContent(
        uiState = sampleState,
        onDigitClick = {},
        onBackspaceClick = {},
        onResetConfirmPin = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_ConfirmPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "5678",
        confirmPin = "56",
        stage = PinSettingUiState.Ready.Stage.CONFIRM_PIN
    )
    PinSettingContent(
        uiState = sampleState,
        onDigitClick = {},
        onBackspaceClick = {},
        onResetConfirmPin = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_ErrorPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "5678",
        confirmPin = "5679",
        stage = PinSettingUiState.Ready.Stage.CONFIRM_PIN,
        errorMessage = "PINコードが一致しません。もう一度お試しください。"
    )
    PinSettingContent(
        uiState = sampleState,
        onDigitClick = {},
        onBackspaceClick = {},
        onResetConfirmPin = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_CompletePreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "5678",
        confirmPin = "5678",
        stage = PinSettingUiState.Ready.Stage.COMPLETE
    )
    SelfUnlockAlarmTheme {
        PinSettingContent(
            uiState = sampleState,
            onDigitClick = {},
            onBackspaceClick = {},
            onResetConfirmPin = {}
        )
    }
}
