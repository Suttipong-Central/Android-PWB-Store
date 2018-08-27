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

public class FilterByBrandAdapter extends RecyclerView.Adapter<FilterByBrandViewHolder> {

    private OnBrandFilterClickListener listener;
    private List<Brand> brands;

    public FilterByBrandAdapter(OnBrandFilterClickListener listener) {
        this.listener = listener;
    }

    public void setBrandForFilter(List<Brand> brands) {
        this.brands = brands;
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
        final Brand brand = brands.get(position);
        holder.setViewHolder(brand);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickedItem(brand);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }
}
