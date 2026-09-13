
package com.example.ivory.data.repository

import com.example.ivory.data.api.ModerationApi
import com.example.ivory.data.remote.dto.ModerationDto
import com.example.ivory.data.remote.dto.ModerationResponseDto
import javax.inject.Inject

class ModerationRepository @Inject constructor(
    private val api: ModerationApi
) {

    suspend fun moderatePost(text: String): ModerationResponseDto {
        return api.classify(
            ModerationDto(text)
        )
    }
}


