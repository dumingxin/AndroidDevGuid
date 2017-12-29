package name.dmx.androiddevguid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.LibInfo

/**
 * Created by dmx on 2017/12/25.
 */
class LibInfoAdapter(private val context: Context, private var libInfo: LibInfo, private val appList: MutableList<AppInfo>) : RecyclerView.Adapter<LibInfoAdapter.MyViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return if (viewType == AppInfoViewType.Description.ordinal) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_description, parent, false)
            MyViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_package, parent, false)
            view.setOnClickListener {
                onItemClickListener?.onItemClick(view, view.tag as Int)
            }
            MyViewHolder(view)
        }
    }

    fun appendAppList(appList: List<AppInfo>) {
        this.appList.addAll(appList)
    }

    fun getItem(position: Int): AppInfo {
        return this.appList[position]
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == AppInfoViewType.Description.ordinal) {
            holder?.tvDescription?.text = if (libInfo.description.isNullOrEmpty()) "内容待补充" else libInfo.description
        } else {
            holder?.tvPackageName?.text = appList[position - 1].name
        }
        holder?.view?.tag = position - 1
    }

    override fun getItemCount(): Int {
        return appList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            AppInfoViewType.Description.ordinal
        } else {
            AppInfoViewType.PackageList.ordinal
        }
    }

    enum class AppInfoViewType {
        Description, PackageList
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        var view: View
        var tvDescription: TextView?
        var tvPackageName: TextView?

        constructor(view: View) : super(view) {
            this.view = view
            tvDescription = view.findViewById(R.id.tvDescription)
            tvPackageName = view.findViewById(R.id.tvPackageName)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}