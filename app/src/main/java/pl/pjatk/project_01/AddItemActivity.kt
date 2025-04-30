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
import pl.pjatk.project_01.databinding.ActivityAddItemBinding
import pl.pjatk.project_01.model.Category
import pl.pjatk.project_01.model.MediaDto
import pl.pjatk.project_01.model.Status
import pl.pjatk.project_01.repository.AppDatabase
import pl.pjatk.project_01.repository.MediaRepository
import pl.pjatk.project_01.repository.MediaRepositoryImpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AddItemActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddItemBinding
    lateinit var categoryAdapter: ArrayAdapter<Category>
    private lateinit var database: AppDatabase
    private lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaRepository = MediaRepositoryImpl(AppDatabase.open(this).media)

        lifecycleScope.launch {
            createDB()
        }

        setListeners()
        populateData()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "media"
        ).build()
    }

    private fun populateData() {
        val categories = Category.entries.map { it }
        categoryAdapter = ArrayAdapter<Category>(
            this@AddItemActivity,
            android.R.layout.simple_spinner_item,
            categories.toTypedArray()
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.addItemCategory.adapter = categoryAdapter
        binding.addItemImage.setImageBitmap(ImageUtils.bitArrayToBitmap(ImageUtils.iconToBitArray(this@AddItemActivity, R.drawable.ic_add)))
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val resizedBitmap = resizeImage(uri, 24, 24)
            resizedBitmap?.let { bitmap ->
                binding.addItemImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setListeners(){
        binding.addItemDiscard.setOnClickListener {
            finish()
        }

        binding.addItemSave.setOnClickListener {
            val validationMsg = isEverythingFilled()
            if (validationMsg == "ALL_GOOD") {
                val newItem = MediaDto(
                    icon = ImageUtils.imageToBitArray(binding.addItemImage.drawable.toBitmap()),
                    title = binding.addItemTitle.text.toString(),
                    releaseDate = binding.addItemPremierDateInput.text.toString(),
                    category = Category.valueOf(binding.addItemCategory.selectedItem.toString()),
                    status =  Status.NOT_WATCHED,
                    comment = ""
                )

                lifecycleScope.launch {
                    mediaRepository.insert(newItem)
                    finish()
                }
            }
            else{
                binding.addItemEditInfo.text = validationMsg
                binding.addItemEditInfo.isVisible = true
            }
        }

        binding.addItemImage.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    suspend fun createDB() = withContext (Dispatchers.IO) {
        val media = AppDatabase.open(this@AddItemActivity).media
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
        if(binding.addItemImage.drawable == R.drawable.ic_add.toDrawable()) return "Please add image!"
        else if(binding.addItemTitle.text.toString() == "Title" || binding.addItemTitle.text.toString().isEmpty()) return "Please add title!"
        else if (!checkDate(binding.addItemPremierDateInput.text.toString())) return "Please add valid date(dd-mm-yyyy)!"
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


