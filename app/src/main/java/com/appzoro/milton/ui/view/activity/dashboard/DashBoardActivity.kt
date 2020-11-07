package com.appzoro.milton.ui.view.activity.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.appzoro.milton.R
import com.appzoro.milton.base.BaseActivity
import com.appzoro.milton.base.PreferenceManager
import com.appzoro.milton.model.NavModel
import com.appzoro.milton.ui.OnClickInterface
import com.appzoro.milton.ui.view.activity.dashboard.navabout.AboutActivity
import com.appzoro.milton.ui.view.activity.dashboard.navtermsorprivacy.TermsPrivacyActivity
import com.appzoro.milton.ui.view.activity.dashboard.navtips.TipsActivity
import com.appzoro.milton.ui.view.activity.dashboard.navtrips.TripsActivity
import com.appzoro.milton.ui.view.activity.login.LoginActivity
import com.appzoro.milton.ui.view.activity.parent_details.ParentDetailsActivity
import com.appzoro.milton.ui.view.fragment.dashboardgroup.GroupFragment
import com.appzoro.milton.ui.view.fragment.dashboardhome.HomeFragment
import com.appzoro.milton.ui.view.fragment.dashboardmessage.MessageFragment
import com.appzoro.milton.ui.view.fragment.dashboardschedule.ScheduleFragment
import com.appzoro.milton.ui.view.fragment.dashboardstudent.StudentFragment
import com.appzoro.milton.utility.AlertDialogView
import com.appzoro.milton.utility.AppLogger
import com.appzoro.milton.utility.Utils
import com.appzoro.milton.utility.Utils.Companion.drawerOpenClose
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_menu_navigation_.*

class DashBoardActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navItemList: ArrayList<NavModel>
    private var backPressedToExitOnce: Boolean = false
    private val iconArray =
        arrayOf(
            R.drawable.ic_nav_profile,
            R.drawable.ic_nav_trips,
            R.drawable.ic_nav_tc,
            R.drawable.ic_nav_privacypolicy,
            R.drawable.ic_nav_tips,
            R.drawable.ic_nav_about,
            R.drawable.ic_nav_logout
        )
    private val titleNameArray = arrayOf(
        "Profile", "Trips", "Terms & Conditions", "Privacy Policy", "Tips", "About", "Logout"
    )
    private var comeFrom: String? = null
    private lateinit var mPreferenceManager: PreferenceManager

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        mPreferenceManager = PreferenceManager(this)
        bindDummyData()
        bindDataIntoNavigationView()
        try {
            comeFrom = intent.getStringExtra("comeFrom") ?: ""
            AppLogger.e("DashBoardActivity comeFrom $comeFrom")

            when (comeFrom) {
                "fromSchedule" -> {
                    loadFragment(ScheduleFragment.newInstance("dashBoard"))
                    bottomNavigation.menu.findItem(R.id.navigation_schedule).isChecked = true
                }
                "addInGroup" -> {
                    loadFragment(GroupFragment())
                    bottomNavigation.menu.findItem(R.id.navigation_groups).isChecked = true
                }
                "notification_message" -> {
                    loadFragment(MessageFragment())
                    bottomNavigation.menu.findItem(R.id.navigation_message).isChecked = true
                }
                else -> {
                    loadFragment(HomeFragment())
                    bottomNavigation.menu.findItem(R.id.navigation_home).isChecked = true
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        toggle = object : ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            private val scaleFactor = 6f
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                dashboard.scaleX = 1 - slideOffset / scaleFactor
                dashboard.scaleY = 1 - slideOffset / scaleFactor
                dashboard.translationX = slideX
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.setToolbarNavigationClickListener {
            drawerOpenClose(drawer_layout)
        }
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    private fun bindDataIntoNavigationView() {
        recyclerNavigationView.setHasFixedSize(true)
        recyclerNavigationView.adapter = NavAdapter(navItemList, object : OnClickInterface {
            override fun onClickItem(pos: Int, isSelected: Boolean) {
                when (pos) {
                    0 -> {
                        startActivity(
                            Intent(
                                this@DashBoardActivity,
                                ParentDetailsActivity::class.java
                            )
                        )
                        drawerOpenClose(drawer_layout)
                    }
                    1 -> {
                        startActivity(Intent(this@DashBoardActivity, TripsActivity::class.java))
                        drawerOpenClose(drawer_layout)
                    }
                    2 -> {
                        startActivity(
                            Intent(
                                this@DashBoardActivity,
                                TermsPrivacyActivity::class.java
                            ).putExtra(
                                "comeFrom",
                                "terms"
                            )
                        )
                        drawerOpenClose(drawer_layout)
                    }
                    3 -> {
                        startActivity(
                            Intent(
                                this@DashBoardActivity,
                                TermsPrivacyActivity::class.java
                            ).putExtra(
                                "comeFrom",
                                "privacy"
                            )
                        )
                        drawerOpenClose(drawer_layout)
                    }
                    4 -> {
                        startActivity(Intent(this@DashBoardActivity, TipsActivity::class.java))
                        drawerOpenClose(drawer_layout)
                    }
                    5 -> {
                        startActivity(Intent(this@DashBoardActivity, AboutActivity::class.java))
                        drawerOpenClose(drawer_layout)
                    }
                    6 -> {
                        drawerOpenClose(drawer_layout)
                        logOutDialog()
                    }
                }

            }

        })
    }

    private fun logOutDialog() {
        val logoutDialog = AlertDialogView.showDialogWithTwoButton(
            this,
            R.drawable.ic_signin_logo,
            getString(R.string.logout_msg),
            getString(R.string.yes),
            getString(R.string.no)
        )
        logoutDialog.buttonNo.setOnClickListener {
            logoutDialog.dismiss()

        }
        logoutDialog.buttonYes.setOnClickListener {
            logoutDialog.dismiss()
            mPreferenceManager.clearSharedPreference()
            startActivity(Intent(this, LoginActivity::class.java).putExtra("comeFrom", "logout"))
            finish()
        }
    }

    private fun bindDummyData() {
        navItemList = ArrayList()
        for (index in iconArray.indices) {
            navItemList.add(NavModel(iconArray[index], titleNameArray[index]))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_home ->
                fragment = HomeFragment()
            R.id.navigation_groups -> fragment = GroupFragment()
            R.id.navigation_schedule -> fragment = ScheduleFragment()
            R.id.navigation_students -> fragment = StudentFragment()
            R.id.navigation_message -> fragment = MessageFragment()
        }
        return loadFragment(fragment)

    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            val backStateName: String = fragment.javaClass.name
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(backStateName)
                .commit()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count > 1) supportFragmentManager.popBackStack() else {
            if (backPressedToExitOnce) ActivityCompat.finishAffinity(this) else {
                this.backPressedToExitOnce = true
                Utils.showToast(this, getString(R.string.please_back_again))
                Handler(Looper.getMainLooper())
                    .postDelayed({ backPressedToExitOnce = false }, 3000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppLogger.e("DashBoardActivity requestCode $requestCode")
        AppLogger.e("DashBoardActivity resultCode $resultCode")
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is StudentFragment) fragment.onActivityResult(
            requestCode,
            resultCode,
            data
        ) else if (fragment is ScheduleFragment) fragment.onActivityResult(
            requestCode,
            resultCode,
            data
        )


    }

}