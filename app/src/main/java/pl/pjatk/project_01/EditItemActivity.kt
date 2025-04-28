package pl.pjatk.project_01

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.pjatk.project_01.databinding.ActivityEditItemBinding
import pl.pjatk.project_01.model.Category
import pl.pjatk.project_01.model.MediaDto
import pl.pjatk.project_01.model.Status
import pl.pjatk.project_01.repository.AppDatabase
import pl.pjatk.project_01.repository.MediaRepository
import pl.pjatk.project_01.repository.MediaRepositoryImpl

class EditItemActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditItemBinding
    lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var database: AppDatabase
    private lateinit var mediaRepository: MediaRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaRepository = MediaRepositoryImpl(AppDatabase.open(this).media)

        binding.editItemDiscard.setOnClickListener {
            finish()
        }

        binding.editItemSave.setOnClickListener {
            val newItem = MediaDto(
                id = 0,
                icon = R.drawable.ic_launcher_foreground,
                title = binding.editItemTitle.text.toString(),
                releaseDate = binding.editItemPremierDateInput.text.toString(),
                category = Category.valueOf(binding.editItemCategory.selectedItem.toString()),
                status = Status.NOT_WATCHED,
                comment = " "
            )

            lifecycleScope.launch {
                mediaRepository.insert(newItem)
            }
            finish()
        }

        val mediaItemId = intent.getLongExtra("mediaItemId", -1)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "media"
        ).build()

        lifecycleScope.launch {
            createDB()
        }

        lifecycleScope.launch {
            val mediaItem = loadMediaItem(mediaItemId)
            if (mediaItem != null) {
                binding.editItemTitle.setText(mediaItem.title)
                binding.editItemPremierDateInput.setText(mediaItem.releaseDate)
//                val categories = Category.values().map { it.name }
//                categoryAdapter = ArrayAdapter(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    categories
//                )
//                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                binding.editItemCategory.adapter = categoryAdapter
//                val categoryName = mediaItem.category.name
//                val position = categoryAdapter.getPosition(categoryName)
//                binding.editItemCategory.setSelection(position)
            }
        }
    }

    private suspend fun loadMediaItem(mediaItemId: Long): MediaDto? {
        return withContext(Dispatchers.IO) {
            mediaRepository.getById(mediaItemId.toInt())
        }
    }

    suspend fun createDB() = withContext (Dispatchers.IO) {
        val media = AppDatabase.open(this@EditItemActivity).media
    }
}