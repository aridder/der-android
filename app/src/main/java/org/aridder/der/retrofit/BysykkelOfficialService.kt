package org.aridder.der.retrofit

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import org.aridder.der.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BysykkelOfficialService{

    private val bysykkelOfficialApi: BysykkelOfficialApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gbfs.urbansharing.com/oslobysykkel.no/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        bysykkelOfficialApi = retrofit.create(BysykkelOfficialApi::class.java)
    }

    fun getStationInformationFromBysykkel(): Single<StationInfoData> {
        return bysykkelOfficialApi.listStationInformation()
    }

    fun getStationStatusFromBysykkel(): Single<StationStatusData>{
        return bysykkelOfficialApi.listStationStatus()
    }
}