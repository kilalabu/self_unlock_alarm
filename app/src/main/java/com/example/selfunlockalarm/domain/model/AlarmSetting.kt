package com.example.selfunlockalarm.domain.model

data class AlarmSetting(
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int
)
