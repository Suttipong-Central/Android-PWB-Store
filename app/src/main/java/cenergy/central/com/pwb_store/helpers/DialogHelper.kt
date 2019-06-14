package cenergy.central.com.pwb_store.helpers

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.APIError

class DialogHelper(var context: Context) {

    fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    fun showErrorDialog(error: APIError) {
        if (error.errorCode == null) {
            showAlertDialog("", context.getString(R.string.not_connected_network))
        } else {
            when (error.errorCode) {
                "408", "404", "500" -> showAlertDialog("", context.getString(R.string.server_not_found))
                else -> showAlertDialog("", context.getString(R.string.some_thing_wrong))
            }
        }
    }

    fun showErrorLoginDialog(error: APIError){
        if (error.errorCode == null) {
            showAlertDialog("", context.getString(R.string.not_connected_network))
        } else {
            when (error.errorCode) {
                "401" -> showAlertDialog("", context.getString(R.string.user_not_found))
                "408", "404", "500" -> showAlertDialog("", context.getString(R.string.server_not_found))
                else -> showAlertDialog("", context.getString(R.string.some_thing_wrong))
            }
        }
    }
}