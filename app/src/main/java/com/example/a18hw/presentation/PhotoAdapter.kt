package com.example.a18hw.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a18hw.R
import com.example.a18hw.databinding.RecyclerPhotoItemBinding
import com.example.a18hw.entity.Photo

class PhotoAdapter : ListAdapter<Photo, PhotoViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = RecyclerPhotoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = getItem(position)
        val res = holder.itemView.resources
        val date = item?.date?.substring(0, 10)?.replace("-", "/")
        val exact = item?.date?.substring(11, item.date.length)?.replace("-", ":")
        holder.binding.dateTextViewTop.text = res.getString(R.string.photo_date, date)
        holder.binding.dateTextViewBottom.text = res.getString(R.string.photo_date, exact)
        item?.let {
            Glide.with(holder.binding.photoImageView)
                .load(Uri.parse(it.filepath))
                .into(holder.binding.photoImageView)
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem.filepath == newItem.filepath
}

class PhotoViewHolder(val binding: RecyclerPhotoItemBinding) : RecyclerView.ViewHolder(binding.root)