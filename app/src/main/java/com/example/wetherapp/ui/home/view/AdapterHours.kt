import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.R
import com.example.wetherapp.databinding.ItemHoursBinding
import com.example.wetherapp.model.forecast.ListElement
import com.example.wetherapp.model.forecast.MainEnum

class WeatherAdapterHours : ListAdapter<ListElement, ViewHolderHours>(WeatherDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHours {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHoursBinding.inflate(inflater, parent, false)
        return ViewHolderHours(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderHours, position: Int) {
        val forecast = getItem(position)


        Log.d("WeatherAdapterHours", "Date: ${forecast.dtTxt}, Temp: ${forecast.main.temp}°C, Status: ${forecast.weather[0].main}")

        holder.binding.tvDateHours.text = forecast.dtTxt
        holder.binding.tvTimeHours.text = forecast.dtTxt.split(" ")[1]
        holder.binding.tvDegreeHours.text = "${forecast.main.temp}°C"

        val weatherStatus = forecast.weather[0].main
        val weatherIcon = when (weatherStatus) {
            MainEnum.Clear -> R.drawable.sun
            MainEnum.Clouds  -> R.drawable.cloud
            MainEnum.Rain -> R.drawable.rain

        }

        holder.binding.ivStatusIconHours.setImageResource(weatherIcon)
    }
}

class ViewHolderHours(val binding: ItemHoursBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherDiffUtil : DiffUtil.ItemCallback<ListElement>() {
    override fun areItemsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
        Log.d("WeatherDiffUtil", "Checking if items are the same: ${oldItem.pop} vs ${newItem.pop}")
        return oldItem.pop == newItem.pop
    }

    override fun areContentsTheSame(oldItem: ListElement, newItem: ListElement): Boolean {
        Log.d("WeatherDiffUtil", "Checking if contents are the same: ${oldItem == newItem}")
        return oldItem == newItem
    }
}
