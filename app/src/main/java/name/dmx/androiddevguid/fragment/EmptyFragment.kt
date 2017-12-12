package name.dmx.androiddevguid.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import name.dmx.androiddevguid.R

/**
 * Created by dmx on 2017/12/12.
 */
class EmptyFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater?.inflate(R.layout.fragment_empty,container,false)
        return  view
    }
    companion object {
        fun  newInstance():EmptyFragment{
            val fragment=EmptyFragment()
            return fragment
        }
    }
}