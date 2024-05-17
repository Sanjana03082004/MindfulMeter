package com.example.mypermission

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Suppress("DEPRECATION")
class MainActivity4 : AppCompatActivity() {

    private lateinit var rvUsageStats: RecyclerView
    private lateinit var usageStatsAdapter: UsageStatsAdapter
    private lateinit var pieChart: PieChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)

        pieChart = findViewById(R.id.pieChart)
        rvUsageStats = findViewById(R.id.rvUsageStats)
        val usageStatsService = UsageStatsService(this)

        val usageStatsMap = usageStatsService.getUsageStatsForSocialMediaApps()
        val entries = ArrayList<PieEntry>()
        var index = 0f
        for ((appName, usageTime) in usageStatsMap) {
            entries.add(PieEntry(usageTime.toFloat(), appName))
        }
        val dataSet = PieDataSet(entries, "App Usage")


        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        // Create a PieData object
        val pieData = PieData(dataSet)

        // Customize your chart
        pieChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)
            setDrawEntryLabels(false)
            setHoleColor(android.R.color.transparent)
            setTransparentCircleColor(android.R.color.transparent)
            holeRadius = 70f
            transparentCircleRadius = 0f
            setDrawCenterText(true)
            setCenterText("Social Media\nApp Usage")
            animateY(1000)
        }

        usageStatsAdapter = UsageStatsAdapter(usageStatsMap)
        rvUsageStats.adapter = usageStatsAdapter
        rvUsageStats.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

