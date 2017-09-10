package cenergy.central.com.pwb_store.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;

import cenergy.central.com.pwb_store.R;

/**
 * Created by napabhat on 4/24/2017 AD.
 */

public class DialogUtils {

    private static final String TAG = "DialogUtils";

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.d(TAG, "createProgressDialog: ");
        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_loading);
        ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        progressbar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        dialog.setCancelable(false);
        return dialog;
    }
}

