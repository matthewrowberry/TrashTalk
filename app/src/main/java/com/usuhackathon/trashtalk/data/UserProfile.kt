package com.usuhackathon.trashtalk.data

data class UserProfile(
    val displayName: String = "",
    val email: String = "",
    val leagueID: String = "",
    val points: Long = 0L
)