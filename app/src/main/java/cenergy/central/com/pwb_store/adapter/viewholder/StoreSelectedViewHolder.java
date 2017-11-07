package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.StoreList;
import cenergy.central.com.pwb_store.view.PowerBuyListDialog;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 9/5/2017 AD.
 */

public class StoreSelectedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.radio_button)
    RadioButton mRadioButton;

    @BindView(R.id.txt_store_name)
    PowerBuyTextView mStoreName;

    private PowerBuyListDialog.OnItemClickListener mListener;

    public StoreSelectedViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(StoreList storeList, PowerBuyListDialog.OnItemClickListener listener){
        this.mListener = listener;
        mStoreName.setText(storeList.getStoreName());
        mRadioButton.setChecked(storeList.isSelected() ? true : false);
        itemView.setTag(storeList);
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (mListener != null) {
            StoreList storeList = (StoreList) itemView.getTag();
            storeList.setSelected(!storeList.isSelected());
            mRadioButton.setChecked(storeList.isSelected() ? true : false);
            mListener.onItemClick(itemView.getTag());
        }
    }
}
