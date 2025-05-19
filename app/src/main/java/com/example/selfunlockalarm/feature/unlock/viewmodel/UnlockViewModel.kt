package com.example.selfunlockalarm.feature.unlock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfunlockalarm.data.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel @Inject constructor(
    private val pinRepository: PinRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UnlockUiState>(UnlockUiState.Loading)
    val uiState: StateFlow<UnlockUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = UnlockUiState.Ready(
                inputPin = "",
                correctPin = pinRepository.getPinCode(),
                verificationState = UnlockUiState.Ready.VerificationState.INITIAL
            )
        }
    }

    fun onDigitEntered(digit: String) {
        _uiState.update { currentState ->
            if (currentState is UnlockUiState.Ready) {
                if (currentState.inputPin.length < MAX_PIN_LENGTH) {
                    val newInputPin = currentState.inputPin + digit
                    val newVerificationState = if (newInputPin.length == MAX_PIN_LENGTH) {
                        // PIN長が最大に達したら検証開始
                        if (newInputPin == currentState.correctPin) {
                            UnlockUiState.Ready.VerificationState.SUCCESS
                        } else {
                            UnlockUiState.Ready.VerificationState.FAILURE
                        }
                    } else {
                        // 入力中はINITIAL状態に戻す（特にエラー後など）
                        UnlockUiState.Ready.VerificationState.INITIAL
                    }
                    currentState.copy(
                        inputPin = newInputPin,
                        verificationState = newVerificationState
                    )
                } else {
                    currentState // 長さを超えて入力はさせない
                }
            } else {
                currentState // Loading状態などでは何もしない
            }
        }
    }

    fun onBackspace() {
        _uiState.update { currentState ->
            if (currentState is UnlockUiState.Ready && currentState.inputPin.isNotEmpty()) {
                // 失敗状態から入力再開する場合、inputPinはクリアされている想定
                // もし失敗後も入力PINが残っている仕様なら、その状態からの削除も考慮
                val currentInput =
                    if (currentState.verificationState == UnlockUiState.Ready.VerificationState.FAILURE) {
                        "" // 失敗後はクリアされているはずなので、ここでの削除は実質空文字に対して行われる
                    } else {
                        currentState.inputPin
                    }

                if (currentInput.isNotEmpty()) {
                    currentState.copy(
                        inputPin = currentInput.dropLast(1),
                        verificationState = UnlockUiState.Ready.VerificationState.INITIAL // 入力中は常にINITIAL
                    )
                } else {
                    currentState // これ以上削除できない
                }

            } else {
                currentState
            }
        }
    }

    fun resetPinInputAfterFailure() {
        _uiState.update { currentState ->
            if (currentState is UnlockUiState.Ready && currentState.verificationState == UnlockUiState.Ready.VerificationState.FAILURE) {
                currentState.copy(inputPin = "") // PIN入力をクリア
            } else {
                currentState
            }
        }
    }

    companion object {
        const val MAX_PIN_LENGTH = 4 // 最大PIN長
    }
}

sealed interface UnlockUiState {
    /** PIN取得中の読み込み状態 */
    data object Loading : UnlockUiState

    /** PIN入力可能な状態 */
    data class Ready(
        /** 入力中のPINコード */
        val inputPin: String,
        /** 正しいPINコード */
        val correctPin: String,
        /** 検証状態 */
        val verificationState: VerificationState
    ) : UnlockUiState {
        enum class VerificationState {
            /** 初期状態（未検証） */
            INITIAL,

            /** PIN一致 */
            SUCCESS,

            /** PIN不一致 */
            FAILURE
        }
    }
}