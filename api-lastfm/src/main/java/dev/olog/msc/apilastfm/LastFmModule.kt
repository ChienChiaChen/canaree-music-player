package dev.olog.msc.apilastfm

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dev.olog.msc.apilastfm.annotation.Impl
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.data.LastFmRepository
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.gateway.LastFmGateway
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class LastFmModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @Singleton
        internal fun provideLastFmRepository(repository: LastFmRepository): LastFmGateway {
            return repository
        }

        @Provides
        @JvmStatic
        @Impl
        internal fun provideLastFmRest(@ApplicationContext context: Context): LastFmService {
            val client = provideOkHttp(context)

            val retrofit = Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()

            return retrofit.create(LastFmService::class.java)
        }

        @Provides
        @Singleton
        @JvmStatic
        @Proxy
        internal fun provideLastFmProxy(proxy: LastFmProxy): LastFmService = proxy

        private fun provideOkHttp(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                .addNetworkInterceptor(logInterceptor())
                .addInterceptor(headerInterceptor(context))
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build()
        }

        private fun logInterceptor(): Interceptor {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                // disable retrofit log on release
                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            return loggingInterceptor
        }

        private fun headerInterceptor(context: Context): Interceptor {
            return Interceptor {
                val original = it.request()
                val request = original.newBuilder()
                    .header("User-Agent", context.packageName)
                    .method(original.method(), original.body())
                    .build()
                it.proceed(request)
            }
        }

    }

}