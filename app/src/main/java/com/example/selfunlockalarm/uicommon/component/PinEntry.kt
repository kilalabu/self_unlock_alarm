package com.example.selfunlockalarm.uicommon.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfunlockalarm.uicommon.theme.ErrorRed
import com.example.selfunlockalarm.uicommon.theme.TextGradientEnd
import com.example.selfunlockalarm.uicommon.theme.TextGradientStart

@Composable
fun PinEntry(
    title: String,
    pinLength: Int,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusMessage: String?,
    statusMessageColor: Color,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PinDisplay(pinLength = pinLength)

        if (statusMessage != null) {
            Text(
                text = statusMessage,
                color = statusMessageColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
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
private fun PinEntryPreview() {
    PinEntry(
        title = "PINコードを入力してください",
        pinLength = 2,
        onDigitClick = {},
        onBackspaceClick = {},
        statusMessage = null,
        statusMessageColor = ErrorRed,
    )
}

@Preview(showBackground = true)
@Composable
private fun PinEntryPreviewError() {
    PinEntry(
        title = "PINコードを入力してください",
        pinLength = 0,
        onDigitClick = {},
        onBackspaceClick = {},
        statusMessage = "PINコードが違います",
        statusMessageColor = ErrorRed,
    )
}