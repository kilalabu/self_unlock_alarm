package com.example.selfunlockalarm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.selfunlockalarm.datastore.AlarmPreferences
import com.example.selfunlockalarm.data.datasource.AlarmPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}
