package com.example.wetherapp.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.databinding.ItemDaysBinding
import com.example.wetherapp.model.forecast.DaysPojo
import com.example.wetherapp.R

class WeatherAdapterDays : ListAdapter<DaysPojo, ViewHolderDays>(WeatherDaysDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDays {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDaysBinding.inflate(inflater, parent, false)
        return ViewHolderDays(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderDays, position: Int) {
        val forecastDay = getItem(position)


        holder.binding.tvDay.text = forecastDay.day
        holder.binding.tvDegreeDays.text = forecastDay.degree
        holder.binding.tvStatusDays.text = forecastDay.description


        val weatherIcon = when (forecastDay.thum) {
            "01d" -> R.drawable.sun
            "02d", "03d", "04d" -> R.drawable.cloud
            "09d", "10d" -> R.drawable.rain
            else->R.drawable.sun
        }

        holder.binding.ivIconDays.setImageResource(weatherIcon)
    }
}

class ViewHolderDays(val binding: ItemDaysBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherDaysDiffUtil : DiffUtil.ItemCallback<DaysPojo>() {
    override fun areItemsTheSame(oldItem: DaysPojo, newItem: DaysPojo): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: DaysPojo, newItem: DaysPojo): Boolean {
        return oldItem == newItem
    }
}
