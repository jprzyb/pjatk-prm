package pl.pjatk.project_01.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.pjatk.project_01.model.Converters
import pl.pjatk.project_01.model.MediaDto
import pl.pjatk.project_01.model.MediaDao

@Database(
    entities = [
        MediaDto::class
               ],
    version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val media: MediaDao

    companion object{
        fun open(context: Context) =
            Room.databaseBuilder(
                context, AppDatabase::class.java,
                name = "media"
            )
                .build()
    }


}
