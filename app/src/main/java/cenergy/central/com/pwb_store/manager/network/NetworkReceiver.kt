package cenergy.central.com.pwb_store.manager.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import cenergy.central.com.pwb_store.activity.BaseActivity
import cenergy.central.com.pwb_store.activity.LoginActivity
import cenergy.central.com.pwb_store.utils.networkState

class NetworkReceiver(private val listener: NetworkStateLister? = null) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            this.listener?.onNetworkStateChange(context.networkState())
        }
    }

    interface NetworkStateLister {
        fun onNetworkStateChange(state: NetworkInfo.State)
    }
}