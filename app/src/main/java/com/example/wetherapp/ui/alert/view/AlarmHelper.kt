package com.example.wetherapp.ui.alert.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.wetherapp.R
import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {

    var temp: Int = 0
    var latitude: Double? = null
    var longitude: Double? = null
    var cityName: String = ""
    var time :Long?=null

    companion object {
        const val CHANNEL_ID = "WeatherAlertChannel"
        const val NOTIFICATION_ID = 200
        const val ACTION_DISMISS = "com.example.weatherwise.DISMISS_ALERT"
        private var ringtone: Ringtone? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        latitude = intent.getDoubleExtra("lat", 0.0)
        longitude = intent.getDoubleExtra("long", 0.0)
        cityName = intent.getStringExtra("cityName") ?: ""

        time = intent.getLongExtra("alarmTime",0)
        Log.d("TAGonreseve", "onReceivebefor : $longitude $latitude")
        if (intent.action == ACTION_DISMISS) {
            dismissAlert(context)
        } else {
            if (latitude != null && longitude != null) {
                Log.d("TAGonreseve", "onReceive after: $longitude $latitude")


                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val repo =RepoImplementation.getInstance(
                            ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                            LocalDaoImplementation(LocalDatabase.getInstance(context))
                        )

                        val response = repo.getCurrentWeather(latitude!!, longitude!!, "en", "metric")




                        response.collect { weatherData ->
                            withContext(Dispatchers.Main) {
                                temp = weatherData.main.temp.toInt()
                                cityName = getAddress(context, latitude!!, longitude!!)
                                showNotification(context, cityName)
                                repo.deleteAlert(AlertPojo(time?.toInt()?:0,cityName,time?.toString()?:" ",latitude?:0.0,longitude?:0.0) )

                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Failed to fetch weather data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotification(context: Context, cityName: String) {
        Log.d("TAGonreseve", "showNotification : ")
        val channelId = CHANNEL_ID
        createNotificationChannel(context, channelId)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            time?.toInt() ?:0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle("Weather Alert for $cityName")
            .setContentText("The temp is $temp°C")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss",
                dismissPendingIntent
            )
            .setAutoCancel(true)
            .setSound(null)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        Log.d("TAGonreseve", "notifiyed")
        playSound(context, soundUri)
    }

    private fun playSound(context: Context, soundUri: android.net.Uri) {
        ringtone = RingtoneManager.getRingtone(context, soundUri)
        (context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.let { audioManager ->
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                ringtone?.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                ringtone?.play()
            }
        }
    }

    private fun dismissAlert(context: Context) {
        ringtone?.stop()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alerts"
            val descriptionText = "Channel for weather alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getAddress(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context)
        val address = geocoder.getFromLocation(latitude, longitude, 1)?.get(0)?.getAddressLine(0)
        return address ?: "Unknown city"
    }
}