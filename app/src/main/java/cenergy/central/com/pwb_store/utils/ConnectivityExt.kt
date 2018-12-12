package cenergy.central.com.pwb_store.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

internal fun Context?.networkState(): NetworkInfo.State {
    if (this == null) return NetworkInfo.State.DISCONNECTED
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    if (activeNetwork != null) {
        return activeNetwork.state
    }
    return NetworkInfo.State.DISCONNECTED
}