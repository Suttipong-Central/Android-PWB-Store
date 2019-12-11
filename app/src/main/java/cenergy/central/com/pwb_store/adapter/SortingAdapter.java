package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.SortingHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.SortingItemViewHolder;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.model.SortingList;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ProductFilterAdapter";

    //Static Members
    private static final int VIEW_TYPE_ID_FILTER_HEADER = 0;
    private static final int VIEW_TYPE_ID_FILTER_ITEM = 1;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private List<SortingHeader> mSortingHeaders;

    public SortingAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_FILTER_HEADER:
                return new SortingHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_ITEM:
                return new SortingItemViewHolder(
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
                if (viewType instanceof SortingHeader && holder instanceof SortingHeaderViewHolder) {
                    SortingHeader sortingHeader = (SortingHeader) viewType;
                    SortingHeaderViewHolder sortingHeaderViewHolder = (SortingHeaderViewHolder) holder;
                    sortingHeaderViewHolder.setViewHolder(sortingHeader);
                }
                break;
            case VIEW_TYPE_ID_FILTER_ITEM:
                if (viewType instanceof SortingItem && holder instanceof SortingItemViewHolder) {
                    SortingItem sortingItem = (SortingItem) viewType;
                    SortingItemViewHolder sortingItemViewHolder = (SortingItemViewHolder) holder;
                    sortingItemViewHolder.setViewHolder(sortingItem);
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


    public void setSorting(SortingList sorting) {
        this.mSortingHeaders = sorting.getSortingHeaders();
    }

    public void addSortLevel2(List<SortingItem> sortingItemList) {

        for (SortingItem sortingItem :
                sortingItemList) {
            sortingItem.setParentId("0");
            sortingItem.setViewTypeId(VIEW_TYPE_ID_FILTER_ITEM);
            mListViewType.add(sortingItem);
        }
        notifyDataSetChanged();
    }

    public void updateSingleProductFilterItem(SortingItem sortingItem) {
        for (SortingHeader sortingHeader : mSortingHeaders) {
            if (!sortingHeader.isMultipleType()) {
                if (sortingHeader.isSortingItemListAvailable()) {
                    List<SortingItem> sortingItemList = sortingHeader.getSortingItems();
                    for (SortingItem headerSortingItem :
                            sortingItemList) {
                        Log.d(TAG, "loop : " + headerSortingItem.getFilterName());
                        headerSortingItem.setSelected(headerSortingItem.getFilterName().equalsIgnoreCase(sortingItem.getFilterName()));
                        Log.d(TAG, "bus : " + sortingItem.getSlug());
                    }
                    notifyItemRangeChanged(0, sortingItemList.size());
                }
            }
        }

    }

    public void removeSortingLevel3(SortingList sortingList) {
        mListViewType.clear();
        for (SortingHeader sortingHeader :
                sortingList.getSortingHeaders()) {
            if (sortingHeader.getType().equalsIgnoreCase("single") ||
                    sortingHeader.getType().equalsIgnoreCase("multiple")) {
                sortingHeader.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
                mListViewType.add(sortingHeader);
            }
        }

        notifyDataSetChanged();
    }
}