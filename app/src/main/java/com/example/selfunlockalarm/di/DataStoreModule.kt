package com.example.selfunlockalarm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.selfunlockalarm.datastore.AlarmPreferences
import com.example.selfunlockalarm.data.datasource.AlarmPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    
    @Provides
    @Singleton
    fun provideAlarmPreferencesDataStore(
        @ApplicationContext context: Context,
        alarmPreferencesSerializer: AlarmPreferencesSerializer
    ): DataStore<AlarmPreferences> {
        return DataStoreFactory.create(
            serializer = alarmPreferencesSerializer,
            produceFile = { context.dataStoreFile("alarm_preferences.pb") }
        )
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<androidx.datastore.preferences.core.Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = listOf(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_DATASTORE_NAME) }
        )
    }

    private const val PREFERENCES_DATASTORE_NAME = "preferences"
}
