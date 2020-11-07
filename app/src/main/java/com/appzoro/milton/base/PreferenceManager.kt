package com.appzoro.milton.base

import android.content.Context
import android.content.SharedPreferences
import com.appzoro.milton.utility.Constant

class PreferenceManager(val mContext: Context) {
    var sharedPreferences: SharedPreferences = mContext.getSharedPreferences("miltonApp", Context.MODE_PRIVATE)


    fun setBoolean(key: String,flagValue: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, flagValue)
        editor.apply()
    }

    fun setString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, Constant.emptyString)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, Constant.defaultBoolean)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }
    fun clearWithoutRememberMe(){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(Constant.isLogin)
        editor.remove(Constant.profileImage)
        editor.apply()

    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}