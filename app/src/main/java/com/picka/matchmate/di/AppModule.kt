package com.picka.matchmate.di

import android.content.Context
import androidx.room.Room
import com.picka.matchmate.local.MatchDatabase
import com.picka.matchmate.local.UserDao
import com.picka.matchmate.repository.MatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MatchDatabase {
        return Room.databaseBuilder(
            appContext,
            MatchDatabase::class.java,
            "match_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: MatchDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideMatchRepository(userDao: UserDao): MatchRepository {
        return MatchRepository(userDao)
    }
}
