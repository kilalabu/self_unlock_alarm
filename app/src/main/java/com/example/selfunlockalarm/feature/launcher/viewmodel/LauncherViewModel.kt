package com.example.selfunlockalarm.feature.launcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfunlockalarm.data.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val pinRepository: PinRepository
) : ViewModel() {

    private val _launchDecision = MutableStateFlow<LaunchDecision>(LaunchDecision.Loading)
    val launchDecision:  StateFlow<LaunchDecision> = _launchDecision.asStateFlow()

    init {
        viewModelScope.launch {
            val pinCode = pinRepository.getPinCode()
            if (pinCode.isNotBlank()) {
                _launchDecision.value = LaunchDecision.NavigateToAlarmSetting
            } else {
                _launchDecision.value = LaunchDecision.NavigateToPinSetting
            }
        }
    }
}

sealed interface LaunchDecision {
    data object Loading : LaunchDecision
    data object NavigateToAlarmSetting : LaunchDecision
    data object NavigateToPinSetting : LaunchDecision
}