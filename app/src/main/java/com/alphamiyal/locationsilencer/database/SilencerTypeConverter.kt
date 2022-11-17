package com.alphamiyal.locationsilencer.database

import android.location.Address
import android.location.Geocoder
import androidx.room.TypeConverter
import java.util.*


class SilencerTypeConverter {
    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }
}