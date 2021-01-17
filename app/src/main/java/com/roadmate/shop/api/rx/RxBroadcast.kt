package com.memtec.mobileecg.rx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.Cancellable

/**
 * RxBroadcast - Class to handle Android Broadcast Receivers
 */
class RxBroadcast {
    companion object {
        /**
         * fromBroadcast - Function to subscribe to BroadcastReceiver with specified IntentFilter
         *
         * @param context - Calling Context
         * @param intentFilter - Intent Filter of Broadcast
         *
         * @return Rx Object the caller ca subscribe to Broadcast
         */
        fun fromBroadcast(context: Context, intentFilter: IntentFilter): Observable<Intent> {
            return Observable.create {
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent){
                        it.onNext(intent)
                    }
                }

                it.setCancellable {
                    Log.i("RxBroadcast", "Cancel called")
                    context.unregisterReceiver(broadcastReceiver)
                }


                context.registerReceiver(broadcastReceiver, intentFilter)
            }
        }
    }
}