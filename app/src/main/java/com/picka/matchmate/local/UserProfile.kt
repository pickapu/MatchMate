package com.picka.matchmate.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.picka.matchmate.local.ProfileStatus

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "age")
    val age: Int,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "picture_url")
    val pictureUrl: String,

    @ColumnInfo(name = "education")
    val education: String,

    @ColumnInfo(name = "religion")
    val religion: String,

    @ColumnInfo(name = "match_score")
    val matchScore: Int,

    @ColumnInfo(name = "status")
    val status: ProfileStatus = ProfileStatus.PENDING
)
