package pl.pjatk.project_01

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    lateinit var categoryAdapter: ArrayAdapter<Category>
    lateinit var statusAdapter: ArrayAdapter<Status>


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
        binding.filterCategory.setSelection(categoryAdapter.getPosition(Category.ALL))
        binding.filterStatus.setSelection(statusAdapter.getPosition(Status.ALL))
    }


    suspend fun createDB() = withContext (Dispatchers.IO) {
        val media = AppDatabase.open(this@MainActivity).media
        if(media.getAll().isEmpty()){
            media.insert(MediaDto(1, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.koziolek), "Koziolek Matolek", "14-12-1932", Category.DOCUMENTARY, Status.WATCHED, "Good movie."))
            media.insert(MediaDto(2, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.myszka), "Myszka Miki", "18-11-1928", Category.MOVIE, Status.WATCHED, "I like it."))
            media.insert(MediaDto(3, ImageUtils.iconToBitArray(this@MainActivity, R.drawable.reksio), "Reksio", "05-02-1967", Category.SERIES, Status.NOT_WATCHED, ""))
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
                if(mediaItem.status == Status.WATCHED){
                    val intent = Intent(this, SeeItemActivity::class.java)
                    intent.putExtra("mediaItemId", mediaItem.id.toLong())
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, EditItemActivity::class.java)
                    intent.putExtra("mediaItemId", mediaItem.id.toLong())
                    startActivity(intent)
                }
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

        val categories = Category.entries.map { it }
        categoryAdapter = ArrayAdapter<Category>(
            this@MainActivity,
            android.R.layout.simple_spinner_item,
            categories.toTypedArray()
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterCategory.adapter = categoryAdapter

        val statuses = Status.entries.map { it }
        statusAdapter = ArrayAdapter<Status>(
            this@MainActivity,
            android.R.layout.simple_spinner_item,
            statuses.toTypedArray()
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterStatus.adapter = statusAdapter

        binding.filterCategory.setSelection(categoryAdapter.getPosition(Category.ALL))
        binding.filterStatus.setSelection(statusAdapter.getPosition(Status.ALL))
    }


    @SuppressLint("SetTextI18n")
    fun setListeners() {
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        val updateFilteredList = {
            val selectedCategory = binding.filterCategory.selectedItem as Category
            val selectedStatus = binding.filterStatus.selectedItem as Status

            lifecycleScope.launch {
                val allMedia = mediaRepository.getMediaList()
                val filteredMedia = allMedia.filter {
                    (selectedCategory == Category.ALL || it.category == selectedCategory) &&
                            (selectedStatus == Status.ALL || it.status == selectedStatus)
                }
                val sortedList = sortMediaListByDate(filteredMedia)
                mediaListAdapter.mediaList = sortedList
                binding.fetchedData.text = "Found: ${sortedList.size}"
            }
        }

        binding.filterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateFilteredList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.filterStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateFilteredList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

}
