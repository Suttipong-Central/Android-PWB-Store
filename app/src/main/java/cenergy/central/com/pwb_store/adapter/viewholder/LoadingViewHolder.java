package cenergy.central.com.pwb_store.adapter.viewholder;

import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import cenergy.central.com.pwb_store.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public LoadingViewHolder(View itemView) {
        super(itemView);
        ProgressBar mProgressBar = itemView.findViewById(R.id.progress_bar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }
}
