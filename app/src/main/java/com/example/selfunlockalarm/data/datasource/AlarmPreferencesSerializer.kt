package com.example.selfunlockalarm.data.datasource

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.selfunlockalarm.datastore.AlarmPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * AlarmPreferencesのシリアライザ
 */
class AlarmPreferencesSerializer @Inject constructor() : Serializer<AlarmPreferences> {
    override val defaultValue: AlarmPreferences = AlarmPreferences.newBuilder()
        .setIsEnabled(false)
        .setHour(7)
        .setMinute(0)
        .build()

    override suspend fun readFrom(input: InputStream): AlarmPreferences {
        try {
            return AlarmPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AlarmPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}