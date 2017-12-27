package name.dmx.androiddevguid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
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
    private val CONSTANT_ITEM = 2
    var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        return if (viewType == AppInfoViewType.Description.ordinal) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_description, parent, false)
            MyViewHolder(view)
        } else if (viewType == AppInfoViewType.Detail.ordinal) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_detail, parent, false)
            MyViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_app_detail_package, parent, false)
            view.setOnClickListener {
                onItemClickListener?.onItemClick(view, view.tag as Int)
            }
            MyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == AppInfoViewType.Description.ordinal) {
            holder?.tvDescription?.text = Html.fromHtml(appInfo.description.substring(3,appInfo.description.length-4))
        } else if (itemType == AppInfoViewType.Detail.ordinal) {
            holder?.tvDetail?.text = Html.fromHtml(appInfo.detail)
        } else {
            holder?.tvPackageName?.text = packageList[position - CONSTANT_ITEM].libPackageName
        }
        holder?.view?.tag = position - CONSTANT_ITEM
    }

    override fun getItemCount(): Int {
        return packageList.size + CONSTANT_ITEM
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            AppInfoViewType.Description.ordinal
        } else if (position == 1) {
            AppInfoViewType.Detail.ordinal
        } else {
            AppInfoViewType.PackageList.ordinal
        }
    }

    enum class AppInfoViewType {
        Description, Detail, PackageList
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        var view: View?
        var tvDescription: TextView?
        var tvDetail: TextView?
        var tvPackageName: TextView?

        constructor(view: View) : super(view) {
            this.view = view
            tvDescription = view.findViewById(R.id.tvDescription)
            tvDetail = view.findViewById(R.id.tvDetail)
            tvPackageName = view.findViewById(R.id.tvPackageName)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}