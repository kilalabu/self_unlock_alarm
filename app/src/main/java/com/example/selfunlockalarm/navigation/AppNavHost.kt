package com.example.selfunlockalarm.navigation

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.selfunlockalarm.feature.alarm.setting.ui.AlarmSettingScreen
import com.example.selfunlockalarm.feature.pinsetting.ui.PinSettingScreen
import com.example.selfunlockalarm.feature.launcher.ui.LauncherScreen

sealed class AppDestination(val route: String) {
    /**
     * アプリの起動画面
     */
    data object Launcher : AppDestination("launcher")

    /**
     * アラーム設定画面
     */
    data object AlarmSetting : AppDestination("alarm_setting")

    /**
     * PINコード設定画面
     */
    data object PinSetting : AppDestination("pin_setting")
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Launcher.route,
        modifier = modifier
    ) {
        // ルーティング画面
        composable(route = AppDestination.Launcher.route) {
            LauncherScreen(navController = navController)
        }

        // アラーム設定画面
        composable(route = AppDestination.AlarmSetting.route) {
            AlarmSettingScreen(
                onNavigateToPinSetting = {
                    navController.navigate(AppDestination.PinSetting.route)
                },
                onNavigateExactAlarmPermissionSettings = {
                    // 正確なアラーム権限を設定するための設定画面を開く
                    val intent =
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${navController.context.packageName}".toUri()
                        }
                    navController.context.startActivity(intent)
                }
            )
        }

        // PINコード設定画面
        composable(route = AppDestination.PinSetting.route) {
            PinSettingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPinSetSuccessfully = {
                    navController.navigate(AppDestination.AlarmSetting.route) {
                        // バックスタックから削除してAlarmSetting画面へ
                        popUpTo(AppDestination.Launcher.route) { inclusive = true }
                    }
                }
            )
        }
    }
}