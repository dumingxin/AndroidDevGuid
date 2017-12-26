package name.dmx.androiddevguid.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import name.dmx.androiddevguid.MyApplication
import name.dmx.androiddevguid.http.transformer.SchedulerTransformer
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.repository.DataRepository

/**
 * Created by dmx on 2017/12/21.
 */
class LibViewModel : ViewModel() {
    private val liveData: MutableLiveData<List<LibInfo>> = MutableLiveData()
    private var pageIndex: Int = 0
    private var pageSize: Int = 0
    private val libList = ArrayList<LibInfo>()
    fun getLiveData(pageSize: Int): LiveData<List<LibInfo>> {
        this.pageSize = pageSize
        fetchData()
        return liveData
    }

    fun refresh() {
        pageIndex = 0
        fetchData()
    }

    fun loadMore() {
        pageIndex++
        fetchData()
    }

    private fun fetchData() {
        val observable = DataRepository.getInstance(MyApplication.instance).getTopLibList(pageIndex, pageSize)
        observable.compose(SchedulerTransformer())
                .subscribe({ data ->
                    if (pageIndex == 0) {
                        libList.clear()
                    }
                    libList.addAll(libList.size, data.results?.toList()!!.sortedByDescending { item-> return@sortedByDescending item._count })
                    liveData.value = libList
                }, {
                    liveData.value = null
                })
    }
}