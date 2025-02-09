package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldinvoice0.billingsoftware.Model.PdfData
import com.goldinvoice0.billingsoftware.Model.Shop
import com.goldinvoice0.billingsoftware.databinding.FragmentItemsBinding
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class ItemsFragment : Fragment() {


    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)


        PDFBoxResourceLoader.init(binding.root.context)

        val pdfData = PdfData(
            "filename", "Rakesh", "9191919191", "Sathin", "12/12/12",
            mutableListOf("Aad", "Aad", "Aad", "Aad", "Aad", "Aad", "Aad", "Aad", "Aad"),
            mutableListOf(
                "12.112",
                "12.112",
                "12.112",
                "12.112",
                "12.112",
                "12.112",
                "12.112",
                "12.112",
                "12.112"
            ),
            mutableListOf(
                "11.121",
                "11.121",
                "11.121",
                "11.121",
                "11.121",
                "11.121",
                "11.121",
                "11.121",
                "11.121"
            ),
            mutableListOf("1200", "1200", "1200", "1200", "1200", "1200", "1200", "1200", "1200"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            mutableListOf("1", "1", "1", "1", "1", "1", "1", "1", "1"),
            "121212"
        )
        val shop = Shop(
            1,
            "Mahalaxmi Jewelllerd",
            "Mahesh",
            "Pipar City",
            "Jodhpur",
            "123123123"
        )


        val file = PdfGenerationClass001().createPdfFromView(
            pdfData,
            requireContext(),
            mutableListOf(),
            100,
            shop,
            "inv121",
            mutableListOf()
        )
        val fileName = file.toString()
        binding.pdfViewMF.fromFile(file).load()
        // Inflate the layout for this fragment
        return binding.root
    }
}