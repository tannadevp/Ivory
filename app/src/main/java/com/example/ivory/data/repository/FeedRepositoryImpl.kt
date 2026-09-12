package com.example.ivory.data.repository

import com.example.ivory.data.api.FeedApiService
import com.example.ivory.data.mapper.toDomain
import com.example.ivory.data.remote.dto.ModerationDto
import com.example.ivory.data.remote.dto.ModerationResponseDto
import com.example.ivory.domain.model.Post
import com.example.ivory.domain.repository.FeedRepository
import java.io.IOException

class FeedRepositoryImpl(
    private val api: FeedApiService
) : FeedRepository {

    override suspend fun getFeed(
        page: Int,
        limit: Int
    ): Result<List<Post>> {

        return try {
            val response = api.getFeed(page, limit)

            if (response.isSuccessful) {
                val posts = response.body()
                    ?.posts
                    ?.map { it.toDomain() }
                    ?: emptyList()

                Result.success(posts)
            } else {
                Result.failure(Exception("Failed to load feed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moderatePost(content: String): Result<ModerationResponseDto> {
        return try {
            val response = api.moderatePost(ModerationDto(content = content))

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Moderation check failed: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Something went wrong"))
        }
    }
}
