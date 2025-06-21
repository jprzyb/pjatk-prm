package pl.pjatk.project_01.model

import androidx.room.*

@Dao
interface MediaDao {
    @Query("SELECT * FROM media")
    suspend fun getAll(): List<MediaDto>

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getById(id: Int): MediaDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaDto): Long?

    @Update
    suspend fun update(media: MediaDto)

    @Delete
    suspend fun delete(media: MediaDto)
}
