package com.alphamiyal.locationsilencer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.alphamiyal.locationsilencer.Silencer
import java.util.UUID


@Dao
interface SilencerDao {
    @Query("SELECT  * FROM silencer")
    fun getSilencers(): LiveData<List<Silencer>>


    @Query("SELECT  * FROM silencer WHERE id=(:id)")
    fun getSilencer(id: UUID): LiveData<Silencer?>

    @Update
    fun updateSilencer(silencer: Silencer)
    @Insert
    fun addSilencer(silencer: Silencer)

    @Delete
    fun deleteSilencer(silencer: Silencer)
}