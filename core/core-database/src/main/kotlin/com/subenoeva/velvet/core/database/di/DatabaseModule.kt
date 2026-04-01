package com.subenoeva.velvet.core.database.di

import android.content.Context
import androidx.room.Room
import com.subenoeva.velvet.core.database.VelvetDatabase
import com.subenoeva.velvet.core.database.dao.FavoriteDao
import com.subenoeva.velvet.core.database.dao.MovieDao
import com.subenoeva.velvet.core.database.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVelvetDatabase(@ApplicationContext context: Context): VelvetDatabase =
        Room.databaseBuilder(context, VelvetDatabase::class.java, "velvet.db").build()

    @Provides
    fun provideMovieDao(db: VelvetDatabase): MovieDao = db.movieDao()

    @Provides
    fun provideFavoriteDao(db: VelvetDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRemoteKeysDao(db: VelvetDatabase): RemoteKeysDao = db.remoteKeysDao()
}
