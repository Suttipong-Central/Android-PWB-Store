package cenergy.central.com.pwb_store.utils

import android.app.Activity
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import cenergy.central.com.pwb_store.R

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
