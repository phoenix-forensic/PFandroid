package net.osmtracker.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.socket.client.IO
import io.socket.client.Socket
import net.osmtracker.R
import net.osmtracker.dashboard.events.NovoEvento
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.net.URISyntaxException

class DashboardActivity : AppCompatActivity() {

    private var socket: Socket? = null
    private var fallbackThread: Thread? = null

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

        initRealtime()
    }

    override fun onStart() {
        super.onStart()
        socket?.connect()
    }

    override fun onStop() {
        super.onStop()
        socket?.disconnect()
        stopFallbackPolling()
    }

    override fun onDestroy() {
        socket?.off()
        socket = null
        stopFallbackPolling()
        super.onDestroy()
    }

    private fun initRealtime() {
        val url = getString(R.string.socket_url)
        val options = IO.Options().apply {
            reconnection = true
            reconnectionAttempts = Int.MAX_VALUE
            reconnectionDelay = 500
            reconnectionDelayMax = 5_000
            timeout = 10_000
            transports = arrayOf("websocket", "polling")
        }

        socket = try {
            IO.socket(url, options)
        } catch (error: URISyntaxException) {
            Log.w(TAG, "Invalid realtime endpoint: $url", error)
            startFallbackPolling()
            null
        }

        socket?.on(Socket.EVENT_CONNECT) {
            stopFallbackPolling()
        }?.on(Socket.EVENT_RECONNECT) {
            stopFallbackPolling()
        }?.on(Socket.EVENT_DISCONNECT) {
            handleSocketDisconnection("disconnect")
        }?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            logSocketIssue("connect_error", args)
            handleSocketDisconnection("connect_error")
        }?.on(Socket.EVENT_CONNECT_TIMEOUT) { args ->
            logSocketIssue("connect_timeout", args)
            handleSocketDisconnection("connect_timeout")
        }?.on(EVENT_NOVO_EVENTO) { args ->
            if (args.isEmpty()) return@on
            val payload = args[0] as? JSONObject ?: return@on
            val latitude = payload.optDouble("lat", 0.0)
            val longitude = payload.optDouble("lon", 0.0)
            val risk = payload.optString("risk", "low")

            runOnUiThread {
                EventBus.getDefault().post(NovoEvento(latitude, longitude, risk))
            }
        }
    }

    private fun handleSocketDisconnection(origin: String) {
        startFallbackPolling()
        Log.d(TAG, "Realtime socket disconnected: $origin")
    }

    private fun logSocketIssue(event: String, args: Array<Any>) {
        if (args.isNotEmpty()) {
            Log.d(TAG, "Realtime socket issue ($event): ${args[0]}")
        } else {
            Log.d(TAG, "Realtime socket issue ($event)")
        }
    }

    private fun startFallbackPolling() {
        if (fallbackThread?.isAlive == true) return
        fallbackThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(FALLBACK_INTERVAL_MS)
                    // TODO: Replace with a lightweight polling call if the realtime channel is unavailable.
                } catch (interruption: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }.apply {
            name = "RealtimeFallbackPoller"
            isDaemon = true
            start()
        }
    }

    private fun stopFallbackPolling() {
        fallbackThread?.interrupt()
        fallbackThread = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val TAG = "DashboardActivity"
        private const val FALLBACK_INTERVAL_MS = 30_000L
        private const val EVENT_NOVO_EVENTO = "novo_evento"
    }
}
