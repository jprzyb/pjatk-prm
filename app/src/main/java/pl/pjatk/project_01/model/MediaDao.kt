package pl.pjatk.project_01.model

import androidx.room.*

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: Media)

    @Update
    suspend fun update(media: Media)

    @Delete
    suspend fun delete(media: Media)

    @Query("SELECT * FROM media")
    suspend fun getAll(): List<Media>

    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getById(id: Int): Media?
}
