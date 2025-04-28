package pl.pjatk.project_01.repository

import pl.pjatk.project_01.model.MediaDto

interface MediaRepository {
    suspend fun getMediaList(): List<MediaDto>
    suspend fun getById(id: Int): MediaDto?
    suspend fun insert(media: MediaDto): Long?
    suspend fun update(media: MediaDto)
    suspend fun delete(media: MediaDto)
}