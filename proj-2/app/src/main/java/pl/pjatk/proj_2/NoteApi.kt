package pl.pjatk.proj_2

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface NoteApi {

    @GET(".")
    fun getNotes(): Call<List<Note>>

    @GET("{id}")
    fun getNote(@Path("id") id: String): Call<Note>

    @POST("{id}")
    fun updateNote(@Path("id") id: String, @Body note: Note): Call<Note>

    @DELETE("{id}")
    fun deleteNote(@Path("id") id: String): Call<Void>

    @Multipart
    @POST("upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<String>
}