package com.august.spiritscribe.showcase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class FetchUserUseCase(
    private val repo: UserDataRepository
) {
    suspend fun fetchUserData(): User = coroutineScope {
        val name = async { repo.getName() }
        val friends = async { repo.getFriends()}
        val profile = async { repo.getProfile() }
        User(
            name = name.await(),
            friends = friends.await(),
            profile = profile.await()
        )
    }
}

interface UserDataRepository {
    suspend fun getName() : String
    suspend fun getFriends() : List<Friend>
    suspend fun getProfile() : Profile
}

data class User(
    val name: String,
    val friends: List<Friend>,
    val profile: Profile
)

data class Friend(
    val id: String,
    val name: String
)

data class Profile(
    val id: String,
    val description: String,
    val profileImageUrl: String,
)