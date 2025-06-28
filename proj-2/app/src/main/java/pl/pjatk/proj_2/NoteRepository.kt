package pl.pjatk.proj_2

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class NoteRepository(baseUrl: String = "http://10.0.2.2:8080/api/notes/") {

    private val api: NoteApi

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NoteApi::class.java)
    }

    fun getNotes(onResult: (List<Note>) -> Unit, onError: (Throwable) -> Unit) {
        api.getNotes().enqueue(object : retrofit2.Callback<List<Note>> {
            override fun onResponse(
                call: Call<List<Note>>,
                response: retrofit2.Response<List<Note>>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body() ?: emptyList())
                } else {
                    onError(Throwable("Server error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Note>>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun saveNote(note: Note, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        if (note.id == null) {
            onError(Throwable("Creating new notes not supported"))
            return
        }
        api.updateNote(note.id, note).enqueue(object : retrofit2.Callback<Note> {
            override fun onResponse(call: Call<Note>, response: retrofit2.Response<Note>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Throwable("Saving error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Note>, t: Throwable) {
                onError(t)
            }
        })
    }


    fun uploadFile(file: File, onResult: (String?) -> Unit, onError: (Throwable) -> Unit) {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        api.uploadFile(body).enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onError(Throwable("Upload error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun deleteNote(note: Note, onSuccess: () -> Unit = {}, onError: (Throwable) -> Unit = {}) {
        val id = note.id
        if (id == null) {
            onError(Throwable("Brak ID notatki do usunięcia"))
            return
        }

        api.deleteNote(id).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Throwable("Błąd podczas usuwania: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onError(t)
            }
        })
    }

}
