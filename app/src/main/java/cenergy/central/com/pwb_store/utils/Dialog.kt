package cenergy.central.com.pwb_store.utils

import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import cenergy.central.com.pwb_store.R

fun AppCompatActivity.showCommonDialog(message: String) {
    showCommonDialog(null, message)
}

fun AppCompatActivity.showCommonDialog(title: String?, message: String,
                                       onClick: DialogInterface.OnClickListener? = null) {
    if (isDestroyed || isFinishing) return
    AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), onClick)
            .show()
}

fun AppCompatActivity.showCommonDialog(@StringRes title: Int, @StringRes message: Int,
                                       onClick: DialogInterface.OnClickListener? = null) {
    showCommonDialog(getString(title), getString(message), onClick)
}

fun AppCompatActivity.showCommonDialog(@StringRes message: Int) {
    showCommonDialog(null, getString(message))
}
