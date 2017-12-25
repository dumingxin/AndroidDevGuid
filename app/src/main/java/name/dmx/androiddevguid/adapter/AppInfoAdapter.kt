package name.dmx.androiddevguid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.model.AppInfo
import name.dmx.androiddevguid.model.RelationApkLib

/**
 * Created by dmx on 2017/12/25.
 */
class AppInfoAdapter(private val context: Context, private var appInfo: AppInfo, private val packageList: List<RelationApkLib>) : RecyclerView.Adapter<AppInfoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return if (viewType == AppInfoViewType.Description.ordinal) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_description, parent, false)
            MyViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_package, parent, false)
            MyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == AppInfoViewType.Description.ordinal) {
            holder?.tvDescription?.text = appInfo.description
        } else {
            holder?.tvPackageName?.text = packageList[position - 1].libPackageName
        }
    }

    override fun getItemCount(): Int {
        return packageList.size + 1
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
        var tvDescription: TextView?
        var tvPackageName: TextView?

        constructor(view: View) : super(view) {
            tvDescription = view.findViewById(R.id.tvDescription)
            tvPackageName = view.findViewById(R.id.tvPackageName)
        }
    }
}