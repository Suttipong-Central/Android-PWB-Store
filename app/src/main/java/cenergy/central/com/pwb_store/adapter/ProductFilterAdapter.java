package cenergy.central.com.pwb_store.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CancelFilterViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterHeaderViewHolder;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ViewType;

public class ProductFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Static Members
    private static final int VIEW_TYPE_ID_CANCEL_FILTER = -1;
    private static final int VIEW_TYPE_ID_FILTER_HEADER = 0;

    //Data Members
    private List<IViewType> mListViewType = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_CANCEL_FILTER:
                return new CancelFilterViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            default:
                return new ProductFilterHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewTypeId = getItemViewType(position);
        IViewType viewType = mListViewType.get(position);
        switch (viewTypeId) {
            case VIEW_TYPE_ID_CANCEL_FILTER: {
                if (holder instanceof CancelFilterViewHolder) {
                    ((CancelFilterViewHolder) holder).bind();
                }
                break;
            }
            case VIEW_TYPE_ID_FILTER_HEADER:
                if (viewType instanceof Category && holder instanceof ProductFilterHeaderViewHolder) {
                    Category category = (Category) viewType;
                    ProductFilterHeaderViewHolder productFilterHeaderViewHolder = (ProductFilterHeaderViewHolder) holder;
                    productFilterHeaderViewHolder.bindItem(category);
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

    public void setProductFilter(ArrayList<Category> categories) {
        this.categories = categories;
        mListViewType.clear();

        mListViewType.add(new ViewType(VIEW_TYPE_ID_CANCEL_FILTER)); // add menu clear filter
        for (Category category : categories) {
            category.setViewTypeId(VIEW_TYPE_ID_FILTER_HEADER);
            mListViewType.add(category);
        }
        notifyDataSetChanged();
    }

    public void updateSingleProductFilterItem(Category category) {
        for (Category item : categories) {
            if (category != null){
                item.setIsSelected(item.getId().equals(category.getId()));
            } else {
                item.setIsSelected(false);
            }
        }
        setProductFilter(categories);
    }
}