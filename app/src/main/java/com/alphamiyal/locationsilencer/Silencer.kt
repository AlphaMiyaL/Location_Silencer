package com.alphamiyal.locationsilencer

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
                    val title: String = "",
                    val location: Location? = null,
                    val setLocationOn: Boolean = true,
                    val setSilencerOn: Boolean = true
)