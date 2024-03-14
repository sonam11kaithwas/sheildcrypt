package com.hlogi.diforinterface

import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sonam on 24-08-2022 15:19.
 */
interface MyOneInterFace {
    fun getMyName()
}
class MyOneInterFaceImple @Inject constructor():MyOneInterFace{
    override fun getMyName() {
        Log.e("My Name Is : ","Sona...")
    }

}

class MyMainClass @Inject constructor(private var myOneInterFace: MyOneInterFace) {
    fun getMyName(){
        myOneInterFace.getMyName()
    }
}
@Module
/*********@InstallIn(ApplicationComponent::class)  ->
 *  ApplicationComponent is Deprecated in Dagger Version 2.30
 *************/
@InstallIn(SingletonComponent::class)
abstract class MyAppModule {
    @Binds
    @Singleton
    abstract fun bindding(imple: MyOneInterFaceImple):MyOneInterFace
}