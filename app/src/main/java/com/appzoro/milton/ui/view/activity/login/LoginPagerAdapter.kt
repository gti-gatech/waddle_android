package com.appzoro.milton.ui.view.activity.login

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.appzoro.milton.R
import com.appzoro.milton.ui.view.fragment.SignInFragment
import com.appzoro.milton.ui.view.fragment.sign_up.SignUpFragment

class LoginPagerAdapter(private val mContext: Context, fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                SignUpFragment/*.NewInstance.instance*/()
            }
            else -> {
                SignInFragment/*.NewInstance.instance*/()
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }

   override fun getPageTitle(position: Int): CharSequence? {
       return if(position == 0){
//           "Sign Up"
           mContext.resources.getString(R.string.sign_up)
       }else{
           mContext.resources.getString(R.string.sign_in)
//           "Sign In"
       }
    }
}