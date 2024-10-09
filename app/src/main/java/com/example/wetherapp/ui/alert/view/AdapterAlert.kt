package com.example.wetherapp.ui.alert.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.databinding.ItemAlertBinding
import com.example.wetherapp.db.AlertPojo



class AdapterAlert(
    private val onDeleteClick: (AlertPojo) -> Unit,
    private val onItemClick: (AlertPojo) -> Unit
) : ListAdapter<AlertPojo, ViewHolderAlert>(WeatherFavDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAlert {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlertBinding.inflate(inflater, parent, false)
        return ViewHolderAlert(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderAlert, position: Int) {
        val alert = getItem(position)
        holder.binding.alarmTime.text=alert.time
        holder.binding.txtCityname.text=alert.cityName
        holder.binding.iconDeleted.setOnClickListener {
            onDeleteClick(alert)
        }
        holder.itemView.setOnClickListener {
            onItemClick(alert)
        }
    }
}


class ViewHolderAlert(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherFavDiffUtil : DiffUtil.ItemCallback<AlertPojo>() {
    override fun areItemsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
        return oldItem == newItem
    }
}