package pl.pjatk.project_01

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EditItemActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditItemBinding
    lateinit var categoryAdapter: ArrayAdapter<Category>
    private lateinit var database: AppDatabase
    private lateinit var mediaRepository: MediaRepository
    private var mediaItemToEdit: MediaDto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaRepository = MediaRepositoryImpl(AppDatabase.open(this).media)

        lifecycleScope.launch {
            createDB()
            val id = intent.getLongExtra("mediaItemId", -1)
            if (id != -1L) {
                val mediaItem = loadMediaItem(id)
                mediaItem?.let {
                    mediaItemToEdit = it
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
                binding.editItemImage.setImageBitmap(bitmap)
            }
        }
    }


    private fun populateData(mediaItem: MediaDto) {
        binding.editItemTitle.setText(mediaItem.title)
        binding.editItemImage.setImageBitmap(ImageUtils.bitArrayToBitmap(mediaItem.icon))
        binding.editItemPremierDateInput.setText(mediaItem.releaseDate)
        val categories = Category.entries.map { it }
        categoryAdapter = ArrayAdapter<Category>(
            this@EditItemActivity,
            android.R.layout.simple_spinner_item,
            categories.toTypedArray()
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editItemCategory.adapter = categoryAdapter
        val categoryName = mediaItem.category
        val position = categoryAdapter.getPosition(categoryName)
        binding.editItemCategory.setSelection(position)
        binding.editItemComment.setText(mediaItem.comment)

        if(mediaItem.status == Status.WATCHED) {
            binding.editItemStatus.isChecked = true
            binding.editItemComment.isEnabled = true
        }
        else {
            binding.editItemStatus.isChecked = false
            binding.editItemComment.isEnabled = false
        }

        binding.editItemStatus.setOnCheckedChangeListener { _, isChecked ->
            binding.editItemComment.isEnabled = isChecked
        }
        if(mediaItem.status == Status.WATCHED) disableAll()
    }

    private fun disableAll() {
        binding.editItemImage.isEnabled = false
        binding.editItemTitle.isEnabled = false
        binding.editItemPremierDateInput.isEnabled = false
        binding.editItemCategory.isEnabled = false
        binding.editItemStatus.isEnabled = false
        binding.editItemComment.isEnabled = false
        binding.editItemSave.isEnabled = false
        binding.editItemEditInfo.isVisible = true
    }

    private fun setListeners(){
        binding.editItemDiscard.setOnClickListener {
            finish()
        }

        binding.editItemSave.setOnClickListener {
            val existingItem = mediaItemToEdit
            val validationMsg = isEverythingFilled()
            if (validationMsg == "ALL_GOOD" && existingItem != null) {
                val updatedItem = existingItem.copy(
                    icon = ImageUtils.imageToBitArray(binding.editItemImage.drawable.toBitmap()),
                    title = binding.editItemTitle.text.toString(),
                    releaseDate = binding.editItemPremierDateInput.text.toString(),
                    category = Category.valueOf(binding.editItemCategory.selectedItem.toString()),
                    status = if (binding.editItemStatus.isChecked) Status.WATCHED else Status.NOT_WATCHED,
                    comment = binding.editItemComment.text.toString()
                )

                lifecycleScope.launch {
                    mediaRepository.update(updatedItem)
                    finish()
                }
            }
            else{
                binding.editItemEditInfo.text = validationMsg
                binding.editItemEditInfo.isVisible = true
            }
        }

        binding.editItemImage.setOnClickListener {
            pickImage.launch("image/*")
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

    private fun isEverythingFilled(): String {
        if(binding.editItemImage.drawable == R.drawable.ic_add.toDrawable()) return "Please add image!"
        else if(binding.editItemTitle.text.toString() == "Title" || binding.editItemTitle.text.toString().isEmpty()) return "Please add title!"
        else if (!checkDate(binding.editItemPremierDateInput.text.toString())) return "Please add valid date(dd-mm-yyyy)!"
        return "ALL_GOOD"
    }

}

private fun checkDate(string: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return try {
        val inputDate = LocalDate.parse(string, formatter)

        val today = LocalDate.now()
        inputDate.isBefore(today) || inputDate.isEqual(today)
    } catch (e: DateTimeParseException) {
        false
    }

}