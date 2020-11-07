package com.appzoro.milton.ui.view.fragment.dashboardgroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseFragment
import com.appzoro.milton.ui.view.activity.onboarding.searchgroup.SearchGroupActivity
import com.appzoro.milton.utility.Constant
import com.appzoro.milton.utility.Utils
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.dashboard_fragment_header.view.*
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.fragment_group.view.*

class GroupFragment : BaseFragment() {


    private var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group, container, false)
        view.imageViewHamburger.setOnClickListener {
            Utils.drawerOpenClose(drawerLayout = activity!!.drawer_layout)
        }
        view.imageNotification.isVisible = false
        view.mFloatingButton.setOnClickListener {
            startActivity(
                Intent(mActivity!!, SearchGroupActivity::class.java).putExtra(
                    "comeFrom",
                    "addInGroup"
                )
            )
        }
        return view
    }

    override fun setUp(view: View) {
        mActivity = activity
        setTabLayout(view)
    }

    private fun setTabLayout(view: View) {
        val viewPagerAdapter = GroupsPagerAdapter(activity!!, childFragmentManager)
        view.mViewPager?.adapter = viewPagerAdapter
        view.mTabLayout?.setupWithViewPager(view.mViewPager)
        val firstTab = (mTabLayout!!.getChildAt(0) as ViewGroup).getChildAt(0)
        val secondTab = (mTabLayout!!.getChildAt(0) as ViewGroup).getChildAt(1)

        firstTab.background =
            ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_left_round)
        secondTab.background =
            ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_right_round)

        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    firstTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_left_round)
                    secondTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_right_round)
                    view.mFloatingButton.visibility = View.VISIBLE
                } else {
                    firstTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_white_left_round)
                    secondTab.background =
                        ContextCompat.getDrawable(mActivity!!, R.drawable.bg_accent_right_round)
                    view.mFloatingButton.visibility = View.GONE
                }
            }

        })

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

    }

    override fun onResume() {
        super.onResume()
        Log.d(Constant.TAG,"Home Fragment call")

    }


}