package com.goldinvoice0.billingsoftware

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.goldinvoice0.billingsoftware.ViewModel.ShopViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentDashboardBinding
import com.goldinvoice0.billingsoftware.databinding.FragmentSettingBinding


class Setting : Fragment() {


    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val shopViewModel : ShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        shopViewModel.shop.observe(viewLifecycleOwner){shop->

        }

        // Inflate the layout for this fragment
        return binding.root
    }


}