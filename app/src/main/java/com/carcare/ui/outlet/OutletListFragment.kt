package com.carcare.ui.outlet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.MainActivity
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.databinding.FragmentOutletViewBinding
import com.carcare.ui.checkout.CheckOutViewModel
import com.carcare.ui.outlet.adapter.HomeOutletAdapter
import com.carcare.utils.PreferenceHelper
import com.carcare.viewmodel.response.LocationInfoData
import com.carcare.viewmodel.response.outlets.Outlet

class OutletListFragment : Fragment() {

    val prefsHelper: PreferenceHelper by lazy {
        CarCareApplication.prefs!!
    }
    private lateinit var binding: FragmentOutletViewBinding
    private lateinit var checkOutViewModel: CheckOutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutletViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkOutViewModel = ViewModelProvider(this)[CheckOutViewModel::class.java]
        binding.outletList.layoutManager = LinearLayoutManager(requireActivity())

//        binding.homeAddress.setOnClickListener {
//            val intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?saddr="+(CarCareApplication.instance.locationInfoData.latitude.toString()+","+CarCareApplication.instance.locationInfoData.longitude)+"&daddr="+data.lat.toString()+","+data.long)
//            )
//            startActivity(intent)
//        }

        binding.customerCall.setOnClickListener {
            dialCustomer(getString(R.string.customer_care_number))
        }
        checkOutViewModel.outletsResponse.observe(requireActivity()) { response ->
            if (response != null && response.data.isNotEmpty()) {
                val outletsAdapter = HomeOutletAdapter(response.data, object : HomeOutletAdapter.ItemClickListener {
                    override fun itemClick(outlet: Outlet) {
                        dialCustomer(outlet.mobileNumber)
                    }

                    override fun moveToMap(data: Outlet) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+(CarCareApplication.instance.locationInfoData.latitude.toString()+","+CarCareApplication.instance.locationInfoData.longitude)+"&daddr="+data.lat.toString()+","+data.long)
                        )
                        startActivity(intent)
                    }

                })
                binding.outletList.adapter = outletsAdapter
            }

        }


        checkOutViewModel.isLoading.observe(requireActivity()) { isLoading ->
             (activity as? MainActivity)?.setDialog(isLoading)
        }
        checkOutViewModel.errorMessage.observe(requireActivity()) { errorMessage ->
             (activity as? MainActivity)?.showToast(errorMessage.toString())
        }


        val query = HashMap<String, Any>()
        query["city"] = "CBE"
        checkOutViewModel.fetchOutLets(query)
    }

    fun dialCustomer(num:String){
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
        startActivity(intent)

    }
}