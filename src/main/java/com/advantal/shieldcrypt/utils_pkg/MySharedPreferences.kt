package com.advantal.shieldcrypt.utils_pkg

import android.content.Context
import android.content.SharedPreferences
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.google.gson.Gson

/**
 * Created by Sonam on 16-08-2022 17:34.
 */
class MySharedPreferences : MySpModel {

    private val LOGIN_DATA = "login_data"
    private val XIP = "x_ip"
    private val CHATXIP = "chatx_ip"
    private val AUTOLOGIN = "autoLogin"
    private val ADDCHATGRP = "addchatgrp"
    private val CROPPED_IMAGE = "cropped_image"
    private val CHAT_WINDOW_OPEN = "chat_window_open"
    private val APP_AUTO_LOGIN_STATUS = "appAutoLoginStatus"


    constructor() {
        mySharedPreferences =
            MyApp.getAppInstance().getSharedPreferences(MyPREFNAME, Context.MODE_PRIVATE)
        preferencesEditor = mySharedPreferences.edit()
    }

    override fun setLoginData(loginData: String) {
        preferencesEditor.putString(LOGIN_DATA, loginData)
        preferencesEditor.commit()
    }

    override fun getLoginData(): LoginResModel {
        var str = mySharedPreferences.getString(LOGIN_DATA, "")!!
        val model: LoginResModel = Gson().fromJson(str, LoginResModel::class.java)
        return model
    }


    companion object {
        private var INSTANCE = MySharedPreferences()
        private lateinit var mySharedPreferences: SharedPreferences
        private lateinit var preferencesEditor: SharedPreferences.Editor
        private var MyPREFNAME = "shield_crypt"

        @JvmStatic
        fun getSharedprefInstance(): MySpModel {
            return INSTANCE
        }
    }

    override fun getBooleanValue(): Boolean {
        return mySharedPreferences.getBoolean(AUTOLOGIN, false)
    }


    override fun getChatWindowOpen(): Boolean {

        return mySharedPreferences.getBoolean(CHAT_WINDOW_OPEN, false)
    }

    override fun getChatip(): String {
        return mySharedPreferences.getString(CHATXIP, "92.204.128.15")!!
    }

    override fun setChatip(ip: String) {
        preferencesEditor.putString(CHATXIP, ip)
        preferencesEditor.commit()
    }

    override fun getChatGrpData(): String {
        return mySharedPreferences.getString(ADDCHATGRP, "")!!
    }

    override fun setChatGrpData(chatgrp: String) {
        preferencesEditor.putString(ADDCHATGRP, chatgrp)
        preferencesEditor.commit()
    }

    override fun setChatWindowOpen(chatWindow: Boolean) {
        preferencesEditor.putBoolean(CHAT_WINDOW_OPEN, chatWindow)
        preferencesEditor.commit()
    }

    override fun getXSip(): String {
        return mySharedPreferences.getString(XIP, "shieldcrypt.co.in")!!

    }

    override fun setXSip(ip: String) {
        preferencesEditor.putString(XIP, ip)
        preferencesEditor.commit()
    }

    fun setCroppedImage(img: String) {
        preferencesEditor.putString(CROPPED_IMAGE, img)
        preferencesEditor.commit()
    }

    override fun getAutoLoginStatus(): Boolean {
        return mySharedPreferences.getBoolean(APP_AUTO_LOGIN_STATUS, false)
    }

    override fun setAutoLoginStatus(status: Boolean) {
        preferencesEditor.putBoolean(APP_AUTO_LOGIN_STATUS, status)
        preferencesEditor.commit()
    }
}
