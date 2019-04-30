package org.aridder.der.retrofit

import io.reactivex.Single
import org.aridder.der.model.Voi
import retrofit2.http.GET

interface VoiServerApi {

    @GET ("status/ready?lat=59.911491&lo=10.757933")
    fun listBikes(): Single<List<Voi>>

}