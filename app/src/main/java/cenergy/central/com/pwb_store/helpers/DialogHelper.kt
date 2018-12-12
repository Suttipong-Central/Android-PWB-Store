package cenergy.central.com.pwb_store.helpers

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import cenergy.central.com.pwb_store.R

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
}