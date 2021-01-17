package com.roadmate.shop.api.retrofit

import com.roadmate.shop.api.rx.RxBus
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy



/**
 * Created by Zco Developer
 * Copyright Memtec
 */

class RetrofitEventListener: EventListener(){
    override fun callStart(call: Call) { RxBus.send(CallStateEvent(RetrofitEvents.CallState.STATE_CALL_START)) }
    override fun callEnd(call: Call) { RxBus.send(CallStateEvent(RetrofitEvents.CallState.STATE_CALL_END)) }
    override fun callFailed(call: Call, ioe: IOException) { RxBus.send(CallStateEvent(
        RetrofitEvents.CallState.STATE_CALL_FAILURE
    )) }

    override fun dnsStart(call: Call, domainName: String) {}
    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {}
    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) { }
    override fun secureConnectStart(call: Call) {}
    override fun secureConnectEnd(call: Call, handshake: Handshake?) { }
    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) { }
    override fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) { }
    override fun connectionAcquired(call: Call, connection: Connection) {}
    override fun connectionReleased(call: Call, connection: Connection) {}
    override fun requestHeadersStart(call: Call) {}
    override fun requestBodyStart(call: Call) {}
    override fun requestBodyEnd(call: Call, byteCount: Long) {}
    override fun responseHeadersStart(call: Call) {}
    override fun responseBodyStart(call: Call) {}
    override fun responseBodyEnd(call: Call, byteCount: Long) {}
}