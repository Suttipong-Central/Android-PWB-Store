package cenergy.central.com.pwb_store.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.adapter.viewholder.FilterByBrandViewHolder;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.model.FilterItem;

public class FilterByBrandAdapter extends RecyclerView.Adapter<FilterByBrandViewHolder> {

    private OnBrandFilterClickListener listener;
    private List<FilterItem> filterItems;

    public FilterByBrandAdapter(OnBrandFilterClickListener listener) {
        this.listener = listener;
    }

    public void setBrandForFilter(List<FilterItem> filterItems) {
        this.filterItems = filterItems;
        notifyDataSetChanged();
    }

    @Override
    public FilterByBrandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterByBrandViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item_product_filter_header, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(FilterByBrandViewHolder holder, int position) {
        final FilterItem filterItem = filterItems.get(position);
        holder.setViewHolder(filterItem.getValue());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickedItem(filterItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterItems.size();
    }
}
