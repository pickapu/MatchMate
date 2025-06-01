package com.picka.matchmate.repository

import com.picka.matchmate.local.ProfileStatus
import com.picka.matchmate.local.UserDao
import com.picka.matchmate.local.UserProfile
import com.picka.matchmate.network.RetrofitInstance
import com.picka.matchmate.network.models.RandomUserDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random

class MatchRepository( private val userDao: UserDao) {

    private val ourAge = 30
    private val ourCity = "Mumbai"

    fun getAllStoredProfiles(): Flow<List<UserProfile>> {
        return userDao.getAllProfiles()
    }

    fun refreshProfiles(): Flow<RefreshResult> = flow {
        emit(RefreshResult.Loading)

        val shouldFail = Random.nextInt(100) < 30
        delay(500)

        if (shouldFail) {
            emit(RefreshResult.Error("Simulated network failure (30% chance)"))
            return@flow
        }

        try {
            val response = RetrofitInstance.api.fetchRandomUsers(results = 10)
            if (response.isSuccessful) {
                val rawList = response.body()?.results ?: emptyList()
                val profilesToSave = rawList.map { dto -> mapDtoToEntity(dto) }
                userDao.insertProfiles(profilesToSave)
                emit(RefreshResult.Success)
            } else {
                emit(RefreshResult.Error("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(RefreshResult.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    suspend fun updateProfileStatus(email: String, newStatus: ProfileStatus) {
        val existing = userDao.getAllProfiles().first().firstOrNull { it.email == email }
        existing?.let {
            val updated = it.copy(status = newStatus)
            userDao.updateProfile(updated)
        }
    }

    private fun mapDtoToEntity(dto: RandomUserDto): UserProfile {
        val fullName = "${dto.name.first} ${dto.name.last}"
        val age = dto.dob.age
        val city = dto.location.city
        val pictureUrl = dto.picture.large

        val educationOptions = listOf(
            "High School Diploma", "Bachelor’s in Engineering",
            "Master’s in Business Admin", "PhD in Sciences"
        )
        val religionOptions = listOf("Hindu", "Muslim", "Christian", "Sikh", "Other")

        val education = educationOptions.random()
        val religion = religionOptions.random()

        val ageScore = maxOf(0, 100 - (kotlin.math.abs(ourAge - age) * 2))
        val cityBonus = if (city.equals(ourCity, ignoreCase = true)) 20 else 0
        val matchScore = minOf(100, ageScore + cityBonus)

        val emailPlaceholder = "${dto.name.first.lowercase()}.${dto.name.last.lowercase()}@example.com"

        return UserProfile(
            email = emailPlaceholder,
            fullName = fullName,
            age = age,
            city = city,
            pictureUrl = pictureUrl,
            education = education,
            religion = religion,
            matchScore = matchScore,
            status = ProfileStatus.PENDING
        )
    }
}
