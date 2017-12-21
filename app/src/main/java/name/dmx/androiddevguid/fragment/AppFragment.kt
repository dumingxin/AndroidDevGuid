package name.dmx.androiddevguid.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_app.*
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.adapter.AppListAdapter
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.viewmodel.AppViewModel

/**
 * Created by dmx on 2017/12/21.
 */
class AppFragment : Fragment() {

    private val PAGE_SIZE = 10
    private var dataList: List<AppInfo> = ArrayList()
    private lateinit var newsViewModel: AppViewModel
    private lateinit var newsLiveData: LiveData<List<AppInfo>>
    private var adapter: AppListAdapter? = null

    private fun getObserver() = Observer<List<AppInfo>> { newsList ->
        if (newsList != null) {
            dataList = newsList
            if (adapter == null) {
                adapter = AppListAdapter(context, dataList)
                adapter!!.onItemClickListener = onItemClickListener
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter
            } else {
                adapter?.data = dataList
            }
            smartRefreshLayout.finishLoadmore()
            smartRefreshLayout.finishRefresh()
            adapter!!.notifyDataSetChanged()
            recyclerView.scrollToPosition(dataList.size - PAGE_SIZE)
        }
    }

    private val onItemClickListener = object : AppListAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val item = dataList[position]
//            val intent = WebViewActivity.makeIntent(context, item.url, item.title, "")
//            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_app, container, false)
        return view!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newsViewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)
        newsLiveData = newsViewModel.getLiveData(PAGE_SIZE)
        newsLiveData.observe(this, getObserver())
        smartRefreshLayout.setOnRefreshListener {
            newsViewModel.refresh()
        }
        smartRefreshLayout.setOnLoadmoreListener {
            newsViewModel.loadMore()
        }
    }

    companion object {
        fun newInstance(): AppFragment {
            return AppFragment()
        }
    }
}