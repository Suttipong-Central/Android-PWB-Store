package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.APIError

fun Activity.showCommonDialog(message: String) {
    showCommonDialog(null, message)
}

fun Activity.showCommonDialog(title: String?, message: String,
                                       onClick: DialogInterface.OnClickListener? = null) {
    if (isDestroyed || isFinishing) return

    AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), onClick)
            .show()
}

fun Activity.showCommonDialog(@StringRes title: Int, @StringRes message: Int,
                                       onClick: DialogInterface.OnClickListener? = null) {
    showCommonDialog(getString(title), getString(message), onClick)
}

fun Activity.showCommonDialog(@StringRes message: Int) {
    showCommonDialog(null, getString(message))
}

fun Activity.showCommonAPIErrorDialog(error: APIError){
    if (error.errorCode == null) {
        showCommonDialog(getString(R.string.not_connected_network))
    } else {
        when (error.errorCode) {
            "401" -> showCommonDialog(getString(R.string.user_not_found))
            "408", "404", "500" -> showCommonDialog(getString(R.string.server_not_found))
            else -> showCommonDialog(getString(R.string.some_thing_wrong))
        }
    }
}