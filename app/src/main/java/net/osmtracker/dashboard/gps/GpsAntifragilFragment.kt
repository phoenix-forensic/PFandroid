package net.osmtracker.dashboard.gps

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.osmtracker.R
import net.osmtracker.dashboard.events.NovoEvento
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GpsAntifragilFragment : Fragment(R.layout.fragment_gps_antifragil) {

    private var anomaliesText: TextView? = null
    private var anomalyCount: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anomaliesText = view.findViewById(R.id.anomalies_count)
        anomalyCount = savedInstanceState?.getInt(STATE_ANOMALY_COUNT) ?: anomalyCount
        updateAnomaliesText()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        anomaliesText = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_ANOMALY_COUNT, anomalyCount)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNovoEvento(event: NovoEvento) {
        if (event.risk.equals("spoof", ignoreCase = true) || event.risk.equals("blocked", ignoreCase = true)) {
            anomalyCount += 1
            updateAnomaliesText()
            view?.animate()?.alpha(0.7f)?.setDuration(150)?.withEndAction {
                view?.animate()?.alpha(1f)?.setDuration(150)?.start()
            }?.start()
        }
    }

    private fun updateAnomaliesText() {
        val text = getString(R.string.dashboard_gps_anomalies_format, anomalyCount)
        anomaliesText?.text = text
    }

    companion object {
        private const val STATE_ANOMALY_COUNT = "state_anomaly_count"
    }
}
