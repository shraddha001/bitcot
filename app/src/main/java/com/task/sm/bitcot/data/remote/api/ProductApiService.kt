package com.task.sm.bitcot.data.remote.api

import com.task.sm.bitcot.data.remote.dto.ProductDetailResponseDto
import com.task.sm.bitcot.data.remote.dto.ProductListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("api/v1/public/randomproducts")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): ProductListResponseDto

    @GET("api/v1/public/randomproducts/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Int
    ): ProductDetailResponseDto
}
