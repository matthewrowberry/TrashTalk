package com.usuhackathon.trashtalk.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class League(
    val id: String,
    val name: String,
    val description: String
)

data class LeagueResponse(val success: Boolean, val league_id: String? = null, val error: String? = null)
data class GenericResponse(val success: Boolean, val error: String? = null)

data class LeagueMember(
    val user_uid: String,
    val total_points: Int,
    val completed_count: Int
)

data class LeagueMembersResponse(val members: List<LeagueMember>)

data class Chore(
    val id: String,
    val name: String,
    val description: String,
    val points: Int,
    val creator_uid: String? = null
)

data class CreateChoreRequest(
    val user_uid: String,
    val league_id: String,
    val name: String,
    val description: String,
    val points: Int
)

data class EditChoreRequest(
    val user_uid: String,
    val chore_id: String,
    val name: String? = null,
    val description: String? = null,
    val points: Int? = null
)

data class CreateChoreResponse(val success: Boolean, val chore_id: String? = null, val error: String? = null)

data class CompletionResponse(val success: Boolean, val completion_id: String? = null, val error: String? = null)

data class UserCompletion(
    val completion_id: String,
    val chore_id: String,
    val chore_name: String,
    val points_earned: Int,
    val completed_at: String,
    val comments: String?,
    val has_proof: Boolean,
    val proof_filename: String? = null
)

data class UserCompletionsResponse(val completions: List<UserCompletion>)

data class LeaderboardEntry(
    val user_uid: String,
    val total_points: Int,
    val completed_count: Int
)

data class LeaderboardResponse(val leaderboard: List<LeaderboardEntry>)

interface ApiService {
    @POST("create_league.php")
    suspend fun createLeague(@Body body: Map<String, String>): LeagueResponse

    @GET("search_leagues.php")
    suspend fun searchLeagues(@Query("q") query: String): List<League>

    @POST("join_league.php")
    suspend fun joinLeague(@Body body: Map<String, String>): GenericResponse

    @POST("leave_league.php")
    suspend fun leaveLeague(@Body body: Map<String, String>): GenericResponse

    @GET("list_league_members.php")
    suspend fun listLeagueMembers(
        @Query("league_id") leagueId: String,
        @Query("user_uid") userUid: String
    ): LeagueMembersResponse

    @POST("create_chore.php")
    suspend fun createChore(@Body body: CreateChoreRequest): CreateChoreResponse

    @POST("edit_chore.php")
    suspend fun editChore(@Body body: EditChoreRequest): GenericResponse

    @POST("delete_chore.php")
    suspend fun deleteChore(@Body body: Map<String, String>): GenericResponse

    @GET("list_chores.php")
    suspend fun listChores(
        @Query("league_id") leagueId: String,
        @Query("user_uid") userUid: String
    ): List<Chore>

    @Multipart
    @POST("complete_chore.php")
    suspend fun completeChore(
        @Part("user_uid") userUid: RequestBody,
        @Part("league_id") leagueId: RequestBody,
        @Part("chore_id") choreId: RequestBody,
        @Part("comments") comments: RequestBody?,
        @Part proof_image: MultipartBody.Part?
    ): CompletionResponse

    @GET("league_leaderboard.php")
    suspend fun getLeaderboard(
        @Query("league_id") leagueId: String,
        @Query("user_uid") userUid: String
    ): LeaderboardResponse

    @GET("user_completed_chores.php")
    suspend fun getUserCompletions(
        @Query("league_id") leagueId: String,
        @Query("target_uid") targetUid: String,
        @Query("user_uid") requesterUid: String
    ): UserCompletionsResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://mrowberry.com/trashtalk/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
