package com.example.selfunlockalarm.data.repository

import com.example.selfunlockalarm.data.datasource.PinPreferencesDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PINコード関連のリポジトリ
 */
@Singleton
class PinRepository @Inject constructor(
    private val pinPreferencesDataSource: PinPreferencesDataSource
) {
    /**
     * PINコードを取得
     */
    suspend fun getPinCode(): String {
        return pinPreferencesDataSource.getPinCode()
    }

    /**
     * PINコードを更新
     */
    suspend fun updatePinCode(pinCode: String) {
        pinPreferencesDataSource.updatePinCode(pinCode)
    }
}
