package org.aridder.der.repository

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.aridder.der.model.StationInfo

@Dao
interface StationDao {

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllStations(stations: List<StationInfo>)

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllStationInfo(stations: List<StationInfo>)

    @WorkerThread
    @Query("SELECT * FROM StationInfo" )
    fun getAllStationInfo(): List<StationInfo>

}