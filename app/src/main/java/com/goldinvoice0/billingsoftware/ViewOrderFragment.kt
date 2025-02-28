package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.goldinvoice0.billingsoftware.ViewModel.SharedNavigationViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentViewOrderBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.io.File


class ViewOrderFragment : Fragment() {


    private var _binding: FragmentViewOrderBinding? = null
    private val binding get() = _binding!!
    lateinit var f: File


    var fromWhichFragment: Boolean = false

    val vm: SharedNavigationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewOrderBinding.inflate(inflater, container, false)

        val bundal = arguments
        val file = bundal!!.getString("file")
//        fromWhichFragment = bundal.getBoolean("FromWhichFragment")
        f = File(file!!)


        PDFBoxResourceLoader.init(binding.root.context)
        binding.pdfViewMF.fromFile(f).load()

        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigate back and request tab 1
                    vm.requestTab(1)
                    // Remove this callback
                    remove()
                    // Actually go back
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        )


        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        vm.requestTab(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}