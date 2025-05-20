package com.example.selfunlockalarm.feature.unlock.ui

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.app.theme.TextGradientEnd
import com.example.selfunlockalarm.app.theme.TextGradientStart
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PINコードを入力してください",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PinDisplay(pinLength = inputPin.length)

        when (verificationState) {
            UnlockUiState.Ready.VerificationState.FAILURE -> {
                Text(
                    text = "PINコードが違います",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            UnlockUiState.Ready.VerificationState.SUCCESS -> {
                Text(
                    text = "アラームを解除しました",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            else -> {
                // メッセージがない場合は、同じ高さのスペーサーを確保してレイアウトがガタつくのを防ぐ
                Spacer(modifier = Modifier.height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 8.dp))
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        NumericKeypad(
            onDigitClick = onDigitClick,
            onBackspaceClick = onBackspaceClick
        )
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
                        colors = listOf(TextGradientStart, TextGradientEnd) // グラデーション色
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
                .width(280.dp) // keypadの幅を固定
                .align(Alignment.Center), // Box内で中央揃え
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
            inputPin = "1234",
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