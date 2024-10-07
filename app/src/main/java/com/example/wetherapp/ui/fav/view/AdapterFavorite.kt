package com.example.wetherapp.ui.fav.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.databinding.ItemFavouriteBinding
import com.example.wetherapp.db.PlaceFavPojo


class AdapterFavorite(
    private val onDeleteClick: (PlaceFavPojo) -> Unit,
    private val onItemClick: (PlaceFavPojo) -> Unit
) : ListAdapter<PlaceFavPojo, ViewHolderFavorite>(WeatherFavDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFavorite {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return ViewHolderFavorite(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderFavorite, position: Int) {
        val favPlace = getItem(position)
        holder.binding.textView.text = favPlace.cityName

        holder.itemView.setOnClickListener {
            onItemClick(favPlace)
        }
        holder.binding.deleteIcon.setOnClickListener {
            onDeleteClick(favPlace)
        }
    }
}



class ViewHolderFavorite(val binding: ItemFavouriteBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherFavDiffUtil : DiffUtil.ItemCallback<PlaceFavPojo>() {
    override fun areItemsTheSame(oldItem: PlaceFavPojo, newItem: PlaceFavPojo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceFavPojo, newItem: PlaceFavPojo): Boolean {
        return oldItem == newItem
    }
}