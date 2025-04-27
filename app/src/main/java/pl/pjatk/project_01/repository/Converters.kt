package pl.pjatk.project_01.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromStatus(value: Status): String = value.name

    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)
}
