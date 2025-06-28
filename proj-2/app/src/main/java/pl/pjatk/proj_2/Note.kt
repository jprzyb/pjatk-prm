package pl.pjatk.proj_2

data class Note(
    val id: String = "",
    val text: String = "",
    val location: String = "",
    val photoUrl: String? = null,
    val audioUrl: String? = null
)
