package org.aridder.der.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.aridder.der.model.Voi
import org.aridder.der.retrofit.VoiService

class VoiViewModel(application: Application): AndroidViewModel(application){
    val voiBike = MutableLiveData<List<Voi>>()
    val voiBikesLoading = MutableLiveData<Boolean>().apply { postValue(true) }
    val voiBikesLoadingError = MutableLiveData<Boolean>().apply { postValue(false) }

    val disposable = CompositeDisposable()

    val voiService = VoiService()

    init {
        fetchBikes()
    }

    private fun fetchBikes(){
        voiBikesLoading.value = true
        disposable.add(voiService.getAllBikes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Voi>>(){
                override fun onSuccess(t: List<Voi>) {
                    voiBikesLoadingError.value = false
                    voiBikesLoading.value = false
                    voiBike.value = t
                }

                override fun onError(e: Throwable) {
                    voiBikesLoading.value = false
                    voiBikesLoadingError.value = true
                }
            }))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}