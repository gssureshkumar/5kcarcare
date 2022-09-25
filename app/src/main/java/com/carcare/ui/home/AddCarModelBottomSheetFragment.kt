package com.carcare.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcare.R
import com.carcare.app.CarCareApplication
import com.carcare.database.VehicleModel
import com.carcare.databinding.AddVehicleViewBinding
import com.carcare.ui.home.adapter.VehicleModelAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.*

class AddCarModelBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var itemClickListener: ItemClickListener
    lateinit var binding: AddVehicleViewBinding
    var carModel: String = ""

    companion object {
        fun newInstance(itemListener: ItemClickListener): AddCarModelBottomSheetFragment =
            AddCarModelBottomSheetFragment().apply {
                itemClickListener = itemListener
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddVehicleViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectVehicleType.setOnClickListener {
            dismiss()
        }

        binding.hatchIcon.setOnClickListener {
            carModel = getString(R.string.hatch_back)
            binding.hatchIcon.setBackgroundResource(R.drawable.draw_blue_circle_corner_view)
            binding.sedanIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.suvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.muvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
        }

        binding.sedanIcon.setOnClickListener {
            carModel = getString(R.string.sedan)
            binding.sedanIcon.setBackgroundResource(R.drawable.draw_blue_circle_corner_view)
            binding.hatchIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.suvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.muvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
        }

        binding.suvIcon.setOnClickListener {
            carModel = getString(R.string.suv)
            binding.suvIcon.setBackgroundResource(R.drawable.draw_blue_circle_corner_view)
            binding.sedanIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.hatchIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.muvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
        }

        binding.muvIcon.setOnClickListener {
            carModel = getString(R.string.muv)
            binding.muvIcon.setBackgroundResource(R.drawable.draw_blue_circle_corner_view)
            binding.sedanIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.suvIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
            binding.hatchIcon.setBackgroundResource(R.drawable.draw_white_circle_gray_corner_view)
        }

        binding.hatchIcon.performClick()

        binding.btnSave.setOnClickListener {

            if (binding.carModelTxt.text.toString().isEmpty()) {
                showToast(getString(R.string.please_enter_car_model))
            } else if (binding.registrationEdt.text.toString().isEmpty()) {
                showToast(getString(R.string.please_enter_registration_number))
            } else {

                val vehicle = VehicleModel(Calendar.getInstance().timeInMillis, carModel, true, binding.carModelTxt.text.toString(), binding.registrationEdt.text.toString())
                CarCareApplication.instance.applicationScope.launch {
                    CarCareApplication.instance.repository.insert(vehicle)
                }
                dismiss()
            }

        }
        binding.progressBar.visibility = View.VISIBLE
        binding.mainContainer.visibility = View.GONE

        binding.addVehicleType.setOnClickListener {
            binding.vehicleContainer.visibility = View.VISIBLE
            binding.vehicleListContainer.visibility = View.GONE
        }

        CarCareApplication.instance.repository.allVehicles.observe(this) { vehiles ->
            binding.progressBar.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            vehiles.let {
                if (it.isNotEmpty()) {
                    binding.selectVehicleType.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_close_icon,0)
                    binding.vehicleContainer.visibility = View.GONE
                    binding.vehicleListContainer.visibility = View.VISIBLE
                    val adapter = VehicleModelAdapter(it)
                    binding.vehicleList.adapter = adapter
                    binding.vehicleList.layoutManager = LinearLayoutManager(requireActivity())
                } else {
                    binding.selectVehicleType.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
                    binding.vehicleContainer.visibility = View.VISIBLE
                    binding.vehicleListContainer.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    interface ItemClickListener {
        fun onSubmitClick(userName: String)
    }

    fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}