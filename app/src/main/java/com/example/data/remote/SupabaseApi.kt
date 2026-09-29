package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {
    @GET("rest/v1/blood_bags")
    suspend fun getBloodBags(
        @Query("select") select: String = "*",
        @Query("order") order: String = "expiration_date.asc"
    ): Response<List<SupabaseBloodBagDto>>

    @POST("rest/v1/blood_bags")
    @Headers("Prefer: return=representation,resolution=merge-duplicates")
    suspend fun upsertBloodBag(
        @Body bloodBag: SupabaseBloodBagDto
    ): Response<List<SupabaseBloodBagDto>>

    @POST("rest/v1/blood_bags")
    @Headers("Prefer: return=representation,resolution=merge-duplicates")
    suspend fun upsertBloodBags(
        @Body bloodBags: List<SupabaseBloodBagDto>
    ): Response<List<SupabaseBloodBagDto>>

    @PATCH("rest/v1/blood_bags")
    suspend fun updateBloodBag(
        @Query("id") idFilter: String,
        @Body updateData: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("rest/v1/blood_bags")
    suspend fun deleteBloodBag(
        @Query("id") idFilter: String
    ): Response<Unit>
}
