package pl.pjatk.project_01.repository

import pl.pjatk.project_01.model.MediaDto

interface MediaRepository {
    suspend fun getMediaList(): List<MediaDto>
}