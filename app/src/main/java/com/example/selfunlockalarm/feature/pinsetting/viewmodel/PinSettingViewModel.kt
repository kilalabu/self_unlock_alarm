package com.example.selfunlockalarm.feature.pin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfunlockalarm.feature.alarm.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinSettingViewModel @Inject constructor(
    private val alarmUseCase: AlarmUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<PinSettingUiState>(PinSettingUiState.Loading)
    val uiState: StateFlow<PinSettingUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = PinSettingUiState.Ready(
                inputPin = "",
                confirmPin = "",
                stage = PinSettingUiState.Ready.Stage.INPUT_NEW_PIN
            )
        }
    }

    fun onDigitEntered(digit: String) {
        _uiState.update { currentState ->
            if (currentState is PinSettingUiState.Ready) {
                when (currentState.stage) {
                    PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> {
                        if (currentState.inputPin.length < MAX_PIN_LENGTH) {
                            val newInputPin = currentState.inputPin + digit

                            // PINが最大長に達したら確認ステージへ
                            val newStage = if (newInputPin.length == MAX_PIN_LENGTH) {
                                PinSettingUiState.Ready.Stage.CONFIRM_PIN
                            } else {
                                currentState.stage
                            }

                            currentState.copy(
                                inputPin = newInputPin,
                                stage = newStage,
                                errorMessage = null // エラーメッセージをクリア
                            )
                        } else {
                            currentState // 長さを超えて入力はさせない
                        }
                    }

                    PinSettingUiState.Ready.Stage.CONFIRM_PIN -> {
                        if (currentState.confirmPin.length < MAX_PIN_LENGTH) {
                            val newConfirmPin = currentState.confirmPin + digit

                            // 確認PINが最大長に達したら検証
                            val (newStage, errorMessage) = if (newConfirmPin.length == MAX_PIN_LENGTH) {
                                if (newConfirmPin == currentState.inputPin) {
                                    // PINが一致したら保存して完了ステージへ
                                    savePinCode(newConfirmPin)
                                    Pair(PinSettingUiState.Ready.Stage.COMPLETE, null)
                                } else {
                                    // PINが一致しなかったらエラーメッセージを表示
                                    Pair(
                                        currentState.stage,
                                        "PINコードが一致しません。もう一度お試しください。"
                                    )
                                }
                            } else {
                                Pair(currentState.stage, null)
                            }

                            currentState.copy(
                                confirmPin = newConfirmPin,
                                stage = newStage,
                                errorMessage = errorMessage
                            )
                        } else {
                            currentState // 長さを超えて入力はさせない
                        }
                    }

                    PinSettingUiState.Ready.Stage.COMPLETE -> {
                        currentState // 完了ステージでは何もしない
                    }
                }
            } else {
                currentState // Loading状態などでは何もしない
            }
        }
    }

    fun onBackspace() {
        _uiState.update { currentState ->
            if (currentState is PinSettingUiState.Ready) {
                when (currentState.stage) {
                    PinSettingUiState.Ready.Stage.INPUT_NEW_PIN -> {
                        if (currentState.inputPin.isNotEmpty()) {
                            currentState.copy(
                                inputPin = currentState.inputPin.dropLast(1),
                                errorMessage = null // エラーメッセージをクリア
                            )
                        } else {
                            currentState // これ以上削除できない
                        }
                    }

                    PinSettingUiState.Ready.Stage.CONFIRM_PIN -> {
                        if (currentState.confirmPin.isNotEmpty()) {
                            currentState.copy(
                                confirmPin = currentState.confirmPin.dropLast(1),
                                errorMessage = null // エラーメッセージをクリア
                            )
                        } else {
                            // 確認PINが空の場合は入力ステージに戻る
                            currentState.copy(
                                stage = PinSettingUiState.Ready.Stage.INPUT_NEW_PIN,
                                errorMessage = null // エラーメッセージをクリア
                            )
                        }
                    }

                    PinSettingUiState.Ready.Stage.COMPLETE -> {
                        currentState // 完了ステージでは何もしない
                    }
                }
            } else {
                currentState // Loading状態などでは何もしない
            }
        }
    }

    fun resetConfirmPin() {
        _uiState.update { currentState ->
            if (currentState is PinSettingUiState.Ready &&
                currentState.stage == PinSettingUiState.Ready.Stage.CONFIRM_PIN
            ) {
                currentState.copy(
                    confirmPin = "",
                    errorMessage = null // エラーメッセージをクリア
                )
            } else {
                currentState
            }
        }
    }

    private fun savePinCode(pinCode: String) {
        viewModelScope.launch {
            alarmUseCase.updatePinCode(pinCode)
        }
    }

    companion object {
        const val MAX_PIN_LENGTH = 4 // 最大PIN長
    }
}

sealed interface PinSettingUiState {
    /** PIN取得中の読み込み状態 */
    data object Loading : PinSettingUiState

    /** PIN入力可能な状態 */
    data class Ready(
        /** 入力中の新しいPINコード */
        val inputPin: String,
        /** 確認用のPINコード */
        val confirmPin: String,
        /** 入力ステージ */
        val stage: Stage,
        /** エラーメッセージ */
        val errorMessage: String? = null
    ) : PinSettingUiState {
        enum class Stage {
            /** 新しいPINを入力中 */
            INPUT_NEW_PIN,

            /** 確認用PINを入力中 */
            CONFIRM_PIN,

            /** PIN設定完了 */
            COMPLETE
        }
    }
}
