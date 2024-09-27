package com.ijonsabae.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ijonsabae.data.BuildConfig
import com.ijonsabae.data.exception.ResultCallAdapterFactory
import com.ijonsabae.data.retrofit.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

const val SERVER_IP = BuildConfig.SERVER_IP

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor):OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun provideGson(): Gson {
        //GSon은 엄격한 json type을 요구하는데, 느슨하게 하기 위한 설정. success, fail이 json이 아니라 단순 문자열로 리턴될 경우 처리..
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory{
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideScalarConverterFactory(): ScalarsConverterFactory{
        return ScalarsConverterFactory.create()
    }

    @Provides
    fun provideRetrofit(client:OkHttpClient,scalarsConverterFactory: ScalarsConverterFactory, gsonConverterFactory: GsonConverterFactory, resultCallAdapterFactory: ResultCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(resultCallAdapterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun provideUserService(retrofit: Retrofit): UserService{
        return retrofit.create(UserService::class.java)
    }
}