package pl.pjatk.project_01.repository

import android.annotation.SuppressLint
import pl.pjatk.project_01.R
import pl.pjatk.project_01.model.Category
import pl.pjatk.project_01.model.Media
import pl.pjatk.project_01.model.Status

object MediaRepositoryInMemory : MediaRepository{
    @SuppressLint("ResourceType")
    private val mediaList = mutableListOf<Media>(
        Media(1, R.drawable.koziolek, "Koziolek Matolek", "14-12-1932", Category.MOVIE, Status.WATCHED, "Good movie."),
        Media(2, R.drawable.myszka, "Myszka Miki", "18-11-1928", Category.MOVIE, Status.WATCHED, "I like it."),
        Media(3, R.drawable.reksio, "Reksio", "05-02-1967", Category.MOVIE, Status.NOT_WATCHED, ""),
        Media(4, R.drawable.koziolek, "Koziolek Matolek", "14-12-1932", Category.MOVIE, Status.WATCHED, "Good movie."),
        Media(5, R.drawable.myszka, "Myszka Miki", "18-11-1928", Category.MOVIE, Status.WATCHED, "I like it."),
        Media(6, R.drawable.reksio, "Reksio", "05-02-1967", Category.MOVIE, Status.NOT_WATCHED, ""),
        Media(7, R.drawable.koziolek, "Koziolek Matolek", "14-12-1932", Category.MOVIE, Status.WATCHED, "Good movie."),
        Media(8, R.drawable.myszka, "Myszka Miki", "18-11-1928", Category.MOVIE, Status.WATCHED, "I like it."),
        Media(9, R.drawable.reksio, "Reksio", "05-02-1967", Category.MOVIE, Status.NOT_WATCHED, "")
    )
    override fun getMediaList() : List<Media> = mediaList
}