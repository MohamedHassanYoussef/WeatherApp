package com.example.wetherapp.ui.alert.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wetherapp.R
import com.example.wetherapp.databinding.FragmentNotificationsBinding
import com.example.wetherapp.db.LocalDaoImplementation
import com.example.wetherapp.db.LocalDatabase
import com.example.wetherapp.model.RepoImplementation
import com.example.wetherapp.network.ImplementNetworkResponse
import com.example.wetherapp.network.RetrofitHelper
import com.example.wetherapp.ui.alert.viewmodel.AlertFactory
import com.example.wetherapp.ui.alert.viewmodel.AlertViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadioButtonListener()
    }



    private fun setupRadioButtonListener() {
        binding.floatingActionAlarm.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.alarmMap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}