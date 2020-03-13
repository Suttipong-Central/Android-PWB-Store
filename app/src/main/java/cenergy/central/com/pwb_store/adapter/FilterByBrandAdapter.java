package cenergy.central.com.pwb_store.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.adapter.viewholder.CancelFilterViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.FilterByBrandViewHolder;
import cenergy.central.com.pwb_store.model.FilterItem;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;

public class FilterByBrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ID_CANCEL_FILTER = -1;
    private static final int VIEW_TYPE_ID_FILTER_ITEM = 1;

    private OnBrandFilterClickListener listener;
    private List<FilterItem> filterItems = new ArrayList<>();
    private List<IViewType> mListViewType = new ArrayList<>();


    public FilterByBrandAdapter(OnBrandFilterClickListener listener) {
        this.listener = listener;
    }

    public void setBrandForFilter(List<FilterItem> filterItems) {
        this.filterItems = filterItems;
        mListViewType.clear();
        mListViewType.add(new ViewType(VIEW_TYPE_ID_CANCEL_FILTER)); // add menu clear filter

        for (FilterItem item : this.filterItems) {
            item.setViewTypeId(VIEW_TYPE_ID_FILTER_ITEM);
            mListViewType.add(item);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_CANCEL_FILTER:
                return new CancelFilterViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_ITEM:
                return new FilterByBrandViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.list_item_product_filter_header, parent, false), listener);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_CANCEL_FILTER: {
                if (holder instanceof CancelFilterViewHolder) {
                    ((CancelFilterViewHolder) holder).itemView.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onClickedItem(null);
                        }
                    });
                }
                break;
            }
            case VIEW_TYPE_ID_FILTER_ITEM:
                if (viewType instanceof FilterItem && holder instanceof FilterByBrandViewHolder) {
                    FilterItem filterItem = (FilterItem) viewType;
                    ((FilterByBrandViewHolder) holder).setViewHolder(filterItem);
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

    public void updateSingleBrandFilterItem(FilterItem filterItem) {
        for (FilterItem item : filterItems) {
            if (filterItem != null) {
                item.setSelected(item.getValue().equals(filterItem.getValue()));
            } else  {
                item.setSelected(false);
            }
        }
        setBrandForFilter(filterItems); // notify
    }
}
