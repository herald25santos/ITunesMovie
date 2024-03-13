package com.appetiser.data.api

import com.appetiser.data.entities.MovieData
import com.appetiser.data.entities.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    /**
     * default 1 since itunes apple doesn't have  page query
     * @param page pagination
     * @param limit max return from movies api
     */
    @GET("/search")
    suspend fun getMovies(
        @Query("term")
        term: String,
        @Query("country")
        country: String,
        @Query("media")
        media: String,
        @Query("page")
        page: Int? = null,
        @Query("limit")
        limit: Int? = null,
    ): MovieResponse<MutableList<MovieData>>

    @GET("/search")
    suspend fun search(
        @Query("term")
        term: String,
        @Query("country")
        country: String,
        @Query("media")
        media: String,
        @Query("page")
        page: Int? = null,
        @Query("limit")
        limit: Int? = null,
    ): MovieResponse<MutableList<MovieData>>

    @GET("/search&all")
    suspend fun getMovies(
        @Query("id")
        movieIds: List<Int>
    ): List<MovieData>

    @GET("/movies/{id}")
    suspend fun getMovie(
        @Path("id")
        movieId: Int
    ): MovieData
}