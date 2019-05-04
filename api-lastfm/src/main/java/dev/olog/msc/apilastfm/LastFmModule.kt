package dev.olog.msc.apilastfm

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.apilastfm.annotation.Impl
import dev.olog.msc.apilastfm.annotation.Proxy
import dev.olog.msc.apilastfm.data.LastFmRepository
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.gateway.LastFmGateway
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class LastFmModule {

    @Provides
    @Singleton
    internal fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addNetworkInterceptor(logInterceptor())
                .addInterceptor(headerInterceptor(context))
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build()
    }

    private fun logInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG){
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // disable retrofit log on release
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    private fun headerInterceptor(context: Context): Interceptor{
        return Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                    .header("User-Agent", context.packageName)
                    .method(original.method(), original.body())
                    .build()
            it.proceed(request)
        }
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
    }

    @Provides
    @Impl
    internal fun provideLastFmRest(retrofit: Retrofit): LastFmService {
        return retrofit.create(LastFmService::class.java)
    }

    @Provides
    @Singleton
    @Proxy
    internal fun provideLastFmProxy(proxy: LastFmProxy): LastFmService = proxy

    @Provides
    @Singleton
    fun provideLastFmRepository(repository: LastFmRepository): LastFmGateway {
        return repository
    }

}