package name.dmx.androiddevguid.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hzzh.baselibrary.net.transformer.SchedulerTransformer
import name.dmx.androiddevguid.MyApplication
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.readhubclient.repository.DataRepository

/**
 * Created by dmx on 2017/12/21.
 */
class AppViewModel : ViewModel() {
    private val liveData: MutableLiveData<List<AppInfo>> = MutableLiveData()
    private var pageIndex: Int = 0
    private var pageSize: Int = 0
    private val newsList = ArrayList<AppInfo>()
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
        val observable = DataRepository.getInstance(MyApplication.instance).getAppList(pageIndex, pageSize)
        observable.compose(SchedulerTransformer())
                .subscribe({ data ->
                    if (pageIndex==0) {
                        newsList.clear()
                    }
                    newsList.addAll(newsList.size, data.results?.toList()!!)
                    liveData.value = newsList
                }, {
                    liveData.value = null
                })
    }
}