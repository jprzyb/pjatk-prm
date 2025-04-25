package pl.pjatk.project_01.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pjatk.project_01.databinding.ItemMediaBinding
import pl.pjatk.project_01.model.Media

class MediaItem(val itemViewBinding: ItemMediaBinding) : RecyclerView.ViewHolder(itemViewBinding.root){
    fun onBind(mediaItem: Media) = with(itemViewBinding) {
        title.setText(mediaItem.title)
        premierDate.setText(mediaItem.releaseDate.toString())
        category.setText(mediaItem.category.toString())
        status.setText(mediaItem.status.toString())
        image.setImageResource(mediaItem.icon)
    }
}

class MediaListAdapter: RecyclerView.Adapter<MediaItem>() {
    var mediaList: List<Media> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)

        return MediaItem(binding)
    }

    override fun onBindViewHolder(
        holder: MediaItem,
        position: Int
    ) {
        holder.onBind(mediaList[position])
    }

    override fun getItemCount(): Int = mediaList.size
}