package com.sam19hw.temiresponse.data

import android.util.Log
import com.burgstaller.okhttp.AuthenticationCacheInterceptor
import com.burgstaller.okhttp.CachingAuthenticatorDecorator
import com.burgstaller.okhttp.digest.CachingAuthenticator
import com.burgstaller.okhttp.digest.Credentials
import com.burgstaller.okhttp.digest.DigestAuthenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap


class DigestAuth {

    public fun sendRequest(): String {

        //TODO("Set the url as a input to the function, and the password as a saved set from the other input of relay number, as a hash map")
        val authenticator = DigestAuthenticator(Credentials("admin", "bewgav-3ruxqe-kynGep"))

        val authCache: Map<String, CachingAuthenticator> = ConcurrentHashMap()
        val client = OkHttpClient.Builder()
            .authenticator(CachingAuthenticatorDecorator(authenticator, authCache))
            .addInterceptor(AuthenticationCacheInterceptor(authCache))
            .build()

        var url = "http://10.72.2.85/relay/0?username=admin&password=bewgav-3ruxqe-kynGep&turn=off"
        var request: Request = Request.Builder()
            .url(url)
            .get()
            .build()
        var response: Response = client.newCall(request).execute()

        Log.d("digestAuth", response.message)
        return(response.message)
    }
}