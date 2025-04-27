package pl.pjatk.project_01.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.DrawableRes

@Entity(tableName = "media")
data class Media(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @DrawableRes
    val icon: Int,
    val title: String,
    val releaseDate: String,
    val category: Category,
    var status: Status = Status.NOT_WATCHED,
    var comment: String = ""
)
