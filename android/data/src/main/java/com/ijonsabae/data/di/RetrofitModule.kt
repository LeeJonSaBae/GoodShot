package com.ijonsabae.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ijonsabae.data.BuildConfig
import com.ijonsabae.data.exception.ResultCallAdapterFactory
import com.ijonsabae.data.retrofit.ConsultService
import com.ijonsabae.data.retrofit.ProfileService
import com.ijonsabae.data.retrofit.RefreshTokenAuthorizationInterceptor
import com.ijonsabae.data.retrofit.TokenInterceptor
import com.ijonsabae.data.retrofit.TokenService
import com.ijonsabae.data.retrofit.UploadImageService
import com.ijonsabae.data.retrofit.UserService
import com.ijonsabae.domain.repository.TokenRepository
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
import javax.inject.Named

const val SERVER_IP = BuildConfig.SERVER_IP

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Named("default_okhttp_client")
    fun provideDefaultOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    @Provides
    @Named("refresh_token_okhttp_client")
    fun provideRefreshTokenOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, refreshTokenAuthorizationInterceptor: RefreshTokenAuthorizationInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(refreshTokenAuthorizationInterceptor)
            .build()
    }

    @Provides
    @Named("okhttp_client")
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, refreshTokenAuthorizationInterceptor: RefreshTokenAuthorizationInterceptor): OkHttpClient {
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
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideScalarConverterFactory(): ScalarsConverterFactory {
        return ScalarsConverterFactory.create()
    }

    @Provides
    @Named("default_retrofit")
    fun provideDefaultRetrofit(
        @Named("default_okhttp_client")client: OkHttpClient,
        scalarsConverterFactory: ScalarsConverterFactory,
        gsonConverterFactory: GsonConverterFactory,
        resultCallAdapterFactory: ResultCallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(resultCallAdapterFactory)
            .client(client)
            .build()
    }

    @Provides
    @Named("refresh_token_retrofit")
    fun provideRefreshTokenHeaderRetrofit(
        @Named("refresh_token_okhttp_client")client: OkHttpClient,
        scalarsConverterFactory: ScalarsConverterFactory,
        gsonConverterFactory: GsonConverterFactory,
        resultCallAdapterFactory: ResultCallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(resultCallAdapterFactory)
            .client(client)
            .build()
    }

    @Provides
    @Named("no_interceptor_retrofit")
    fun provideNoInterceptorRetrofit(
        @Named("okhttp_client")client: OkHttpClient,
        scalarsConverterFactory: ScalarsConverterFactory,
        gsonConverterFactory: GsonConverterFactory,
        resultCallAdapterFactory: ResultCallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(resultCallAdapterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun provideProfileService(@Named("default_retrofit")retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Provides
    fun provideUserService(@Named("default_retrofit")retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    fun provideTokenService(@Named("refresh_token_retrofit")retrofit: Retrofit): TokenService {
        return retrofit.create(TokenService::class.java)
    }

    @Provides
    fun provideUploadImageServiceService(@Named("no_interceptor_retrofit")retrofit: Retrofit): UploadImageService {
        return retrofit.create(UploadImageService::class.java)
    }

    @Provides
    fun provideExpertServiceService(@Named("no_interceptor_retrofit")retrofit: Retrofit): ConsultService {
        return retrofit.create(ConsultService::class.java)
    }
}