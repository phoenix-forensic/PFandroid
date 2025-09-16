package net.osmtracker.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.osmtracker.dashboard.analytics.AnalyticsDashboardFragment
import net.osmtracker.dashboard.defense.DefenseControlCenterFragment
import net.osmtracker.dashboard.dns.DnsFirewallImportFragment
import net.osmtracker.dashboard.dns.DnsFirewallLogsFragment
import net.osmtracker.dashboard.gps.GpsAntifragilFragment
import net.osmtracker.dashboard.tracker.TrackerModuleFragment

class DashboardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AnalyticsDashboardFragment()
            1 -> DnsFirewallImportFragment()
            2 -> DnsFirewallLogsFragment()
            3 -> TrackerModuleFragment()
            4 -> DefenseControlCenterFragment()
            else -> GpsAntifragilFragment()
        }
    }
}
