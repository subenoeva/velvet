package com.subenoeva.velvet.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.subenoeva.velvet.core.domain.repository.MovieRepository
import com.subenoeva.velvet.core.network.BuildConfig
import com.subenoeva.velvet.core.network.api.TmdbApiService
import com.subenoeva.velvet.core.network.interceptor.AuthInterceptor
import com.subenoeva.velvet.core.network.repository.MovieRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkProviderModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Named("tmdb_api_key")
    fun provideTmdbApiKey(): String = BuildConfig.TMDB_API_KEY

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("tmdb_api_key") apiKey: String
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(apiKey))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingsModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository
}
