package com.carcare.ui.bookingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.databinding.FragmentDashboardBinding
import com.carcare.ui.bookingList.adapter.BookingListAdapter
import com.carcare.ui.home.adapter.ServiceListAdapter

class BookingListFragment : Fragment() {

    private lateinit var _binding: FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]
        _binding.bookingList.layoutManager = LinearLayoutManager(requireActivity())
        val bookingListAdapter = BookingListAdapter(emptyList())
        _binding.bookingList.adapter = bookingListAdapter

    }

}