package com.example.selfunlockalarm.data.datasource

import androidx.datastore.core.DataStore
import com.example.selfunlockalarm.datastore.AlarmPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<AlarmPreferences>
) {
    // アラーム設定を取得するFlow
    val alarmPreferencesFlow: Flow<AlarmPreferences> = dataStore.data

    // アラーム設定を更新
    suspend fun updateAlarmPreferences(
        isEnabled: Boolean? = null,
        hour: Int? = null,
        minute: Int? = null
    ) {
        dataStore.updateData { preferences ->
            val builder = preferences.toBuilder()

            isEnabled?.let { builder.setIsEnabled(it) }
            hour?.let { builder.setHour(it) }
            minute?.let { builder.setMinute(it) }

            builder.build()
        }
    }
}
