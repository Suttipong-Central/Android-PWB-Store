package cenergy.central.com.pwb_store.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.viewholder.CancelFilterViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterHeaderViewHolder;
import cenergy.central.com.pwb_store.adapter.viewholder.ProductFilterItemViewHolder;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.IViewType;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ViewType;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ProductFilterAdapter";

    //Static Members
    private static final int VIEW_TYPE_ID_CANCEL_FILTER = -1;
    private static final int VIEW_TYPE_ID_FILTER_HEADER = 0;
    private static final int VIEW_TYPE_ID_FILTER_ITEM = 1;

    //Data Members
    private Context mContext;
    private List<IViewType> mListViewType = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private Category selectedFilter;

    public ProductFilterAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ID_CANCEL_FILTER:
                return new CancelFilterViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_HEADER:
                return new ProductFilterHeaderViewHolder(
                        LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.list_item_product_filter_header, parent, false)
                );
            case VIEW_TYPE_ID_FILTER_ITEM:
                return new ProductFilterItemViewHolder(
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
                    productFilterHeaderViewHolder.bindItem(category, selectedFilter);
                }
                break;
            case VIEW_TYPE_ID_FILTER_ITEM:
                if (viewType instanceof ProductFilterItem && holder instanceof ProductFilterItemViewHolder) {
                    ProductFilterItem productFilterItem = (ProductFilterItem) viewType;
                    ProductFilterItemViewHolder productFilterItemViewHolder = (ProductFilterItemViewHolder) holder;
                    productFilterItemViewHolder.setViewHolder(productFilterItem);
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
        int parentId = Integer.parseInt(category.getId());
        for (Category item : categories) {
            if (parentId == Integer.parseInt(item.getId())) {
                selectedFilter = item;
                notifyDataSetChanged();
            }
        }
    }
}