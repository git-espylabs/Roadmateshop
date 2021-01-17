package com.roadmate.shop.api.retrofit

import com.roadmate.shop.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


/**
 * Created by Zco Developer
 * Copyright Memtec
 */


/**
 * Retrofit class
 *
 * Helper class to create Retrofit service. This is used mainly in APIManager class
 */

class Retrofit{
    private var eventListener = RetrofitEventListener()

    fun <T> create(baseUrl: String, service: Class<T>): T{
        val gson = GsonBuilder().setLenient().create()
        lateinit var retrofit: Retrofit

        /*
        Check if log is enabled, if it is enabled add HTTP logging interceptor, otherwise create
        service with out logging.
         */
        if(BuildConfig.IS_LOG_ENABLED){
            val logging = CustomHttpLoggingInterceptor()
            // set your desired log level
            logging.level = CustomHttpLoggingInterceptor.Level.BODY

            retrofit = createRetrofit(
                    baseUrl,
                    logging,
                    authorizeInterceptor(),
                    ScalarsConverterFactory.create(),
                    GsonConverterFactory.create(gson),
                    RxJava2CallAdapterFactory.create(),
                    eventListener)
        }
        else{
            retrofit = createRetrofit(
                    baseUrl,
                    authorizeInterceptor(),
                    ScalarsConverterFactory.create(),
                    GsonConverterFactory.create(gson),
                    RxJava2CallAdapterFactory.create(),
                    eventListener)
        }

        return retrofit.create(service)
    }

    /**
     * Helper method to create Retrofit Service.
     *
     * Parameters
     *  @param params - Variable argument of different type, this can be Interceptor, EventListener,
     *  @param Converter.Factory, CallAdapter.Factory, Base URL String, HttpUrl
     *
     *  @return - returns the Retrofit class
     */
    private fun createRetrofit(vararg params: Any): Retrofit {
        val retrofitBuilder = Retrofit.Builder()
        val okHttpBuilder = OkHttpClient.Builder()

        for(param in params){
            when(param){
                is Interceptor -> { okHttpBuilder.interceptors().add(param) }
                is EventListener -> { okHttpBuilder.eventListener(param) }
                is Converter.Factory -> { retrofitBuilder.converterFactories().add(param) }
                is CallAdapter.Factory -> { retrofitBuilder.callAdapterFactories().add(param) }
                is String -> { retrofitBuilder.baseUrl(param) }
                is HttpUrl -> { retrofitBuilder.baseUrl(param) }
            }
        }

        return retrofitBuilder.client(okHttpBuilder.build()).build()
    }

    /**
     * authorizeInterceptor - An Interceptor used to add Authorization and other headers. In this
     * method we add the Shared Key, IMEI Number of the phone device and ServiceTag
     *
     * @return Interceptor
     */
    private fun authorizeInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestOriginal = chain.request()
            val newBuilder = requestOriginal.newBuilder()

            //Api Header from shared preference
//            newBuilder.addHeader("X-TECAS-SharedKey", ApiHeaderDetails().sharedKey)
//            newBuilder.addHeader("X-TECAS-IMEI", ApiHeaderDetails().imei)
//            newBuilder.addHeader("X-TECAS-ServiceTag", ApiHeaderDetails().serviceTag)

            val request = newBuilder.build()
            chain.proceed(request)
        }
    }
}