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
import kotlinx.android.synthetic.main.fragment_lib.*
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.activity.LibDetailActivity
import name.dmx.androiddevguid.adapter.LibListAdapter
import name.dmx.androiddevguid.model.LibInfo
import name.dmx.androiddevguid.viewmodel.LibViewModel

/**
 * Created by dmx on 2017/12/21.
 */
class LibFragment : Fragment() {

    private val PAGE_SIZE = 10
    private var dataList: List<LibInfo> = ArrayList()
    private lateinit var appViewModel: LibViewModel
    private lateinit var appLiveData: LiveData<List<LibInfo>>
    private var adapter: LibListAdapter? = null

    private val filterHeaders= arrayListOf("类型","标签")
    private lateinit var  popupView:List<View>
    private fun getObserver() = Observer<List<LibInfo>> { appList ->
        if (appList != null) {
            dataList = appList
            if (adapter == null) {
                adapter = LibListAdapter(context, dataList)
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

    private val onItemClickListener = object : LibListAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val item = dataList[position]
            val intent = LibDetailActivity.makeIntent(this@LibFragment.context, item, position)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_lib, container, false)
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popupView= arrayListOf(View(this.context), View(this.context))
        (smartRefreshLayout.parent as ViewGroup).removeView(smartRefreshLayout)
        dropDownMenu.setDropDownMenu(filterHeaders,popupView,smartRefreshLayout)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appViewModel = ViewModelProviders.of(this).get(LibViewModel::class.java)
        appLiveData = appViewModel.getLiveData(PAGE_SIZE)
        appLiveData.observe(this, getObserver())
        smartRefreshLayout.setOnRefreshListener {
            appViewModel.refresh()
        }
        smartRefreshLayout.setOnLoadmoreListener {
            appViewModel.loadMore()
        }
    }

    companion object {
        fun newInstance(): LibFragment {
            return LibFragment()
        }
    }
}