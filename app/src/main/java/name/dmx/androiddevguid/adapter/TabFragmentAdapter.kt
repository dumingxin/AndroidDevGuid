package name.dmx.androiddevguid.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by dmx on 2017/12/12.
 */
class TabFragmentAdapter(fm: FragmentManager, private var fragmentList: List<Fragment>, private var titleList: List<String>) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return titleList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titleList[position]
    }
}