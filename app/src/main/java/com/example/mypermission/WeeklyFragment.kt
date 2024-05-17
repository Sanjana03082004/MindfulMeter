package com.example.mypermission

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.ArrayList

class WeeklyFragment : Fragment() {
    private lateinit var rvUsageStats: RecyclerView
    private lateinit var rvMaxUtilizingApps: RecyclerView
    private lateinit var weeklyUsageStatsAdapter: WeeklyUsageStatsAdapter
    private lateinit var maxUtilizingAppsAdapter: WeeklyUsageStatsAdapter
    private lateinit var lineChart: LineChart
    private var isScrolling = false
    private val THRESHOLD = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weekly, container, false)
        lineChart = view.findViewById(R.id.linechart)
        rvUsageStats = view.findViewById(R.id.rvUsageStats)
        rvMaxUtilizingApps = view.findViewById(R.id.rvMaxUtilizingApps)

        val weeklyUsageStatsService = WeeklyUsageStatsService(requireContext())
        val usageStatsMap = weeklyUsageStatsService.getUsageStatsForSocialMediaApps()
        val sortedStats = usageStatsMap.entries.sortedByDescending { (_, value) -> value }.associate { it.toPair() }

        weeklyUsageStatsAdapter = WeeklyUsageStatsAdapter(usageStatsMap)
        rvUsageStats.adapter = weeklyUsageStatsAdapter
        rvUsageStats.layoutManager = LinearLayoutManager(requireContext())

        maxUtilizingAppsAdapter = WeeklyUsageStatsAdapter(sortedStats)
        rvMaxUtilizingApps.adapter = maxUtilizingAppsAdapter
        rvMaxUtilizingApps.layoutManager = LinearLayoutManager(requireContext())

        // Line Chart Configuration
        val lineEntries = ArrayList<Entry>()
        var index = 0f
        for ((_, usageTime) in usageStatsMap) {
            lineEntries.add(Entry(index++, usageTime.toFloat()))
        }
        val lineDataSet = LineDataSet(lineEntries, "App Usage")
        lineDataSet.color = ColorTemplate.VORDIPLOM_COLORS[0]
        lineDataSet.valueTextColor = ColorTemplate.VORDIPLOM_COLORS[0]
        lineDataSet.lineWidth = 3f
        val lineData = LineData(lineDataSet)

        lineChart.apply {
            data = lineData
            setDrawGridBackground(false)
            description.isEnabled = false
            legend.isEnabled = false
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
        val weeklyUsageStatsService = WeeklyUsageStatsService(requireContext())
        val usageStatsMap = weeklyUsageStatsService.getUsageStatsForSocialMediaApps()
        val sortedStats = usageStatsMap.entries.sortedByDescending { it.value }.associate { it.toPair() }
        maxUtilizingAppsAdapter.updateUsageStats(sortedStats)
    }
}

class WeeklyUsageStatsService(private val context: Context) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun getUsageStatsForSocialMediaApps(): Map<String, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7) // Adjust to one week ago
        val startTime = cal.timeInMillis
        val endTime = System.currentTimeMillis()

        return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime)
            .filter { isSocialMediaApp(it.packageName) }
            .groupBy { it.packageName }
            .mapValues { (_, stats) -> stats.sumOf { it.totalTimeInForeground } }
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

class WeeklyUsageStatsAdapter(private var usageStats: Map<String, Long>) :
    RecyclerView.Adapter<WeeklyUsageStatsAdapter.ViewHolder>() {

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
        notifyDataSetChanged()
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
