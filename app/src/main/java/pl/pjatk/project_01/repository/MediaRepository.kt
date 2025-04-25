package pl.pjatk.project_01.repository

import pl.pjatk.project_01.model.Media

interface MediaRepository {
    fun getMediaList(): List<Media>
}