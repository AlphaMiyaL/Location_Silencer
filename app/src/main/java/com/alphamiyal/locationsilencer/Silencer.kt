package com.alphamiyal.locationsilencer

import android.location.Address
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
                    var idInt: Int = -1,
                    var title: String = "",
                    var radius: Double = 0.0,
                    var unit: String = "Meters",
                    var address: String = "",
                    var thoroughfare: String = "",
                    var subThoroughfare: String = "",
                    var locality: String = "",
                    var adminArea: String = "",
                    var postalCode: String = "",
                    var latitude: Double = 0.0,
                    var longitude: Double = 0.0,
                    var startTime: Date = Date(),
                    var endTime: Date = Date(),
                    var useLoc: Boolean = true,
                    var useTime: Boolean = false,
                    var on: Boolean = true,
)