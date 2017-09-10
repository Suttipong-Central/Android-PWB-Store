package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareHeaderDetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView)
    PowerBuyTextView header;

    public CompareHeaderDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setViewHolder(CompareDetail compareDetail){
        header.setText(compareDetail.getName());
    }
}
