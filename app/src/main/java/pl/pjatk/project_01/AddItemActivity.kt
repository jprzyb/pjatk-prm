package pl.pjatk.project_01


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
                binding.addItemImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setListeners(){
        binding.addItemDiscard.setOnClickListener {
            finish()
        }

        binding.addItemSave.setOnClickListener {
            if (isEverythingFilled()) {
                val updatedItem = MediaDto(
//                    icon = binding.addItemImage.drawable,
                    title = binding.addItemTitle.text.toString(),
                    releaseDate = binding.addItemPremierDateInput.text.toString(),
                    category = Category.valueOf(binding.addItemCategory.selectedItem.toString()),
                    status =  Status.NOT_WATCHED,
                    comment = ""
                )

                lifecycleScope.launch {
                    mediaRepository.insert(updatedItem)
                    finish()
                }
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

    fun drawableToIcon(drawable: Drawable): Icon? {
        return if (drawable is BitmapDrawable) {
            Icon.createWithBitmap(drawable.bitmap)
        } else {
            null
        }
    }

    private fun AddItemActivity.isEverythingFilled(): Boolean {
        return true

    }

}


