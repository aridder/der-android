package org.aridder.der.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.aridder.der.model.*
import org.aridder.der.repository.StationRepository
import org.aridder.der.retrofit.BysykkelOfficialService
import org.aridder.der.util.getDistanceToStationFromUserLocation
import kotlin.random.Random

class BysykkelViewModel(application: Application): AndroidViewModel(application){
    val stationInfoList= MutableLiveData<List<StationInfo>>().apply { postValue(emptyList()) }
    val stationStatusList = MutableLiveData<List<StationStatus>>().apply { postValue(emptyList()) }

    val stationInfoListLoading = MutableLiveData<Boolean>().apply { postValue(true) }
    val stationStatisListLoading = MutableLiveData<Boolean>().apply { postValue(true) }

    val stationInfoError = MutableLiveData<Boolean>().apply { postValue(false) }
    val stationStatusError = MutableLiveData<Boolean>().apply { postValue(false) }

    val bysykkelOfficalService = BysykkelOfficialService()
    val disposable = CompositeDisposable()

    val stationCompleteList = MutableLiveData<List<StationComplete>>().apply { postValue(emptyList()) }
    val stationCompleteLoading = MutableLiveData<Boolean>().apply { postValue((true)) }

    val stationRepository = StationRepository(application)

    init {
        val stationInfoFromRoomDb = stationRepository.getAllStationInfo()
        if(stationInfoFromRoomDb.value.isNullOrEmpty()){
            fetchStations()
        }else{
            stationInfoList.value = stationInfoFromRoomDb.value
            stationInfoListLoading.value = false
        }

        fetchStationAvailabilities()
    }

    private fun fetchStations() {
        stationInfoListLoading.value = true
        disposable.add(bysykkelOfficalService.getStationInformationFromBysykkel()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<StationInfoData>(){
                override fun onSuccess(stationList: StationInfoData) {
                    stationInfoError.value = false
                    stationInfoList.value = stationList.data.stations
                    stationInfoListLoading.value = false
                    //stationRepository.insertAllStations(stationList)
                    //stationRepository.insertAllDetailsTEST(test(stationList))
                    initMergeIfReady()
                }

                override fun onError(e: Throwable) {
                    stationInfoError.value = true
                    stationInfoListLoading.value = false
                }
            })
        )
    }

    private fun fetchStationAvailabilities(){
        stationStatisListLoading.value = true
        disposable.add(bysykkelOfficalService.getStationStatusFromBysykkel()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<StationStatusData>(){
                override fun onSuccess(stationList: StationStatusData) {
                    stationStatusError.value = false
                    stationStatisListLoading.value = false
                    stationStatusList.value = stationList.data.stations
                    initMergeIfReady()
                }

                override fun onError(e: Throwable) {
                    stationStatusError.value = true
                    stationStatisListLoading.value = false
                }
            })
        )
    }

    fun initMergeIfReady(){
        if(stationInfoListLoading.value == false && stationStatisListLoading.value == false){
            mergeStationObjectsToCompleteObject()
        }
    }

    fun mergeStationObjectsToCompleteObject(){
        stationCompleteLoading.value = true

        val stationStatus = stationStatusList.value.orEmpty()

        val stationsComplete = stationInfoList.value!!.map {
            StationComplete(it.station_id, it, stationStatus.firstOrNull{ stationSta ->
                stationSta.station_id == it.station_id
            }, test(stationInfoList.value!!).firstOrNull{ stationStats->
                stationStats.stationInfo.station_id == it.station_id
            })
        }

        stationCompleteList.value = stationsComplete
        stationCompleteLoading.value = false
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun test(stations: List<StationInfo>):List<StationStats>{
        return stations.map {
                val percentagelastDay = Random.nextInt(0, 100)
                val percentagelastWeek = Random.nextInt(0, 100)
                val percentagelastMonth = Random.nextInt(0, 100)
                val percentagelastYear = Random.nextInt(0, 100)
                val avgbikeslastDay = Random.nextInt(0, 30)
                val avgbikeslastWeek = Random.nextInt(0, 30)
                val avgbikeslastMonth = Random.nextInt(0, 30)
                val avgbikeslastYear = Random.nextInt(0, 30)
            StationStats(it, avgbikeslastDay, avgbikeslastWeek, avgbikeslastMonth, avgbikeslastYear, percentagelastDay, percentagelastWeek, percentagelastMonth, percentagelastYear )
        }
    }
}