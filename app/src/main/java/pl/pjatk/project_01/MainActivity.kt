package pl.pjatk.project_01

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import pl.pjatk.project_01.adapter.MediaListAdapter
import pl.pjatk.project_01.databinding.ActivityMainBinding
import pl.pjatk.project_01.repository.MediaRepository
import pl.pjatk.project_01.repository.MediaRepositoryInMemory

class MainActivity : AppCompatActivity() {
    lateinit var mediaRepository: MediaRepository
    lateinit var binding: ActivityMainBinding
    lateinit var mediaListAdapter: MediaListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaRepository = MediaRepositoryInMemory

        mediaListAdapter = MediaListAdapter()

        binding.mediaList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mediaListAdapter
        }

        mediaListAdapter.mediaList = mediaRepository.getMediaList()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}
