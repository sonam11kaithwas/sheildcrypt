package com.advantal.shieldcrypt.utils_pkg

import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusResponse

/**
 * Created by Sonam on 16-08-2022 17:38.
 */
interface MySpModel {
    fun setLoginData(usrNm: String)
    fun getLoginData(): LoginResModel
    fun setAutoLoginStatus(status: Boolean)
    fun getBooleanValue(): Boolean
    fun getAutoLoginStatus(): Boolean
    fun getChatWindowOpen(): Boolean
    fun setChatWindowOpen(chatWindow: Boolean)


    fun getXSip():String
    fun setXSip(ip:String)
    fun getChatip():String
    fun setChatip(ip:String)
    fun getChatGrpData():String
    fun setChatGrpData(chatgrp:String)

}