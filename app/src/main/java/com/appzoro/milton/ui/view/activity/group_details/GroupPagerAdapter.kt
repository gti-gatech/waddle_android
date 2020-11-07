package com.appzoro.milton.ui.view.activity.group_details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.appzoro.milton.ui.view.fragment.group_details_students.GroupDetailsStudentsFragment
import com.appzoro.milton.ui.view.fragment.group_details_trips.GroupDetailsTripsFragment

class GroupPagerAdapter(private val studentsFragment: GroupDetailsStudentsFragment, private val tripsFragment: GroupDetailsTripsFragment, fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                studentsFragment
            }
            else -> {
                tripsFragment
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }

   override fun getPageTitle(position: Int): CharSequence? {
       return if(position == 0){
           "STUDENTS"
       }else{
          "TRIPS"
       }
    }
}