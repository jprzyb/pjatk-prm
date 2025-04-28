package pl.pjatk.project_01.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pjatk.project_01.databinding.ItemMediaBinding
import pl.pjatk.project_01.model.MediaDto

class MediaItem(val itemViewBinding: ItemMediaBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
    fun onBind(mediaItem: MediaDto, onItemClicked: (MediaDto) -> Unit) = with(itemViewBinding) {
        title.text = mediaItem.title
        premierDate.text = mediaItem.releaseDate.toString()
        category.text = mediaItem.category.toString()
        status.text = mediaItem.status.toString()
        image.setImageResource(mediaItem.icon)

        root.setOnClickListener {
            onItemClicked(mediaItem)
        }
    }
}

class MediaListAdapter(
    private val onItemClicked: (MediaDto) -> Unit
) : RecyclerView.Adapter<MediaItem>() {

    var mediaList: List<MediaDto> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
        return MediaItem(binding)
    }

    override fun onBindViewHolder(holder: MediaItem, position: Int) {
        holder.onBind(mediaList[position], onItemClicked)
    }

    override fun getItemCount(): Int = mediaList.size
}
