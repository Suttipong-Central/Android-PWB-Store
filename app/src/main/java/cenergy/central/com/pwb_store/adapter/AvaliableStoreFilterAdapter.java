package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableFilterHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableFilterItemViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterItemViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterList;
import cenergy.central.com.pwb_store.model.StoreFilterHeader;
import cenergy.central.com.pwb_store.model.StoreFilterItem;
import cenergy.central.com.pwb_store.model.StoreFilterList;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableStoreFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AvaliableStoreFilterAdapter";

    //Static Members
    private static final int VIEW_TYPE_ID_FILTER_HEADER = 0;
    private static final int VIEW_TYPE_ID_FILTER_ITEM = 1;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();

    public AvaliableStoreFilterAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_FILTER_HEADER:
                return new AvaliableFilterHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_ITEM:
                return new AvaliableFilterItemViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_item, parent, false)
                );

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_FILTER_HEADER:
                if (viewType instanceof StoreFilterHeader && holder instanceof AvaliableFilterHeaderViewHolder) {
                    StoreFilterHeader storeFilterHeader = (StoreFilterHeader) viewType;
                    AvaliableFilterHeaderViewHolder avaliableFilterHeaderViewHolder = (AvaliableFilterHeaderViewHolder) holder;
                    avaliableFilterHeaderViewHolder.setViewHolder(storeFilterHeader);
                }
                break;
            case VIEW_TYPE_ID_FILTER_ITEM:
                if (viewType instanceof StoreFilterItem && holder instanceof AvaliableFilterItemViewHolder) {
                    StoreFilterItem storeFilterItem = (StoreFilterItem) viewType;
                    AvaliableFilterItemViewHolder avaliableFilterItemViewHolder = (AvaliableFilterItemViewHolder) holder;
                    avaliableFilterItemViewHolder.setViewHolder(storeFilterItem);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListViewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListViewType.get(position).getViewTypeId();
    }


    public void setStoreFilter(StoreFilterList storeFilterList) {
        mListViewType.clear();
        for (StoreFilterHeader storeFilterHeader :
                storeFilterList.getStoreFilterHeaders()) {
            if (storeFilterHeader.getType().equalsIgnoreCase("single") ||
                    storeFilterHeader.getType().equalsIgnoreCase("multiple")) {
                storeFilterHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
                mListViewType.add(storeFilterHeader);
            }
        }

        notifyDataSetChanged();
    }
    public void addStoreLevel2(StoreFilterHeader storeFilterHeader) {
        List<StoreFilterItem> storeFilterItemList = storeFilterHeader.getStoreFilterItemList();
        mListViewType.clear();
        storeFilterHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
        mListViewType.add(storeFilterHeader);

        for (StoreFilterItem storeFilterItem :
                storeFilterHeader.getStoreFilterItemList()) {
            storeFilterItem.setParentId(storeFilterHeader.getId());
            storeFilterItem.setViewTypeId(VIEW_TYPE_ID_FILTER_ITEM);
        }

        int positionAdded = -1;
        for (int i = 0; i < mListViewType.size(); i++) {
            if (mListViewType.get(i) instanceof StoreFilterHeader) {
                StoreFilterHeader filterHeader = (StoreFilterHeader) mListViewType.get(i);
                if (filterHeader.getId().equalsIgnoreCase(storeFilterHeader.getId())) {
                    mListViewType.addAll(i + 1, storeFilterItemList);
                    positionAdded = i;
                    break;
                }
            }
        }

        if (positionAdded != -1) {
            notifyItemRangeInserted(positionAdded + 1, storeFilterItemList.size());
        }

        notifyDataSetChanged();
    }
    public void removeStoreLevel3(StoreFilterList storeFilterList) {
        mListViewType.clear();
        for (StoreFilterHeader storeFilterHeader :
                storeFilterList.getStoreFilterHeaders()) {
            if (storeFilterHeader.getType().equalsIgnoreCase("single") ||
                    storeFilterHeader.getType().equalsIgnoreCase("multiple")) {
                storeFilterHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
                mListViewType.add(storeFilterHeader);
            }
        }

        notifyDataSetChanged();
    }
}