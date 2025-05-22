package com.example.selfunlockalarm.feature.unlock.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.uicommon.theme.ErrorRed
import com.example.selfunlockalarm.uicommon.theme.MdBluePrimary
import com.example.selfunlockalarm.uicommon.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.uicommon.component.PinEntry
import com.example.selfunlockalarm.feature.unlock.viewmodel.UnlockUiState
import com.example.selfunlockalarm.feature.unlock.viewmodel.UnlockViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UnlockScreen(
    modifier: Modifier = Modifier,
    viewModel: UnlockViewModel = hiltViewModel(),
    onUnlockSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.uiState.collectLatest { state ->
            if (state is UnlockUiState.Ready) {
                when (state.verificationState) {
                    UnlockUiState.Ready.VerificationState.SUCCESS -> {
                        onUnlockSuccess()
                    }

                    UnlockUiState.Ready.VerificationState.FAILURE -> {
                        viewModel.resetPinInputAfterFailure()
                    }

                    else -> {}
                }
            }
        }
    }

    when (val currentUiState = uiState) {
        UnlockUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UnlockUiState.Ready -> {
            PinEntryContent(
                modifier = modifier,
                inputPin = currentUiState.inputPin,
                verificationState = currentUiState.verificationState,
                onDigitClick = { digit -> viewModel.onDigitEntered(digit) },
                onBackspaceClick = { viewModel.onBackspace() }
            )
        }
    }
}

@Composable
private fun PinEntryContent(
    inputPin: String,
    verificationState: UnlockUiState.Ready.VerificationState,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusMessage = when (verificationState) {
        UnlockUiState.Ready.VerificationState.FAILURE -> "PINコードが違います"
        UnlockUiState.Ready.VerificationState.SUCCESS -> "アラームを解除しました"
        else -> null
    }

    val statusColor = when (verificationState) {
        UnlockUiState.Ready.VerificationState.FAILURE -> ErrorRed
        UnlockUiState.Ready.VerificationState.SUCCESS,
        UnlockUiState.Ready.VerificationState.INITIAL -> MdBluePrimary
    }

    PinEntry(
        title = "PINコードを入力してください",
        pinLength = inputPin.length,
        onDigitClick = onDigitClick,
        onBackspaceClick = onBackspaceClick,
        modifier = modifier,
        statusMessage = statusMessage,
        statusMessageColor = statusColor,
    )
}

@Preview(showBackground = true)
@Composable
fun UnlockScreenReadyPreview() {
    SelfUnlockAlarmTheme {
        PinEntryContent(
            inputPin = "12",
            verificationState = UnlockUiState.Ready.VerificationState.INITIAL, // INITIALの代わりにVERIFYINGを使用
            onDigitClick = {},
            onBackspaceClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnlockScreenErrorPreview() {
    SelfUnlockAlarmTheme {
        PinEntryContent(
            inputPin = "",
            verificationState = UnlockUiState.Ready.VerificationState.FAILURE,
            onDigitClick = {},
            onBackspaceClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnlockScreenSuccessPreview() {
    SelfUnlockAlarmTheme {
        PinEntryContent(
            inputPin = "1234",
            verificationState = UnlockUiState.Ready.VerificationState.SUCCESS,
            onDigitClick = {},
            onBackspaceClick = {}
        )
    }
}