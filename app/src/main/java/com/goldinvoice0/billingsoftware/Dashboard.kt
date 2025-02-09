package com.goldinvoice0.billingsoftware

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.goldinvoice0.billingsoftware.Model.PdfFinalData
import com.goldinvoice0.billingsoftware.ViewModel.PdfFinalDataViewModel
import com.goldinvoice0.billingsoftware.databinding.FragmentDashboardBinding


class Dashboard : Fragment() {


    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val pdfFinalDataViewModel: PdfFinalDataViewModel by viewModels()
    var totalNtWt: Double = 0.0
    var totalAmount: Int = 0
    var totalPcs: Int = 0
    var totalPaymentDue: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        pdfFinalDataViewModel.getAllPdfFinalData {
            val s: List<PdfFinalData> = it


            // Calculate the sum of all ntWtList values
            totalNtWt = s.flatMap { pdf ->
                pdf.ntWtList.mapNotNull { ntWt ->
                    ntWt.toDoubleOrNull() // Convert to Double, ignoring invalid entries
                }
            }.sum()
            val totalNtWtText = String.format("%.2f", totalNtWt) + " grams"
            binding.totalGoldWeight.text = totalNtWtText
            // Calculate the sum of all totalList values
            totalAmount = s.flatMap { pdf ->
                pdf.totalList.mapNotNull { total ->
                    total.toIntOrNull() // Convert to Double, ignoring invalid entries
                }
            }.sum()
            binding.totalAnnualSales.text = "₹$totalAmount"
            totalPcs = s.flatMap { pdf ->
                pdf.pcsList.mapNotNull { total ->
                    total.toIntOrNull() // Convert to Double, ignoring invalid entries
                }
            }.sum()
            binding.totalItemsSold.text = "$totalPcs pieces"
            // Calculate the average of goldPriceList values
            val goldPrices = s.flatMap { pdf ->
                pdf.goldPriceList.mapNotNull { goldPrice ->
                    goldPrice.toDoubleOrNull() // Convert to Double, ignoring invalid entries
                }
            }

            val averageGoldPrice = if (goldPrices.isNotEmpty()) {
                goldPrices.sum() / goldPrices.size
            } else {
                0.0 // Handle the case where the list is empty
            }
            binding.avarageRate.text = averageGoldPrice.toString()

            // Setup Pie Chart for Received vs Total Amount
            setupPieChart(s)

            // Setup Bar Chart for Descriptions
            setupBarChart(s)

        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupPieChart(data: List<PdfFinalData>) {
        // Calculate total received amount using regex to extract numbers
        val totalReceived = data.flatMap { pdf ->
            pdf.receivedList.mapNotNull { received ->
                try {
                    // Extract only numeric value (including minus sign if present)
                    val numericValue = Regex("-?\\d+").find(received)?.value

                    // Convert to positive integer if found, null otherwise
                    val amount = numericValue?.replace("-", "")?.toIntOrNull()

                    Log.d(
                        "ReceivedDebug",
                        "Original: $received, Extracted: $numericValue, Final: $amount"
                    )

                    amount
                } catch (e: Exception) {
                    Log.e("ReceivedDebug", "Error processing value '$received': ${e.message}")
                    null
                }
            }
        }.sum()

        Log.d("ReceivedDebug", "Total Received: $totalReceived")

        // Rest of the pie chart setup...
        val totalAmount = data.map { it.totalAmount }.sum()

        val pieEntries = listOf(
            PieEntry(totalReceived.toFloat(), "Received (₹$totalReceived)"),
            PieEntry(totalAmount.toFloat(), "Pending (₹$totalAmount)")
        )

        val pieDataSet = PieDataSet(pieEntries, "Payment Status")
        pieDataSet.colors = listOf(
            Color.rgb(76, 175, 80),
            Color.rgb(244, 67, 54)
        )
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)

        binding.pieChart.apply {
            setData(pieData)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            centerText = "Payment Status"
            setCenterTextColor(Color.BLACK)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.isEnabled = true
            invalidate()
        }
    }

    private fun setupBarChart(data: List<PdfFinalData>) {
        // Get description counts
        val descriptionCounts = data.flatMap { pdf ->
            pdf.descriptionList
        }.groupingBy { it }.eachCount()

        // Create bar entries
        val barEntries = descriptionCounts.map { (description, count) ->
            BarEntry(descriptionCounts.keys.indexOf(description).toFloat(), count.toFloat())
        }

        // Create and customize BarDataSet
        val barDataSet = BarDataSet(barEntries, "Items by Category")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 12f

        // Create BarData
        val barData = BarData(barDataSet)

        // Customize X-axis
        val xAxis = binding.barChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(descriptionCounts.keys.toList())
            granularity = 1f
            labelRotationAngle = 45f
        }

        // Customize bar chart
        binding.barChart.apply {
            setData(barData)  // Fixed: Use setData instead of just referencing barData
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
            animateY(1000)

            // Additional customizations for better visibility
            axisLeft.axisMinimum = 0f  // Start Y-axis at 0
            axisRight.isEnabled = false  // Disable right Y-axis
            setPinchZoom(false)  // Disable pinch zoom
            setScaleEnabled(false)  // Disable scaling

            invalidate()
        }
    }
}