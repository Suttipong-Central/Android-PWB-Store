package cenergy.central.com.pwb_store.adapter;

import android.support.v7.widget.RecyclerView;

import cenergy.central.com.pwb_store.view.PowerBuyListDialog;

/**
 * Created by napabhat on 3/13/2017 AD.
 */

public abstract class ListDialogAbstractItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected PowerBuyListDialog.OnItemClickListener mListener;

    public PowerBuyListDialog.OnItemClickListener getListener() {
        return mListener;
    }

    public void setListener(PowerBuyListDialog.OnItemClickListener listener) {
        this.mListener = listener;
    }
}