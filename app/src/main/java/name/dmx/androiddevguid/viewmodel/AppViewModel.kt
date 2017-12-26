package name.dmx.androiddevguid.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import name.dmx.androiddevguid.MyApplication
import name.dmx.androiddevguid.http.transformer.SchedulerTransformer
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.repository.DataRepository

/**
 * Created by dmx on 2017/12/21.
 */
class AppViewModel : ViewModel() {
    private val liveData: MutableLiveData<List<AppInfo>> = MutableLiveData()
    private var pageIndex: Int = 0
    private var pageSize: Int = 0
    private val appList = ArrayList<AppInfo>()
    fun getLiveData(pageSize: Int): LiveData<List<AppInfo>> {
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
        val observable = DataRepository.getInstance(MyApplication.instance).getTopAppList(pageIndex, pageSize)
        observable.compose(SchedulerTransformer())
                .subscribe({ data ->
                    if (pageIndex == 0) {
                        appList.clear()
                    }
                    appList.addAll(appList.size, data.results?.toList()!!)
                    liveData.value = appList
                }, {
                    liveData.value = null
                })
    }
}