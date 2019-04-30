package org.aridder.der.model

import androidx.room.*

@Entity
data class StationInfo(
    @PrimaryKey
    var station_id: String,
    var name: String,
    var address: String,
    var lat: Float,
    var lon: Float,
    var capacity: Int
)

data class StationInfoData(
    val last_updated: Int,
    val ttl: Int,
    val data: StationInfoArray
)

data class StationInfoArray (
    val stations: List<StationInfo>
)

data class StationStatusData(
    val last_updated: Int,
    val ttl: Int,
    val data: StationStatusList
)

data class StationStatusList(
    val stations: List<StationStatus>
)

data class StationStatus(
    var station_id: String,
    var is_installed: Int,
    var is_renting: Int,
    var is_returning: Int,
    var last_reported: Int,
    var num_bikes_available: Int,
    var num_docks_available: Int
)


data class StationComplete(
    val id: String,
    val stationInfo: StationInfo?,
    val stationStatus: StationStatus?,
    val stats: StationStats?
)

@Entity
data class StationStats(
    @Embedded
    val stationInfo: StationInfo,
    val averageNumberOfBikesInStationLastDay: Int,
    val averageNumberOfBikesInStationLastWeek: Int,
    val averageNumberOfBikesInStationLastMonth: Int,
    val averageNumberOfBikesInStationLastYear: Int,
    val percentageOfTimeBikeAvailableLastDay: Int,
    val percentageOfTimeBikeAvailableLastWeek: Int,
    val percentageOfTimeBikeAvailableLastMonth: Int,
    val percentageOfTimeBikeAvailableLastYear: Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
