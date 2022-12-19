package com.fivek.userapp.ui.bookingList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivek.userapp.MainActivity
import com.fivek.userapp.databinding.FragmentDashboardBinding
import com.fivek.userapp.ui.bookingDetails.BookingDetailsActivity
import com.fivek.userapp.ui.bookingList.adapter.BookingListAdapter
import com.fivek.userapp.utils.Constants
import com.fivek.userapp.viewmodel.response.bookingList.Bookings

class BookingListFragment : Fragment() {

    private lateinit var _binding: FragmentDashboardBinding
    private lateinit var bookingViewModel: BookingViewModel

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
        bookingViewModel = ViewModelProvider(this)[BookingViewModel::class.java]
        _binding.bookingList.layoutManager = LinearLayoutManager(requireActivity())

        _binding.bookingNowBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        bookingViewModel.bookingListResponse.observe(requireActivity()) { response ->

            if (response != null && response.data.bookings.isNotEmpty()) {
                _binding.bookingList.visibility = View.VISIBLE
                _binding.noDataFound.visibility = View.GONE
                val bookingListAdapter = BookingListAdapter(response.data.bookings, object :
                    BookingListAdapter.ItemClickListener{
                    override fun itemClick(data: Bookings) {
                        val intent = Intent(requireActivity(), BookingDetailsActivity::class.java)
                        intent.putExtra(Constants.BOOKING_ID, data.id)
                        startActivity(intent)

                    }

                })
                bookingListAdapter.updateService(response.data.services)
                _binding.bookingList.adapter = bookingListAdapter
            } else {
                _binding.bookingList.visibility = View.GONE
                _binding.noDataFound.visibility = View.VISIBLE
            }

        }


        bookingViewModel.isLoading.observe(requireActivity()) { isLoading ->
            (activity as? MainActivity)?.setDialog(isLoading)
        }
        bookingViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
            (activity as? MainActivity)?.showToast(errorMessage.toString())
        }

        bookingViewModel.getBookingList()

    }

}