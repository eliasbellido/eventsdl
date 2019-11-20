package com.beyondthecode.shared.data.signin

import com.beyondthecode.shared.BuildConfig
import com.beyondthecode.shared.domain.internal.DefaultScheduler
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException


/**
 * Uses an HTTP client to hit an endpoint when the user changes.
 * */
object AuthenticatedUserRegistration{

    private val client: OkHttpClient by lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .build()
    }

    fun callRegistrationEndpoint(token: String){
        DefaultScheduler.execute {
            val request = Request.Builder()
                .header("Authorization", "Bearer $token")
                .url(BuildConfig.REGISTRATION_ENDPOINT_URL)
                .build()

            //Blocking call
            val response = try{
                client.newCall(request).execute()
            }catch (e: IOException){
                Timber.e(e)
                return@execute
            }

            val body = response.body()?.string()?: ""

            if(body.isEmpty() || !response.isSuccessful){
                Timber.e("Network error calling registration point (response ${response.code()} )")
                return@execute
            }

            Timber.d("Registration point called, user is registered: $body")
            response.body()?.close()

        }
    }
}