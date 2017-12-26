package name.dmx.androiddevguid.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import name.dmx.androiddevguid.R
import name.dmx.androiddevguid.model.LibInfo


/**
 * Created by dmx on 2017/12/21.
 */
class LibListAdapter(private val context: Context, var data: List<LibInfo>) : RecyclerView.Adapter<LibListAdapter.MyViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    private val colorArray: IntArray = context.resources.getIntArray(R.array.randomColor)

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val item = data[position]
        val name = item.packageName.substring(item.packageName.lastIndexOf('.') + 1)
        //动态设置圆角背景
        val gd = GradientDrawable()//创建drawable
        gd.setColor(colorArray[position % colorArray.size])
        gd.cornerRadius = 20f
        holder?.launcher?.background = gd
        holder?.launcher?.text = name
        holder?.name?.text = item.packageName
        holder?.downloadCount?.text = getDownloadCountStr(item._count)
        holder?.view?.tag = position
    }

    private fun getDownloadCountStr(downloadCount: Int): String {
        return downloadCount.toString() + "次引用"
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lib, parent, false)
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
        var launcher: TextView
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