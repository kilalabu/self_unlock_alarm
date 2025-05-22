package com.example.selfunlockalarm.feature.pinsetting.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.app.theme.ErrorRed
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.app.theme.TextBlue
import com.example.selfunlockalarm.uicommon.component.PinEntry
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
    modifier: Modifier = Modifier
) {
    val titleText = when (uiState.stage) {
        PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> "新しいPINコードを入力してください"
        PinSettingUiState.Ready.Stage.CONFIRM_PIN -> "確認のため、もう一度PINコードを入力してください"
        PinSettingUiState.Ready.Stage.COMPLETE -> "PINコードを設定しました"
    }

    val displayPin = when (uiState.stage) {
        PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> uiState.inputPin
        PinSettingUiState.Ready.Stage.CONFIRM_PIN -> uiState.confirmPin
        PinSettingUiState.Ready.Stage.COMPLETE -> uiState.inputPin
    }

    val statusMessage = when {
        uiState.errorMessage != null -> uiState.errorMessage
        uiState.stage == PinSettingUiState.Ready.Stage.COMPLETE -> "PINコードを設定しました"
        else -> null
    }

    val statusColor = if (uiState.errorMessage != null) {
        ErrorRed
    } else {
        TextBlue
    }

    PinEntry(
        title = titleText,
        pinLength = displayPin.length,
        onDigitClick = onDigitClick,
        onBackspaceClick = onBackspaceClick,
        modifier = modifier,
        statusMessage = statusMessage,
        statusMessageColor = statusColor,
    )
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_InputPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "12",
        confirmPin = "",
        stage = PinSettingUiState.Ready.Stage.INPUT_NEW_PIN
    )
    SelfUnlockAlarmTheme {
        PinSettingContent(
            uiState = sampleState,
            onDigitClick = {},
            onBackspaceClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_ConfirmPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "5678",
        confirmPin = "56",
        stage = PinSettingUiState.Ready.Stage.CONFIRM_PIN
    )
    SelfUnlockAlarmTheme {
        PinSettingContent(
            uiState = sampleState,
            onDigitClick = {},
            onBackspaceClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PinSettingScreen_ErrorPreview() {
    val sampleState = PinSettingUiState.Ready(
        inputPin = "5678",
        confirmPin = "5679",
        stage = PinSettingUiState.Ready.Stage.CONFIRM_PIN,
        errorMessage = "PINコードが違います"
    )
    SelfUnlockAlarmTheme {
        PinSettingContent(
            uiState = sampleState,
            onDigitClick = {},
            onBackspaceClick = {},
        )
    }
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
        )
    }
}
