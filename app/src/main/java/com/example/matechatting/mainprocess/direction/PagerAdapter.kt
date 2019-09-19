package com.example.matechatting.mainprocess.direction

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class PagerAdapter(private val fragmentList: List<Fragment>, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return position.toString()
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {

    }
}