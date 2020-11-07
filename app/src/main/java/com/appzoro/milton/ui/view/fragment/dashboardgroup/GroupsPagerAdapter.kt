package com.appzoro.milton.ui.view.fragment.dashboardgroup

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.appzoro.milton.ui.view.fragment.group_my_groups.MyGroupsFragment
import com.appzoro.milton.ui.view.fragment.group_nearby.NearbyFragment

class GroupsPagerAdapter(private val mContext: Context, fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                MyGroupsFragment()
            }
            else -> {
                NearbyFragment()
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }

   override fun getPageTitle(position: Int): CharSequence? {
       return if(position == 0){
           "My GROUPS"
       }else{
          "NEARBY"
       }
    }
}