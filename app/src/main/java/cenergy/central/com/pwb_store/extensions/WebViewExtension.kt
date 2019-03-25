package cenergy.central.com.pwb_store.extensions

import android.webkit.WebView

fun WebView.setupForDescription() {
    settings.apply {
        javaScriptEnabled = true
    }
}