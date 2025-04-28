package pl.pjatk.project_01.repository

import pl.pjatk.project_01.model.MediaDao
import pl.pjatk.project_01.model.MediaDto

class MediaRepositoryImpl(private val mediaDao: MediaDao) : MediaRepository {

    override suspend fun getMediaList(): List<MediaDto> {
        return mediaDao.getAll()
    }

    override suspend fun getById(id: Int): MediaDto? {
        return mediaDao.getById(id)
    }

    override suspend fun insert(media: MediaDto): Long? {
        return mediaDao.insert(media)
    }

    override suspend fun update(media: MediaDto) {
        mediaDao.update(media)
    }

    override suspend fun delete(media: MediaDto) {
        mediaDao.delete(media)
    }
}
