package com.example.selfunlockalarm.feature.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.selfunlockalarm.navigation.AppDestination
import com.example.selfunlockalarm.feature.launcher.viewmodel.LaunchDecision
import com.example.selfunlockalarm.feature.launcher.viewmodel.LauncherViewModel

@Composable
fun LauncherScreen(
    navController: NavHostController,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val decision by viewModel.launchDecision.collectAsState()

    LaunchedEffect(decision) {
        when (decision) {
            LaunchDecision.NavigateToAlarmSetting -> {
                navController.navigate(AppDestination.AlarmSetting.route) {
                    popUpTo(AppDestination.Launcher.route) { inclusive = true }
                }
            }
            LaunchDecision.NavigateToPinSetting -> {
                navController.navigate(AppDestination.PinSetting.route) {
                    popUpTo(AppDestination.Launcher.route) { inclusive = true }
                }
            }
            LaunchDecision.Loading -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (decision == LaunchDecision.Loading) {
            CircularProgressIndicator()
        }
    }
}