package com.alphamiyal.locationsilencer

import android.location.Address
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 *@author:AlphaMiyaL
 * This class defines the structure of the entities in the database
 *  Each row in table represents an individual Silencer
 *  Each property will be a column on the table
 *  UUID will be the unique identifier
 */
@Entity
data class Silencer(@PrimaryKey
                    val id: UUID = UUID.randomUUID(),
                    var title: String = "",
                    var radius: Double = 1.0,
                    var address: String = "",
                    var latitude: Double = 0.0,
                    var longitude: Double = 0.0,
                    //val setLocationOn: Boolean = true,
                    var on: Boolean = true,
)