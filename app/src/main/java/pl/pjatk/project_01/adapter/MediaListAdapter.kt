package pl.pjatk.project_01.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pjatk.project_01.databinding.ItemMediaBinding
import pl.pjatk.project_01.model.MediaDto

class MediaItem(val itemViewBinding: ItemMediaBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
    fun onBind(
        mediaItem: MediaDto,
        onItemClicked: (MediaDto) -> Unit,
        onItemLongClicked: (MediaDto) -> Unit
    ) = with(itemViewBinding) {
        title.text = mediaItem.title
        premierDate.text = mediaItem.releaseDate.toString()
        category.text = mediaItem.category.toString()
        status.text = mediaItem.status.toString()
        image.setImageBitmap(BitmapFactory.decodeByteArray(mediaItem.icon, 0, mediaItem.icon.size))

        root.setOnClickListener {
            onItemClicked(mediaItem)
        }

        root.setOnLongClickListener {
            onItemLongClicked(mediaItem)
            true
        }
    }
}


class MediaListAdapter(
    private val onItemClicked: (MediaDto) -> Unit,
    private val onItemLongClicked: (MediaDto) -> Unit
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
        holder.onBind(mediaList[position], onItemClicked, onItemLongClicked)
    }

    override fun getItemCount(): Int = mediaList.size
}


