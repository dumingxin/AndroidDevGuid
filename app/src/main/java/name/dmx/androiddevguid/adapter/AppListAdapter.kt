package name.dmx.androiddevguid.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.model.AppInfo

/**
 * Created by dmx on 2017/12/21.
 */
class AppListAdapter(private val context: Context, var data: List<AppInfo>) : RecyclerView.Adapter<AppListAdapter.MyViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val item = data[position]
        Picasso.with(context).load(item.imgUrl).into(holder!!.launcher!!)
        holder?.name?.text = item.name
        holder?.downloadCount?.text = getDownloadCountStr(item.downloadCount)
        holder?.updateTime?.text = item.updateTime
        holder?.view?.tag = position
    }

    private fun getDownloadCountStr(downloadCount: Int): String {
        return if (downloadCount >= 10000) {
            (downloadCount / 10000).toString() + "万下载"
        } else {
            downloadCount.toString() + "次下载"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        val viewHolder = MyViewHolder(view)
        view.setOnClickListener {
            onItemClickListener?.onItemClick(view, view.tag as Int)
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder : RecyclerView.ViewHolder {
        var view: View
        var launcher: ImageView
        var name: TextView
        var downloadCount: TextView
        var updateTime: TextView

        constructor(view: View) : super(view) {
            this.view = view
            this.launcher = view.findViewById(R.id.sdvLauncher)
            this.name = view.findViewById(R.id.tvName)
            this.downloadCount = view.findViewById(R.id.tvDownloadCount)
            this.updateTime = view.findViewById(R.id.tvUpdateTime)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}