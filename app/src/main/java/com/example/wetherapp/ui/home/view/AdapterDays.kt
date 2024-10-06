package com.example.wetherapp.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.databinding.ItemDaysBinding
import com.example.wetherapp.model.forecast.ListElement
import com.example.wetherapp.R
import com.example.wetherapp.model.forecast.MainEnum

class WeatherAdapterDays : ListAdapter<ListElement, ViewHolderDays>(WeatherDaysDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDays {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDaysBinding.inflate(inflater, parent, false)
        return ViewHolderDays(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderDays, position: Int) {
        val forecastDays = getItem(position)

        holder.binding.tvDay.text = forecastDays.dtTxt
        holder.binding.tvDegreeDays.text = "${forecastDays.main.temp}°C"
        holder.binding.tvStatusDays.text = forecastDays.weather[0].description


        val weatherStatus = forecastDays.weather[0].main
        val weatherIcon = when (weatherStatus) {
            MainEnum.Clear -> R.drawable.sun
            MainEnum.Clouds  -> R.drawable.cloud
            MainEnum.Rain -> R.drawable.rain

        }
        holder.binding.ivIconDays.setImageResource(weatherIcon)
    }
}

class ViewHolderDays(val binding: ItemDaysBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherDaysDiffUtil : DiffUtil.ItemCallback<ListElement>() {
    override fun areItemsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
        return oldItem == newItem
    }
}
