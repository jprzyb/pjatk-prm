package pl.pjatk.project_01.model

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@SuppressLint("SupportAnnotationUsage")
@Entity(tableName = "media")
data class MediaDto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @DrawableRes
    val icon:  ByteArray,
    val title: String,
    val releaseDate: String,
    val category: Category,
    var status: Status = Status.NOT_WATCHED,
    var comment: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaDto

        if (id != other.id) return false
        if (!icon.contentEquals(other.icon)) return false
        if (title != other.title) return false
        if (releaseDate != other.releaseDate) return false
        if (category != other.category) return false
        if (status != other.status) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + icon.contentHashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + comment.hashCode()
        return result
    }
}
