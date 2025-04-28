package pl.pjatk.project_01

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.pjatk.project_01.databinding.ActivityEditItemBinding

class EditItemActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editItemDiscard.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.editItemSave.setOnClickListener {
            val resultIntent = Intent().apply {

            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }


    }
}