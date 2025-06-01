package com.picka.matchmate.network

import com.picka.matchmate.network.models.RandomUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserService {
    @GET("api/")
    suspend fun fetchRandomUsers(@Query("results") results: Int = 10): Response<RandomUserResponse>
}
