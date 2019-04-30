package org.aridder.der.retrofit

import io.reactivex.Single
import org.aridder.der.model.*
import retrofit2.http.GET

interface BysykkelOfficialApi {

    @GET("station_information.json")
    fun listStationInformation(): Single<StationInfoData>

    @GET("station_status.json")
    fun listStationStatus(): Single<StationStatusData>
}