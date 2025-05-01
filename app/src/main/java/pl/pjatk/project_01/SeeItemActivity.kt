package pl.pjatk.project_01

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.pjatk.project_01.databinding.ActivitySeeItemBinding
import pl.pjatk.project_01.model.MediaDto
import pl.pjatk.project_01.repository.AppDatabase
import pl.pjatk.project_01.repository.MediaRepository
import pl.pjatk.project_01.repository.MediaRepositoryImpl

class SeeItemActivity: AppCompatActivity() {
    lateinit var binding: ActivitySeeItemBinding
    private lateinit var database: AppDatabase
    private lateinit var mediaRepository: MediaRepository
    private var mediaItemToSee: MediaDto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeeItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaRepository = MediaRepositoryImpl(AppDatabase.Companion.open(this).media)

        lifecycleScope.launch {
            createDB()
            val id = intent.getLongExtra("mediaItemId", -1)
            if (id != -1L) {
                val mediaItem = loadMediaItem(id)
                mediaItem?.let {
                    mediaItemToSee = it
                    populateData(it)
                }
            }
        }

        setListeners()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "media"
        ).build()
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val resizedBitmap = resizeImage(uri, 120, 120)
            resizedBitmap?.let { bitmap ->
                binding.seeItemImage.setImageBitmap(bitmap)
            }
        }
    }


    private fun populateData(mediaItem: MediaDto) {
        binding.seeItemImage.setImageBitmap(ImageUtils.bitArrayToBitmap(mediaItem.icon))
        binding.seeItemTitle.setText(mediaItem.title)
        binding.seeItemPremierDateInput.setText(mediaItem.releaseDate)
        binding.seeItemCategory.text = mediaItem.category.toString()
        binding.seeItemStatus.text = mediaItem.status.toString()
        binding.seeItemComment.setText(mediaItem.comment)
        disableAll()
    }

    private fun disableAll() {
        binding.seeItemImage.isEnabled = false
        binding.seeItemTitle.isEnabled = false
        binding.seeItemPremierDateInput.isEnabled = false
        binding.seeItemCategory.isEnabled = false
        binding.seeItemStatus.isEnabled = false
        binding.seeItemComment.isEnabled = false
    }

    private fun setListeners(){
        binding.seeItemBack.setOnClickListener {
            finish()
        }

        binding.seeItemImage.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private suspend fun loadMediaItem(mediaItemId: Long): MediaDto? {
        return withContext(Dispatchers.IO) {
            mediaRepository.getById(mediaItemId.toInt())
        }
    }

    suspend fun createDB() = withContext(Dispatchers.IO) {
        val media = AppDatabase.Companion.open(this@SeeItemActivity).media
    }

    fun resizeImage(uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        val inputStream = contentResolver.openInputStream(uri)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)

        val scaleFactor = Math.min(options.outWidth / maxWidth, options.outHeight / maxHeight)

        inputStream?.close()
        val inputStream2 = contentResolver.openInputStream(uri)

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        return BitmapFactory.decodeStream(inputStream2, null, options)
    }
}