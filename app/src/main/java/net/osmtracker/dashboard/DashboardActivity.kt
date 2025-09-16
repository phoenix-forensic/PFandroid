package net.osmtracker.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.osmtracker.R

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar: MaterialToolbar = findViewById(R.id.dashboardToolbar)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val pager: ViewPager2 = findViewById(R.id.dashboardPager)
        val tabs: TabLayout = findViewById(R.id.dashboardTabs)

        pager.adapter = DashboardPagerAdapter(this)

        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.dashboard_tab_analytics)
                1 -> getString(R.string.dashboard_tab_dns_import)
                2 -> getString(R.string.dashboard_tab_dns_logs)
                3 -> getString(R.string.dashboard_tab_tracker)
                4 -> getString(R.string.dashboard_tab_control_center)
                else -> getString(R.string.dashboard_tab_gps)
            }
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
