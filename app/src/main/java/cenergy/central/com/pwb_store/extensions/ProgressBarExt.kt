package cenergy.central.com.pwb_store.extensions

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.show() {
    this.visibility = View.VISIBLE
}

fun ProgressBar.dismiss() {
    this.visibility = View.GONE
}