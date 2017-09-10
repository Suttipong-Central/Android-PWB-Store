package cenergy.central.com.pwb_store.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareDetailBaseAdapter extends BaseAdapter {

    @BindView(R.id.textView)
    PowerBuyTextView mTextView;

    //Data member
    private Context context; //context
    private ArrayList<CompareDetail> mCompareDetails;
    private ArrayList<CompareDetailItem> mCompareDetailItems;

    //public constructor
    public CompareDetailBaseAdapter(Context context, ArrayList<CompareDetail> compareDetails, ArrayList<CompareDetailItem> compareDetailItems) {
        this.context = context;
        this.mCompareDetails = compareDetails;
        this.mCompareDetailItems = compareDetailItems;
    }

    @Override
    public int getCount() {
        return mCompareDetails.size() + mCompareDetailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mCompareDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.list_item_text_compare_detail, parent, false);
        }

        ButterKnife.bind(this, convertView);

        CompareDetail compareDetail = (CompareDetail) getItem(position);

        mTextView.setText(compareDetail.getName());

        if (compareDetail.getCompareDetailItems() != null){
            for (CompareDetailItem compareDetailItem : compareDetail.getCompareDetailItems()){
                mTextView.setText(compareDetailItem.getType());
            }
        }

        return convertView;
    }
}
