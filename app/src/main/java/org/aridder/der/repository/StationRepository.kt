package org.aridder.der.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData

import org.aridder.der.model.StationInfo
import org.jetbrains.anko.doAsync

class StationRepository(val application: Application) {
    val stationDao = Database.getDatabase(application)!!.StationDao()

    fun insertAllStationInfo(stations: List<StationInfo>) {
        doAsync {
            stationDao.insertAllStations(stations)
        }
    }

    fun getAllStationInfo(): MutableLiveData<List<StationInfo>>{
        val stations = MutableLiveData<List<StationInfo>>()
        doAsync {
            stations.postValue(stationDao.getAllStationInfo())
        }
        return stations
    }
}