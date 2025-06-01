package com.picka.matchmate.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE status = :status")
    fun getProfilesByStatus(status: ProfileStatus): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles")
    suspend fun clearAll()
}
