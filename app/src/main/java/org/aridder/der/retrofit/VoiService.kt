package org.aridder.der.retrofit

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import org.aridder.der.model.Voi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VoiService{

    private val voiServerApi: VoiServerApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.voiapp.io/v1/vehicle/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        voiServerApi = retrofit.create(VoiServerApi::class.java)
    }

    fun getAllBikes(): Single<List<Voi>> {
        return voiServerApi.listBikes()
    }

}