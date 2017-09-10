package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.CompareItemDetailViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SpecAddCompareViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SpecDetailViewHolder;
import cenergy.central.com.pwb_store.model.CompareDao;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.SpecDao;
import cenergy.central.com.pwb_store.model.SpecItem;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class SpecDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Static Members
    private static final int VIEW_TYPE_ID_SPEC_HEADER = 0;
    private static final int VIEW_TYPE_ID_SPEC_ITEM = 1;

    private static final ViewType VIEW_TYPE_SPEC_HEADER = new ViewType(VIEW_TYPE_ID_SPEC_HEADER);

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private final GridLayoutManager.SpanSizeLookup mSpanSize = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ID_SPEC_HEADER:
                    return 4;
                case VIEW_TYPE_ID_SPEC_ITEM:
                    return 4;
                default:
                    return 1;
            }
        }
    };

    public SpecDetailAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_SPEC_HEADER:
                return new SpecAddCompareViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_spec_add, parent, false)
                );
            case VIEW_TYPE_ID_SPEC_ITEM:
                return new SpecDetailViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_spec_text_detail, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_SPEC_HEADER:
                if (holder instanceof SpecAddCompareViewHolder) {
                    SpecAddCompareViewHolder specAddCompareViewHolder = (SpecAddCompareViewHolder) holder;
                    specAddCompareViewHolder.setViewHolder();
                }
                break;

            case VIEW_TYPE_ID_SPEC_ITEM:
                if (viewType instanceof SpecItem && holder instanceof SpecDetailViewHolder) {
                    SpecItem specItem = (SpecItem) viewType;
                    SpecDetailViewHolder specDetailViewHolder = (SpecDetailViewHolder) holder;
                    specDetailViewHolder.setViewHolder(specItem);
                }
                break;
        }
    }

    public int getItemCount() {
        return mListViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }

    public void setSpecDetail(SpecDao specDao){
        mListViewType.add(VIEW_TYPE_SPEC_HEADER);
        for (SpecItem specItem : specDao.getSpecItems()){
            specItem.setViewTypeId(VIEW_TYPE_ID_SPEC_ITEM);
            mListViewType.add(specItem);
        }

        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSize() {
        return mSpanSize;
    }
}
