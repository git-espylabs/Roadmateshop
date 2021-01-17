package com.roadmate.shop.api.manager

import com.roadmate.shop.BuildConfig
import com.roadmate.shop.api.retrofit.Retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class APIManager {

    companion object{
        var rxRetrofit = Retrofit()
        var map = HashMap<String, Any>()

        fun isSuccess(errorCode: Int?): Boolean = (errorCode == 0)

        /**
         * service - Returns the specified retrofit service of type T
         * This is used to receive the service for retrofit calls, currently we have only one service
         * TMECloudService.
         *
         * The service is cached for future use in a HashMap. If it is already in map, returns from
         * the map otherwise create, add to the map and return to the caller.
         *
         * @return - Service of type T
         */
        inline fun <reified T> service(): T{
            return if(map.containsKey(T::class.toString())){
                map[T::class.toString()] as T
            } else{
                var cls = rxRetrofit.create(BuildConfig.URL_ENDPOINT, T::class.java)

                map[T::class.toString()] = cls as Any

                cls
            }
        }

        /**
         * call - Call a web service. This is a suspended function.
         *
         * @sample:
         *
         * APIManager.call<TMECloudService, retrofit2.Response<ResponseBody>> {
        sendClinicalData(
        Constants.CONTENT_TYPE_OCTET_STREAM,
        ecgData[0].stripNum.toString(),
        Constants.STRIP_COUNT_IN_CLINICAL_DATA,
        CrcUtils().getCRCValue(
        outputStream.toByteArray().contentToString().replace(
        " ",
        ""
        ).toByteArray()
        ),
        outputStream.toByteArray()
        )
        }
         *
         * @param block - Web service bloc to execute
         * @return - Returns the return data from the webservice
         */
        suspend inline fun <reified T, reified R> call(crossinline block: suspend T.() -> R): R {
            val retval = GlobalScope.async (Dispatchers.IO) {
                with(service<T>()){
                    this.block()
                }
            }.await()

            return retval
        }
    }
}