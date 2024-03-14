package com.advantal.shieldcrypt.network_pkg

import com.advantal.shieldcrypt.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Sonam on 27-07-2022 16:47.
 */

@Module
@InstallIn(SingletonComponent::class)
/*********@InstallIn(ApplicationComponent::class)  ->
 *  ApplicationComponent is Deprecated in Dagger Version 2.30
 *************/
class ApplicationModule {

    @Provides
//    @Binds
    fun provideBaseUrls() = BuildConfig.BASE_URL
    //"https://shieldcrypt.co.in:8011/shieldcryptUserManagementMobile/user/"//BuildConfig.BASE_URL

    @Provides
//    @Binds
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

            //                    .addConverterFactory(GsonConverterFactory.create())

            .build()
    } else OkHttpClient
        .Builder()
        .build()


    @Provides
//    @Binds
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit =
//       okHttpClient.authenticator().
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
//    @Binds
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

}

//https://blog.mindorks.com/dagger-hilt-tutorial