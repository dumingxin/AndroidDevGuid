package name.dmx.androiddevguid.http

import android.content.Context
import name.dmx.androiddevguid.BuildConfig
import name.dmx.androiddevguid.R
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by dmx on 2017/8/11.
 */
object DefaultOkHttpClient {
    private val DEFAULT_TIME_OUT = 10//默认的超时时间5秒钟

    fun getOkHttpClient(context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
        builder.addInterceptor({ chain ->
          chain.proceed(chain.request().newBuilder()
                   .addHeader("X-Bmob-Application-Id",BuildConfig.bombAppId)
                   .addHeader("X-Bmob-REST-API-Key",BuildConfig.bombApiKey)
                   .header("Content-Type","application/json")
                   .build())
        })
        setCertificates(context, builder)
        return builder.build()
    }

    private fun setCertificates(context: Context, builder: OkHttpClient.Builder) {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val input = context.resources.openRawResource(R.raw.api_bomb_cn)
            val trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType())
            trustStore.load(null)
            trustStore.setCertificateEntry("", certificateFactory.generateCertificate(input))
            val sslContext = SSLContext.getInstance("TLS")

            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            trustManagerFactory.init(trustStore)
            sslContext.init(
                    null,
                    Array(1,{_ -> MyTrustManager() }),
                    SecureRandom()
            )
            builder.sslSocketFactory(sslContext.socketFactory,MyTrustManager())


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
   class MyTrustManager: X509TrustManager {
       override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
           return arrayOf()
       }

       @Throws(CertificateException::class)
       override fun checkClientTrusted(chain: Array<X509Certificate>,
                                       authType: String) {
       }

       @Throws(CertificateException::class)
       override fun checkServerTrusted(chain: Array<X509Certificate>,
                                       authType: String) {
       }
   }
}