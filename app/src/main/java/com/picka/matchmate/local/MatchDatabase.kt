package com.picka.matchmate.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class MatchDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
