package cenergy.central.com.pwb_store.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.FilterByBrandViewHolder;

public class FilterByBrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mFilterByBrandList;

    public void setBrandForFilter(List<String> filterByBrandList) {
        this.mFilterByBrandList = filterByBrandList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterByBrandViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item_product_filter_header, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FilterByBrandViewHolder filterByBrandViewHolder = (FilterByBrandViewHolder) holder;
        filterByBrandViewHolder.setViewHolder(mFilterByBrandList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFilterByBrandList.size();
    }
}
