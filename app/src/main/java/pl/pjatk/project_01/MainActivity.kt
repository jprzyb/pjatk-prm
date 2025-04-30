package pl.pjatk.project_01

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.pjatk.project_01.adapter.MediaListAdapter
import pl.pjatk.project_01.databinding.ActivityMainBinding
import pl.pjatk.project_01.model.Category
import pl.pjatk.project_01.model.MediaDto
import pl.pjatk.project_01.model.Status
import pl.pjatk.project_01.repository.AppDatabase
import pl.pjatk.project_01.repository.MediaRepository
import pl.pjatk.project_01.repository.MediaRepositoryImpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaRepository: MediaRepository
    lateinit var mediaListAdapter: MediaListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        deleteDatabase("media")
        setVars()
        setContentView(binding.root)
        setListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        lifecycleScope.launch {
            createDB()
            mediaListAdapter.mediaList = sortMediaListByDate(mediaRepository.getMediaList())
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            mediaListAdapter.mediaList = sortMediaListByDate(mediaRepository.getMediaList())
        }
    }


    suspend fun createDB() = withContext (Dispatchers.IO) {
        val media = AppDatabase.open(this@MainActivity).media
        if(media.getAll().isEmpty()){
            media.insert(MediaDto(1, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.koziolek), "Koziolek Matolek", "14-12-1932", Category.MOVIE, Status.WATCHED, "Good movie."))
            media.insert(MediaDto(2, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.myszka), "Myszka Miki", "18-11-1928", Category.MOVIE, Status.WATCHED, "I like it."))
            media.insert(MediaDto(3, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.reksio), "Reksio", "05-02-1967", Category.MOVIE, Status.NOT_WATCHED, ""))
        }
    }

    fun sortMediaListByDate(mediaList: List<MediaDto>, descending: Boolean = false): List<MediaDto> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        return if (descending) {
            mediaList.sortedByDescending {
                runCatching { LocalDate.parse(it.releaseDate, formatter) }.getOrNull()
            }
        } else {
            mediaList.sortedBy {
                runCatching { LocalDate.parse(it.releaseDate, formatter) }.getOrNull()
            }
        }
    }

    fun setVars(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        mediaRepository = MediaRepositoryImpl(AppDatabase.open(this).media)
        mediaListAdapter = MediaListAdapter(
            onItemClicked = { mediaItem ->
                val intent = Intent(this, EditItemActivity::class.java)
                intent.putExtra("mediaItemId", mediaItem.id.toLong())
                startActivity(intent)
            },
            onItemLongClicked = { mediaItem ->
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete item")
                    .setMessage("Do you want to delete '${mediaItem.title}'?")
                    .setPositiveButton("Yes") { dialog, which ->
                        lifecycleScope.launch {
                            mediaRepository.delete(mediaItem)
                            mediaListAdapter.mediaList = sortMediaListByDate(mediaRepository.getMediaList())
                        }
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )
        binding.mediaList.adapter = mediaListAdapter
        binding.mediaList.layoutManager = LinearLayoutManager(this)
    }


    fun setListeners(){
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }
}
