import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.R
import com.example.wetherapp.databinding.ItemHoursBinding



class WeatherAdapterHours : ListAdapter<HoursPojo, ViewHolderHours>(WeatherHoursDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHours {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHoursBinding.inflate(inflater, parent, false)
        return ViewHolderHours(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderHours, position: Int) {
        val forecastHour = getItem(position)

        Log.d("WeatherAdapterHours", "Day: ${forecastHour.day}, Time: ${forecastHour.hours}, Temp: ${forecastHour.degree}")

        holder.binding.tvTimeHours.text = forecastHour.hours
        holder.binding.tvDegreeHours.text = forecastHour.degree


        val weatherIcon = when (forecastHour.thum) {
            "01d" -> R.drawable.sun
            "02d", "03d", "04d" -> R.drawable.cloud
            "09d", "10d" -> R.drawable.rain
            else -> R.drawable.sun
        }

        holder.binding.ivStatusIconHours.setImageResource(weatherIcon)
    }
}

class ViewHolderHours(val binding: ItemHoursBinding) : RecyclerView.ViewHolder(binding.root)

class WeatherHoursDiffUtil : DiffUtil.ItemCallback<HoursPojo>() {
    override fun areItemsTheSame(oldItem: HoursPojo, newItem: HoursPojo): Boolean {
        return oldItem.hours == newItem.hours
    }

    override fun areContentsTheSame(oldItem: HoursPojo, newItem: HoursPojo): Boolean {
        return oldItem == newItem
    }
}
