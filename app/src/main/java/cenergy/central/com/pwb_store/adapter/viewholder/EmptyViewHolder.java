package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cenergy.central.com.pwb_store.R;

/**
 * Created by napabhat on 8/10/2017 AD.
 */

public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    public void setViewHolder() {
        TextView textView = itemView.findViewById(R.id.txt_result);
        textView.setText(itemView.getContext().getString(R.string.not_found_data));
    }
}
