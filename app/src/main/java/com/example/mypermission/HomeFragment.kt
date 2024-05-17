package com.example.mypermission

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var rvUsageStats: RecyclerView
    private lateinit var rvMaxUtilizingApps: RecyclerView
    private lateinit var usageStatsAdapter: UsageStatsAdapter
    private lateinit var maxUtilizingAppsAdapter: UsageStatsAdapter
    private lateinit var pieChart: PieChart
    private var isScrolling = false
    private val THRESHOLD = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        pieChart = view.findViewById(R.id.pieChart)
        rvUsageStats = view.findViewById(R.id.rvUsageStats)
        rvMaxUtilizingApps = view.findViewById(R.id.rvMaxUtilizingApps)
        val usageStatsService = UsageStatsService(requireContext())
        val usageStatsMap = usageStatsService.getUsageStatsForSocialMediaApps()
        val sortedStats = usageStatsMap.entries.sortedByDescending { (_, value) -> value }.associate { it.toPair() }

        usageStatsAdapter = UsageStatsAdapter(usageStatsMap)
        rvUsageStats.adapter = usageStatsAdapter
        rvUsageStats.layoutManager = LinearLayoutManager(requireContext())

        maxUtilizingAppsAdapter = UsageStatsAdapter(sortedStats)
        rvMaxUtilizingApps.adapter = maxUtilizingAppsAdapter
        rvMaxUtilizingApps.layoutManager = LinearLayoutManager(requireContext())

        val entries = ArrayList<PieEntry>()
        var index = 0f
        for ((appName, usageTime) in usageStatsMap) {
            entries.add(PieEntry(usageTime.toFloat(), appName))
        }
        val dataSet = PieDataSet(entries, "App Usage")

        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        val pieData = PieData(dataSet)

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



        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvUsageStats.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (isScrolling && (visibleItemCount + firstVisibleItemPosition >= totalItemCount - THRESHOLD)) {
                    isScrolling = false
                    displayMaxUtilizingApps()
                }
            }
        })

        return view
    }

    private fun displayMaxUtilizingApps() {
        val usageStatsService = UsageStatsService(requireContext())
        val usageStatsMap = usageStatsService.getUsageStatsForSocialMediaApps()
        val sortedStats = usageStatsMap.entries.sortedByDescending { (_, value) -> value }.associate { it.toPair() }
        maxUtilizingAppsAdapter.updateUsageStats(sortedStats)
    }
}

// UsageStatsService class remains the same
class UsageStatsService(private val context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun getUsageStatsForSocialMediaApps(): Map<String, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            .filter { isSocialMediaApp(it.packageName) }
            .groupBy { it.packageName }
            .mapValues { (_, stats) -> stats.sumOf { it.totalTimeInForeground } }

        return usageStats
    }

    private fun isSocialMediaApp(packageName: String): Boolean {
        val socialMediaApps = listOf(
            "com.facebook.katana",
            "com.twitter.android",
            "com.instagram.android",
            "com.whatsapp",
            "com.snapchat.android"
        )
        return socialMediaApps.contains(packageName)
    }
}

class UsageStatsAdapter(private var usageStats: Map<String, Long>) :
    RecyclerView.Adapter<UsageStatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usage_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usageStats.entries.elementAt(position))
    }

    override fun getItemCount() = usageStats.size

    fun updateUsageStats(newUsageStats: Map<String, Long>) {
        usageStats = newUsageStats
        notifyDataSetChanged() // Notify RecyclerView that the dataset has changed
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvUsageTime: TextView = itemView.findViewById(R.id.tvUsageTime)

        fun bind(entry: Map.Entry<String, Long>) {
            tvAppName.text = getAppName(entry.key)
            tvUsageTime.text = formatUsageTime(entry.value)
        }

        private fun getAppName(packageName: String): String {
            val packageManager = itemView.context.packageManager
            return try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName
            }
        }

        private fun formatUsageTime(usageTimeMillis: Long): String {
            val seconds = (usageTimeMillis / 1000) % 60
            val minutes = (usageTimeMillis / (1000 * 60) % 60)
            val hours = (usageTimeMillis / (1000 * 60 * 60) % 24)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}