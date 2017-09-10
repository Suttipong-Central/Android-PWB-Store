package cenergy.central.com.pwb_store.adapter.viewholder;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }
}
