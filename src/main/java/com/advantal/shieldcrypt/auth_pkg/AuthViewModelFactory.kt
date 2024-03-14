package com.advantal.shieldcrypt.auth_pkg

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.advantal.shieldcrypt.network_pkg.MainRepository
import  com.advantal.shieldcrypt.network_pkg.NetworkHelper
import java.lang.reflect.InvocationTargetException

/**
 * Created by Sonam on 16-06-2022 13:15.
 */
class AuthViewModelFactory constructor(
//    var mainRepositorys: MainRepository,
//    var networkHelpers: NetworkHelper
) : ViewModelProvider.Factory {

    companion object {

//        private var mainRepository: MainRepository? = null
//        private val networkHelper: NetworkHelper? = null

        lateinit var context: Context

        fun createFactory(activity: Activity): AuthViewModelFactory {
            context = activity.applicationContext
                ?: throw IllegalStateException("Not yet attached to Application")
            return AuthViewModelFactory()
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        return try {
            modelClass.getConstructor()//MainRepository::class.java, NetworkHelper::class.java
                .newInstance()//mainRepository, networkHelper
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }

    }


//https://medium.com/@abuhasanbaskara/android-kotlin-live-data-mutable-live-data-example-b6a11e4d5b48
}