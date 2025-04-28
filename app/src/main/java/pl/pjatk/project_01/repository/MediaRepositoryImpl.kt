package pl.pjatk.project_01.repository

import pl.pjatk.project_01.model.MediaDao
import pl.pjatk.project_01.model.MediaDto

class MediaRepositoryImpl(private val mediaDao: MediaDao) : MediaRepository {

    override suspend fun getMediaList(): List<MediaDto> {
        return mediaDao.getAll()
    }
}
