package com.example.selfunlockalarm.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PINコード設定用のDataStore
 */
@Singleton
class PinPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * PINコードを取得
     */
    suspend fun getPinCode(): String {
        return dataStore.data.map { preferences ->
            preferences[PIN_CODE_KEY] ?: ""
        }.first()
    }

    /**
     * PINコードを更新
     */
    suspend fun updatePinCode(pinCode: String) {
        dataStore.edit { preferences ->
            preferences[PIN_CODE_KEY] = pinCode
        }
    }

    companion object {
        private val PIN_CODE_KEY = stringPreferencesKey("pin_code")
    }
}
