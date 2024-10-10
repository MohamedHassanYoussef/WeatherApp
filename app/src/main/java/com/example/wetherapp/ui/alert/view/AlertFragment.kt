package com.example.wetherapp.ui.alert.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentNotificationsBinding
import com.example.wetherapp.db.AlertPojo
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.alert.viewmodel.AlertFactory
import com.example.wetherapp.ui.alert.viewmodel.AlertViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val alertViewModel: AlertViewModel by viewModels {
        AlertFactory(
            RepoImplementation.getInstance(
                ImplementNetworkResponse.getInstance(RetrofitHelper.retrofit),
                LocalDaoImplementation(LocalDatabase.getInstance(requireContext()))
            )
        )
    }

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var cityName: String? = null

    private lateinit var adapter1: AdapterAlert

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter
        adapter1 = AdapterAlert(
            onDeleteClick = { alertPojo ->
                // Handle delete click
                lifecycleScope.launch(Dispatchers.IO) {
                    alertViewModel.deleteAlert(alertPojo)
                    cancelAlarm(requireContext(),alertPojo.id)
                }
            },
            onItemClick = { alertPojo ->
                Toast.makeText(
                    requireContext(),
                    "Clicked on ${alertPojo.cityName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )



        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapter1
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioButtonListener()

        latitude = arguments?.getDouble("latitude")
        longitude = arguments?.getDouble("longitude")
        cityName = arguments?.getString("cityName")
        val time = arguments?.getLong("alarmTime")
        Log.d("AlertFragment", "Location: $latitude, $longitude, City: $cityName   $time"   )




        lifecycleScope.launch {
            alertViewModel.alerts.collect { alerts ->
                adapter1.submitList(alerts)
            }
        }
    }

    private fun setupRadioButtonListener() {
        binding.floatingActionAlarm.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.alarmMap)
        }
    }

    @SuppressLint("ScheduleExactAlarm", "ServiceCast")
    private fun scheduleAlarm(selectedDateTime: Calendar) {
        val intent = Intent(requireContext(), AlarmHelper::class.java)
        intent.putExtra("lat", latitude)
        intent.putExtra("long", longitude)
        intent.putExtra("cityName", cityName)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            selectedDateTime.timeInMillis,
            pendingIntent
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val result =
                AlertPojo(selectedDateTime.timeInMillis.toInt(), cityName!!, selectedDateTime.time.toString(), latitude!!, longitude!!)
            alertViewModel.insertAlert(result)

            Log.d("TAG2", "scheduleAlarm:$result ")


        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val intent = Intent(context, AlarmHelper::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        alarmManager.cancel(pendingIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
