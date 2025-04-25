package pl.pjatk.project_01.model

import androidx.annotation.DrawableRes

data class Media(
    @DrawableRes
    val id: Int,
    val icon: Int,
    val title: String,
    val releaseDate: String,
    val category: Category,
    var status: Status = Status.NOT_WATCHED,
    var comment: String = ""
)
