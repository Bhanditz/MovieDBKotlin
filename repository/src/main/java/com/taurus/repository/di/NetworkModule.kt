package com.taurus.repository.di

import com.taurus.repository.BuildConfig
import com.taurus.repository.remote.MovieDBService
import com.taurus.repository.remote.RequestInterceptor
import dagger.Module
import dagger.Provides
import extensions.debug
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

  @Provides
  @Singleton
  fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
      .setLevel(HttpLoggingInterceptor.Level.BODY)

  @Provides
  @Singleton
  fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    val okhttpBuilder = OkHttpClient.Builder()
    debug { okhttpBuilder.addInterceptor(loggingInterceptor) }
    okhttpBuilder.addInterceptor(RequestInterceptor())
    return okhttpBuilder.build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
      Retrofit.Builder()
          .client(okHttpClient)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(MoshiConverterFactory.create())
          .baseUrl(BuildConfig.API_URL)
          .build()

  @Provides
  @Singleton
  fun provideMovieDBService(retrofit: Retrofit) = retrofit.create(MovieDBService::class.java)
}